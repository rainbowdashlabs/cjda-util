/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist.modules.voting.poll;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.dv8tion.jda.api.entities.User;

/**
 * Response of botlist.me's vote check.
 */
public class BotlistMeVoteCheck implements VotePayload {
    @JsonProperty("hasVoted")
    private boolean hasVoted;

    @Override
    public boolean hasVoted(User user) {
        return hasVoted;
    }
}
