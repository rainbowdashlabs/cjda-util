/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist.modules.voting.poll;

import de.chojo.jdautil.botlist.BotList;
import net.dv8tion.jda.api.entities.User;

/**
 * Asks a bot list whether a user voted for the bot.
 */
@FunctionalInterface
public interface VoteChecker {
    /**
     * Checks whether the user has an active vote on the bot list.
     *
     * @param botList bot list to ask
     * @param user    user to check
     * @return true when the user has an active vote
     */
    boolean hasVoted(BotList botList, User user);
}
