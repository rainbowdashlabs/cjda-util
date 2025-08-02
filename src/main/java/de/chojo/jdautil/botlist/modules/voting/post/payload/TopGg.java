/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist.modules.voting.post.payload;

import de.chojo.jdautil.botlist.modules.voting.post.VoteData;

public class TopGg implements VoteDataAdapter {
    public long bot;
    public long user;
    public String type;
    public boolean isWeekend;

    @Override
    public VoteData toVotaData() {
        return new VoteData(user, isWeekend);
    }
}
