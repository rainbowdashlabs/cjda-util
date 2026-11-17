/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist.modules.submission;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.chojo.jdautil.botlist.BotList;
import de.chojo.jdautil.botlist.BotListData;
import de.chojo.jdautil.botlist.BotlistService;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.Logger;

import java.util.List;
import java.util.function.Supplier;

import static org.slf4j.LoggerFactory.getLogger;

public class SubmissionService {
    private static final Logger log = getLogger(SubmissionService.class);
    private final BotlistService botlistService;
    private final ShardManager shardManager;
    private final Supplier<Integer> guildCount;
    private final Supplier<Integer> userCount;

    public SubmissionService(
            BotlistService botlistService,
            ShardManager shardManager,
            Supplier<Integer> guildCount,
            Supplier<Integer> userCount) {
        this.botlistService = botlistService;
        this.shardManager = shardManager;
        this.guildCount = guildCount;
        this.userCount = userCount;
    }

    public void submitData() {
        var total = BotListData.total(guildCount.get(), userCount.get(), shardManager.getShardsTotal());
        var shards = botlistService.botlists().stream().anyMatch(BotList::isShardStats)
                ? BotListData.shards(shardManager)
                : List.<BotListData>of();
        for (var botlist : botlistService.botlists()) {
            try {
                botlist.report(total, shards);
            } catch (JsonProcessingException e) {
                log.error("Could not build stats for {}.", botlist.name(), e);
            } catch (RuntimeException e) {
                log.error("Could not send stats for {}.", botlist.name(), e);
            }
        }
    }
}
