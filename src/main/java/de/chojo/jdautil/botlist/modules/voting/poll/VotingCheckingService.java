/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist.modules.voting.poll;

import de.chojo.jdautil.botlist.modules.shared.RouteProvider;
import net.dv8tion.jda.api.entities.User;

public class VotingCheckingService<T extends VotePayload> extends RouteProvider implements VoteChecker {
    public VotingCheckingService(String route, Class<T> response) {
        super(route);
    }

    @Override
    public boolean hasVoted(User user) {
        return false;
    }
}
