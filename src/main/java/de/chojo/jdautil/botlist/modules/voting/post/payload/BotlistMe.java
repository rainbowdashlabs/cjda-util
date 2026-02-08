/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist.modules.voting.post.payload;

import de.chojo.jdautil.botlist.modules.voting.post.VoteData;

public class BotlistMe implements VoteDataAdapter {
    public long bot;
    public long user;
    public String type;

    @Override
    public VoteData toVoteData() {
        return new VoteData("botlist.me",user, false,0);
    }

    @Override
    public void injectGuild(long guildId) {

    }
}
