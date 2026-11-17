/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist.modules.voting.poll;

import net.dv8tion.jda.api.entities.User;

/**
 * Response of a bot list's vote check endpoint.
 */
public interface VotePayload {
    /**
     * Whether the response shows an active vote of the user.
     *
     * @param user user that was checked
     * @return true when the user has an active vote
     */
    boolean hasVoted(User user);
}
