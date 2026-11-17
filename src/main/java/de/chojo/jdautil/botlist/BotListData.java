/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.List;

/**
 * Statistics sent to a bot list, either for the whole bot or for a single shard.
 *
 * @param guilds     guild count
 * @param user       user count
 * @param shardId    id of the shard, or -1 for the whole bot
 * @param shardCount total number of shards
 */
public record BotListData(long guilds, long user, int shardId, int shardCount) {

    /**
     * Statistics for the whole bot.
     *
     * @param guilds     guild count
     * @param user       user count
     * @param shardCount total number of shards
     * @return statistics without a shard id
     */
    public static BotListData total(long guilds, long user, int shardCount) {
        return new BotListData(guilds, user, -1, shardCount);
    }

    /**
     * Statistics for the whole bot, taken from the caches of the shard manager.
     *
     * @param shardManager shard manager
     * @return statistics without a shard id
     */
    public static BotListData total(ShardManager shardManager) {
        return total(
                shardManager.getGuildCache().size(), shardManager.getUserCache().size(), shardManager.getShardsTotal());
    }

    /**
     * Statistics per shard, taken from the caches of each running shard.
     *
     * @param shardManager shard manager
     * @return one entry per running shard
     */
    public static List<BotListData> shards(ShardManager shardManager) {
        int shardCount = shardManager.getShardsTotal();
        return shardManager.getShards().stream()
                .map(shard -> shard(shard, shardCount))
                .toList();
    }

    private static BotListData shard(JDA shard, int shardCount) {
        return new BotListData(
                shard.getGuildCache().size(),
                shard.getUserCache().size(),
                shard.getShardInfo().getShardId(),
                shardCount);
    }

    /**
     * Whether these statistics belong to a single shard.
     *
     * @return true for shard statistics
     */
    public boolean isShard() {
        return shardId >= 0;
    }
}
