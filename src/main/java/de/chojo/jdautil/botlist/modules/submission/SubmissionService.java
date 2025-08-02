/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist.modules.submission;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.chojo.jdautil.botlist.BotListData;
import de.chojo.jdautil.botlist.BotlistService;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class SubmissionService {
    private static final Logger log = getLogger(SubmissionService.class);
    private final BotlistService botlistService;
    private final ShardManager shardManager;

    public SubmissionService(BotlistService botlistService, ShardManager shardManager) {
        this.botlistService = botlistService;
        this.shardManager = shardManager;
    }

    public void submitData() {
        var shardData = BotListData.ofShards(shardManager);
        var total = BotListData.total(shardManager);
        for (var botlist : botlistService.botlists()) {
            if (!botlist.isShardStats()) {
                try {
                    botlist.report(total);
                } catch (JsonProcessingException e) {
                    log.error("Could not build stats for {}.", botlist.name(), e);
                } catch (RuntimeException e) {
                    log.error("Could not send stats for {}.", botlist.name(), e);
                }
                continue;
            }
            for (var data : shardData) {
                try {
                    botlist.report(data);
                } catch (JsonProcessingException e) {
                    log.error("Could not build stats for {}.", botlist.name(), e);
                } catch (RuntimeException e) {
                    log.error("Could not send stats for {}.", botlist.name(), e);
                }
            }
        }
    }
}
