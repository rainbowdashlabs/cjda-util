/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist.modules.submission;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.chojo.jdautil.botlist.BotListData;
import de.chojo.jdautil.botlist.BotlistService;
import org.slf4j.Logger;

import java.util.function.Supplier;

import static org.slf4j.LoggerFactory.getLogger;

public class SubmissionService {
    private static final Logger log = getLogger(SubmissionService.class);
    private final BotlistService botlistService;
    private final Supplier<Integer> guildCount;
    private final Supplier<Integer> userCount;


    public SubmissionService(BotlistService botlistService, Supplier<Integer> guildCount, Supplier<Integer> userCount) {
        this.botlistService = botlistService;
        this.guildCount = guildCount;
        this.userCount = userCount;
    }

    public void submitData() {
        var total = new BotListData(guildCount.get(), userCount.get());
        for (var botlist : botlistService.botlists()) {
            try {
                botlist.report(total);
            } catch (JsonProcessingException e) {
                log.error("Could not build stats for {}.", botlist.name(), e);
            } catch (RuntimeException e) {
                log.error("Could not send stats for {}.", botlist.name(), e);
            }
            continue;
        }
    }
}
