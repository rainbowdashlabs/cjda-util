/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist;

import de.chojo.jdautil.botlist.modules.submission.SubmissionService;
import de.chojo.jdautil.botlist.modules.voting.VoteService;
import de.chojo.jdautil.botlist.modules.voting.post.VoteData;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.Logger;

import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Class which can handle reporting to botlists.
 */
public class BotlistService implements Runnable {
    private static final Logger log = getLogger(BotlistService.class);
    private final Set<BotList> botLists;
    private final VoteService voteService;
    private final SubmissionService submissionService;
    private Consumer<VoteData> data;

    private BotlistService(ShardManager shardManager, Set<BotList> botLists, VoteService voteService) {
        this.botLists = botLists;
        this.voteService = voteService;
        this.submissionService = new SubmissionService(this, shardManager);
    }

    /**
     * Get a builder for a botlist reporter
     *
     * @param shardManager shardmanager to use for statistics
     * @return builder instance
     */
    public static Builder build(ShardManager shardManager) {
        return new Builder(shardManager);
    }

    @Override
    public void run() {
        try {
            submissionService.submitData();
        } catch (RuntimeException e) {
            log.error("Data submission failed.");
        }
    }

    private void ignite() {
        for (var botList : botLists) voteService.register(botList);
    }

    public List<BotList> hasVoted(User user) {
        if (voteService == null) return Collections.emptyList();
        return voteService.hasVoted(user);
    }

    public Set<BotList> botlists() {
        return Collections.unmodifiableSet(botLists);
    }

    public static class Builder {
        private final ShardManager shardManager;
        private long interval = 60;
        private TimeUnit unit = TimeUnit.of(ChronoUnit.MINUTES);
        private ScheduledExecutorService executorService;
        private final Set<BotList> botLists = new HashSet<>();
        private VoteService voteService;

        public Builder(ShardManager shardManager) {
            this.shardManager = shardManager;
        }

        /**
         * Add a botlist to submit to.
         *
         * @param botList the botlist to submit to.
         * @return builder instance
         */
        public Builder forBotlist(BotList botList) {
            botLists.add(botList);
            return this;
        }

        /**
         * Add discord.bots.gg
         *
         * @param config botlist configuration
         * @return builder instance
         */
        public Builder forTopGG(BotListConfig config) {
            return forBotlist(BotListFactory.TOP_GG.build(shardManager, config));
        }

        /**
         * Add discord.bots.gg
         *
         * @param config botlist configuration
         * @return builder instance
         */
        public Builder forDiscordBotListCOM(BotListConfig config) {
            return forBotlist(BotListFactory.DISCORDBOTLIST_COM.build(shardManager, config));
        }

        /**
         * Add discord.bots.gg
         *
         * @param config botlist configuration
         * @return builder instance
         */
        public Builder forDiscordBotsGG(BotListConfig config) {
            return forBotlist(BotListFactory.DISCORDBOTS_GG.build(shardManager, config));
        }

        /**
         * Add discord.bots.gg
         *
         * @param config botlist configuration
         * @return builder instance
         */
        public Builder forBotlistMe(BotListConfig config) {
            return forBotlist(BotListFactory.BOTLIST_ME.build(shardManager, config));
        }

        /**
         * Set the executor service.
         * <p>
         * If not set a {@link Executors#newSingleThreadScheduledExecutor()} will be used.
         *
         * @param executorService executor service
         * @return builder instance
         */
        public Builder withExecutorService(ScheduledExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        /**
         * Set the submit interval of the stats
         *
         * @param interval interval
         * @param unit     time unit
         * @return builder instance
         */
        public Builder withSubmitInterval(long interval, TimeUnit unit) {
            this.interval = interval;
            this.unit = unit;
            return this;
        }

        public Builder withVoteService(Function<VoteService.Builder, VoteService> voteService) {
            this.voteService = voteService.apply(VoteService.builder());
            return this;
        }

        /**
         * Build the bot list reporter and start submitting
         *
         * @return created and scheduled reporter instance
         */
        public BotlistService build() {
            if (executorService == null) {
                executorService = Executors.newSingleThreadScheduledExecutor();
            }
            log.info("Started BotlistReporter for {} lists. Sending data every {} {}", botLists.size(), interval, unit.name());
            var botlistService = new BotlistService(shardManager, botLists, voteService);
            executorService.scheduleAtFixedRate(botlistService, 1, interval, unit);
            botlistService.ignite();
            return botlistService;
        }
    }
}
