/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.List;

public record BotListData(long guilds, long user) {

    public static BotListData total(ShardManager shardManager) {
        var guilds = shardManager.getGuildCache().size();
        var user = shardManager.getUserCache().size();

        return new BotListData(guilds, user);
    }
}
