/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist.modules.voting.poll;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

/**
 * Response of discordbotlist.com's upvote list. It holds the most recent 500 votes of the last 12 hours, so older votes
 * of a busy bot are not found.
 */
public class DiscordbotlistComUpvotes implements VotePayload {
    @JsonProperty("upvotes")
    private List<Upvote> upvotes = List.of();

    @Override
    public boolean hasVoted(User user) {
        if (upvotes == null) return false;
        return upvotes.stream().anyMatch(upvote -> upvote.userId == user.getIdLong());
    }

    private static class Upvote {
        @JsonProperty("user_id")
        private long userId;
    }
}
