/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.buffer;

import net.dv8tion.jda.api.sharding.ShardManager;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MessageBuffer implements Runnable {
    private final DateTimeFormatter formatter;
    private final int maxLength;
    private final ShardManager shardManager;
    private final long channelId;
    private final int flushDuration;
    private Instant oldest = null;
    private final StringBuilder buffer = new StringBuilder();

    private MessageBuffer(int maxLength, DateTimeFormatter formatter, ShardManager shardManager, long channelId, int flushDuration) {
        this.maxLength = maxLength;
        this.formatter = formatter;
        this.shardManager = shardManager;
        this.channelId = channelId;
        this.flushDuration = flushDuration;
    }


    @Override
    public synchronized void run() {
        if (oldest == null) return;
        if (oldest.isBefore(Instant.now().minus(flushDuration, ChronoUnit.MILLIS)) && !buffer.isEmpty()) {
            flush();
        }
    }

    public void add(String string) {
        var message = timestamp() + " " + string + "\n";
        if (message.length() + buffer.length() > maxLength) {
            flush();
        }
        if (oldest == null) {
            oldest = Instant.now();
        }
        buffer.append(message);
    }

    private void flush() {
        oldest = null;
        var string = buffer.toString();
        buffer.setLength(0);
        shardManager.getTextChannelById(channelId).sendMessage(string).queue();
    }

    private String timestamp() {
        return formatter.format(Instant.now());
    }


    public static final class Builder {
        private final int maxLength = 2048;
        private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        private final ShardManager shardManager;
        private final long channelId;
        private int flushDuration = 5000;

        public Builder(ShardManager shardManager, long channelId) {
            this.shardManager = shardManager;
            this.channelId = channelId;
        }

        public Builder formatter(DateTimeFormatter formatter) {
            this.formatter = formatter;
            return this;
        }

        public Builder formatter(String format) {
            return formatter(DateTimeFormatter.ofPattern(format));
        }

        public Builder flushDuration(int flushDuration) {
            this.flushDuration = flushDuration;
            return this;
        }

        public MessageBuffer build(ScheduledExecutorService service) {
            var messageBuffer = new MessageBuffer(maxLength, formatter, shardManager, channelId, flushDuration);
            service.scheduleAtFixedRate(messageBuffer, flushDuration / 2, flushDuration / 2, TimeUnit.MILLISECONDS);
            return messageBuffer;
        }

        public MessageBuffer build() {
            return new MessageBuffer(maxLength, formatter, shardManager, channelId, flushDuration);
        }
    }
}
