/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist;

import de.chojo.jdautil.botlist.builder.BotlistBuilder;
import de.chojo.jdautil.botlist.modules.shared.AuthHandler;
import de.chojo.jdautil.botlist.modules.submission.StatsMapper;
import de.chojo.jdautil.botlist.modules.voting.poll.BotlistMeVoteCheck;
import de.chojo.jdautil.botlist.modules.voting.poll.DiscordbotlistComUpvotes;
import de.chojo.jdautil.botlist.modules.voting.poll.TopGgVoteCheck;
import de.chojo.jdautil.botlist.modules.voting.poll.VotingCheckingService;
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
            .withBaseUrl("https://top.gg/api/v1")
            .withAuthentication(AuthHandler.bearer(config.statsToken()))
            .withSubmission(StatsMapper.of(
                    "PATCH",
                    "projects/@me/metrics",
                    (data, map) -> map.add("server_count", data.guilds()).add("shard_count", data.shardCount())))
            .useShardStats(false)
            .withVoteChecker(new VotingCheckingService<>("projects/@me/votes/{user}", TopGgVoteCheck.class))
            .withVoteReceiver(VoteReceiverFactory.TOP_GG.build(config.voteToken()))
            .build();

    /**
     * Build a discord.bots.gg botlist
     */
    BotListFactory DISCORD_BOTS_GG = (shardManager, config) -> BotlistBuilder.builder("discord.bots.gg")
            .forClient(shardManager)
            .withBaseUrl("https://discord.bots.gg/api/v1")
            .withAuthentication(AuthHandler.of(config.statsToken()))
            .withSubmission(StatsMapper.of("bots/{id}/stats", (data, map) -> map.add("guildCount", data.guilds())))
            .useShardStats(false)
            .build();

    /**
     * Build a discordbotlist.com botlist
     */
    BotListFactory DISCORDBOTLIST_COM = (shardManager, config) -> BotlistBuilder.builder("discordbotlist.com")
            .forClient(shardManager)
            .withBaseUrl("https://discordbotlist.com")
            .withAuthentication(AuthHandler.of(config.statsToken()))
            .withSubmission(StatsMapper.of("api/v1/bots/{id}/stats", (data, map) -> {
                map.add("guilds", data.guilds()).add("users", data.user());
                if (data.isShard()) map.add("shard_id", data.shardId());
            }))
            .useShardStats(true)
            .withVoteChecker(new VotingCheckingService<>("api/v1/bots/{id}/upvotes", DiscordbotlistComUpvotes.class))
            .withVoteReceiver(VoteReceiverFactory.DISCORDBOTLIST_COM.build(config.voteToken()))
            .build();

    /**
     * Build a botlist.me botlist
     */
    BotListFactory BOTLIST_ME = (shardManager, config) -> BotlistBuilder.builder("botlist.me")
            .forClient(shardManager)
            .withBaseUrl("https://api.botlist.me")
            .withAuthentication(AuthHandler.of(config.statsToken()))
            .withSubmission(StatsMapper.of(
                    "api/v1/bots/{id}/stats",
                    (data, map) -> map.add("server_count", data.guilds()).add("shard_count", data.shardCount())))
            .useShardStats(false)
            .withVoteChecker(
                    new VotingCheckingService<>("api/v1/bots/{id}/voted?userId={user}", BotlistMeVoteCheck.class))
            .withVoteReceiver(VoteReceiverFactory.BOTLIST_ME.build(config.voteToken()))
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
