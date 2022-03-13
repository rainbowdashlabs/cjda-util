/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist.modules.voting.post.payload;

import de.chojo.jdautil.botlist.modules.voting.post.VoteData;

public class BotlistMe implements VoteDataAdapter {
    private long bot;
    private long user;
    private String type;

    @Override
    public VoteData toVotaData() {
        return new VoteData(user, false);
    }
}
