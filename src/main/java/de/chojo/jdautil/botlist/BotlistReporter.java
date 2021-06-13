package de.chojo.jdautil.botlist;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.Logger;

import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Class which can handle reporting to botlists.
 */
public class BotlistReporter implements Runnable {
    private static final Logger log = getLogger(BotlistReporter.class);
    private final ShardManager shardManager;
    private final Set<BotList> botLists;

    private BotlistReporter(ShardManager shardManager, Set<BotList> botLists) {
        this.shardManager = shardManager;
        this.botLists = botLists;
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
        for (var botList : botLists) {
            try {
                botList.report(shardManager);
            } catch (JsonProcessingException e) {
                log.error("Could not build stats", e);
            }
        }
    }

    public static class Builder {
        private final ShardManager shardManager;
        private long interval = 60;
        private TimeUnit unit = TimeUnit.of(ChronoUnit.MINUTES);
        private ScheduledExecutorService executorService;
        Set<BotList> botLists = new HashSet<>();

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
         * @param key auth key
         * @return builder instance
         */
        public Builder forTopGG(String key) {
            return forBotlist(BotListFactory.TOP_GG.build(key));
        }

        /**
         * Add discord.bots.gg
         *
         * @param key auth key
         * @return builder instance
         */
        public Builder forDiscordBotListCOM(String key) {
            return forBotlist(BotListFactory.DISCORD_BOT_LIST_COM.build(key));
        }

        /**
         * Add discord.bots.gg
         *
         * @param key auth key
         * @return builder instance
         */
        public Builder forDiscordBotsGG(String key) {
            return forBotlist(BotListFactory.DISCORD_BOTS_GG.build(key));
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

        /**
         * Build the bot list reporter and start submitting
         *
         * @return created and scheduled reporter instance
         */
        public BotlistReporter build() {
            if (executorService == null) {
                executorService = Executors.newSingleThreadScheduledExecutor();
            }
            var botlistReporter = new BotlistReporter(shardManager, botLists);
            executorService.scheduleAtFixedRate(botlistReporter, 1, interval, unit);
            return botlistReporter;
        }
    }
}
