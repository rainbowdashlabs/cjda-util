/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.util;

import java.time.Instant;

/**
 * Inspired by
 * https://github.com/callicoder/java-snowflake/blob/a166af56e24a01dd91d5787f0b85afd5091559c0/src/main/java/com/callicoder/snowflake/Snowflake.java
 */
public class SnowflakeCreator {
    private static final int SEQUENCE_BITS = 12;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    private final long workerId;
    private final long processId;
    private final long customEpoch;

    private volatile long lastTimestamp = -1L;
    private volatile long sequence;

    public static SnowflakeCreatorBuilder builder() {
        return new SnowflakeCreatorBuilder();
    }

    SnowflakeCreator(long workerId, long processId, long customEpoch) {
        this.workerId = workerId << 17;
        this.processId = processId << 12;
        this.customEpoch = customEpoch;
    }

    /**
     * Get a new unique snowflake.
     *
     * @return a unique snowflake for the current time
     */
    public synchronized long nextId() {
        var current = timestamp();

        if (current < lastTimestamp) throw new IllegalStateException("Invalid System Clock!");

        if (current == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // Sequence Exhausted, wait till next millisecond.
                current = waitNextMillis(current);
            }
        } else {
            // reset sequence to start with zero for the next millisecond
            lastTimestamp = current;
            sequence = 0;
        }

        return (current << 22) + workerId + processId + sequence;
    }

    /**
     * Epoch millis of the current time with the {@link #customEpoch} offset applied
     *
     * @return current custom epoch millis
     */
    private long timestamp() {
        return Instant.now().toEpochMilli() - customEpoch;
    }

    /**
     * Block and wait till next millisecond
     *
     * @param currentTimestamp the current timestamp when the wait was initialized
     * @return the next timestamp
     */
    private long waitNextMillis(long currentTimestamp) {
        while (currentTimestamp == lastTimestamp) {
            currentTimestamp = timestamp();
        }
        return currentTimestamp;
    }
}
