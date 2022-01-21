/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.chojo.jdautil.botlist.modules.voting.poll;

import net.dv8tion.jda.api.entities.User;

public interface VoteChecker {
    boolean hasVoted(User user);
}
