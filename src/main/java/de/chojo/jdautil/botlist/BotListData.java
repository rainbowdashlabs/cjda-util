/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.List;

public record BotListData(long guilds, long user, int shardId, int shards) {

    public static BotListData total(ShardManager shardManager) {
        var guilds = shardManager.getGuildCache().size();
        var user = shardManager.getUserCache().size();
        var shards = shardManager.getShards().size();

        return new BotListData(guilds, user, shards, shards);
    }
    public static List<BotListData> ofShards(ShardManager shardManager) {
        return shardManager.getShards().stream().map(BotListData::ofShard).toList();
    }

    public static BotListData ofShard(JDA shard) {
        var guilds = shard.getGuildCache().size();
        var user = shard.getUserCache().size();
        return new BotListData(guilds, user, shard.getShardInfo().getShardId(), shard.getShardInfo().getShardTotal());
    }
}
