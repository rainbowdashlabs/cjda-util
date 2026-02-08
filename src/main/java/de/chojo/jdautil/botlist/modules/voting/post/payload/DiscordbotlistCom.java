/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist.modules.voting.post.payload;

import de.chojo.jdautil.botlist.modules.voting.post.VoteData;

public class DiscordbotlistCom implements VoteDataAdapter {
    public long id;

    @Override
    public VoteData toVoteData() {
        return new VoteData("discordbotlist.com", id, false, 0);
    }
}
