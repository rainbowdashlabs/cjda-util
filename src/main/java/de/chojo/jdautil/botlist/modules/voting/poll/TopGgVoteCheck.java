/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist.modules.voting.poll;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.dv8tion.jda.api.entities.User;

import java.time.OffsetDateTime;

/**
 * Response of top.gg's vote check. The vote is active until {@code expires_at}.
 */
public class TopGgVoteCheck implements VotePayload {
    @JsonProperty("expires_at")
    private String expiresAt;

    @Override
    public boolean hasVoted(User user) {
        return expiresAt != null && OffsetDateTime.parse(expiresAt).isAfter(OffsetDateTime.now());
    }
}
