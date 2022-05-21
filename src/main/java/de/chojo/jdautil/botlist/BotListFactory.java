/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist;

import de.chojo.jdautil.botlist.builder.BotlistBuilder;
import de.chojo.jdautil.botlist.modules.shared.AuthHandler;
import de.chojo.jdautil.botlist.modules.submission.StatsMapper;
import de.chojo.jdautil.botlist.modules.voting.post.VoteReceiverFactory;
import net.dv8tion.jda.api.sharding.ShardManager;

/**
 * Factory to provide botlist settings for most popular botlists.
 */
public interface BotListFactory {
    /**
     * Build a top.gg botlist
     */
    BotListFactory TOP_GG = (shardManager, config) -> BotlistBuilder.builder("top.gg")
            .forClient(shardManager)
            .withBaseUrl("https://top.gg/api")
            .withAuthentication(AuthHandler.of(config.statsToken()))
            .withSubmission(StatsMapper.of(
                    "bots/{ID}/stats",
                    (data, map) -> map
                            .add("server_count", data.guilds())
                            .add("shard_id", data.shardId())
                            .add("shard_count", data.shards())
            ))
            .withVoteReceiver(VoteReceiverFactory.TOP_GG.build(config.voteToken()))
            .build();

    /**
     * Build a discord.bots.gg botlist
     */
    BotListFactory DISCORDBOTS_GG = (shardManager, config) -> BotlistBuilder.builder("discord.bots.gg")
            .forClient(shardManager)
            .withBaseUrl("https://discord.bots.gg/api/v1")
            .withAuthentication(AuthHandler.of(config.statsToken()))
            .withSubmission(StatsMapper.of(
                    "bots/{ID}/stats",
                    (data, map) -> map
                            .add("guildCount", data.guilds())
                            .add("shardCount", data.shards())
                            .add("shardId", data.shardId())
            ))
            .build();

    /**
     * Build a discordbotlist.com botlist
     */
    BotListFactory DISCORDBOTLIST_COM = (shardManager, config) -> BotlistBuilder.builder("discordbotlist.com")
            .forClient(shardManager)
            .withBaseUrl("https://discordbotlist.com")
            .withAuthentication(AuthHandler.of(config.statsToken()))
            .withSubmission(StatsMapper.of(
                    "api/v1/bots/{id}/stats", (shard, map) -> map
                            .add("guilds", shard.guilds())
                            .add("users", shard.user())
                            .add("shard_id", shard.shardId())
            ))
            .withVoteReceiver(VoteReceiverFactory.DISCORDBOTLIST_COM.build(config.voteToken()))
            .build();

    /**
     * Build a discordbotlist.com botlist
     */
    BotListFactory BOTLIST_ME = (shardManager, config) -> BotlistBuilder.builder("botlist.me")
            .forClient(shardManager)
            .withBaseUrl("https://botlist.me")
            .withAuthentication(AuthHandler.of(config.statsToken()))
            .withSubmission(StatsMapper.of(
                    "api/v1/bots/{id}/stats",
                    (shard, map) -> map
                            .add("server_count", shard.guilds())
                            .add("shard_count", shard.shards())
            ))
            .useShardStats(false)
            .withVoteReceiver(VoteReceiverFactory.BOTLIST_ME.build(config.statsToken()))
            .build();

    /**
     * Build the botlist settings
     *
     * @param shardManager shard manager instance
     * @param config       config of botlist
     * @return new botlist instance
     */
    BotList build(ShardManager shardManager, BotListConfig config);
}
