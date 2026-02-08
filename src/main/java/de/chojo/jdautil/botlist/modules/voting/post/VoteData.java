/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist.modules.voting.post;

import java.util.Map;

public class VoteData {
    private final String listId;
    private final long userId;
    private final boolean bonus;
    private final long guildId;

    public VoteData(String listId, long userId, boolean bonus, long guildId) {
        this.listId = listId;
        this.userId = userId;
        this.bonus = bonus;
        this.guildId = guildId;
    }

    public long userId() {
        return userId;
    }

    public boolean isBonus() {
        return bonus;
    }

    public long guildId() {
        return guildId;
    }
}
