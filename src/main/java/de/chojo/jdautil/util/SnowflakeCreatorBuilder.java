/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.util;

import org.slf4j.Logger;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public class SnowflakeCreatorBuilder {
    // Custom Epoch (January 1, 2015 Midnight UTC = 2015-01-01T00:00:00Z)
    private static final long DEFAULT_CUSTOM_EPOCH = 1420070400000L;
    private static final Logger log = getLogger(SnowflakeCreatorBuilder.class);

    private static final int ID_BITS = 5;
    private static final long MAX_ID = (1L << ID_BITS) - 1;

    private static final Map<Long, Long> CURRENT_PROCESS_ID = new HashMap<>();

    private long workerId = createNodeId();
    private long processId = -1;
    private long customEpoch = DEFAULT_CUSTOM_EPOCH;

    /**
     * Set the worker id for the creator. This id is usually the same for each running application instance
     *
     * @param workerId worker id
     * @return builder instance
     * @throws IllegalStateException when the id is larger than the max id or smaller than 0
     */
    public SnowflakeCreatorBuilder withWorkerId(long workerId) {
        if (workerId < 0 || workerId > MAX_ID) {
            throw new IllegalArgumentException(String.format("NodeId must be between %d and %d", 0, MAX_ID));
        }
        this.workerId = workerId;
        return this;
    }

    /**
     * Set the process id for the creator. If the id is not set a auto incremental id will be used which is unique for the worker.
     *
     * @param processId process id
     * @return builder instance
     * @throws IllegalStateException when the id is larger than the max id or smaller than 0
     */
    public SnowflakeCreatorBuilder withProcessId(long processId) {
        if (processId < 0 || processId > MAX_ID) {
            throw new IllegalArgumentException(String.format("NodeId must be between %d and %d", 0, MAX_ID));
        }
        this.processId = processId;
        return this;
    }

    /**
     * Sets a custom offset for the epoch millis.
     * <p>
     * Default is the {@code January 1, 2015 Midnight UTC = 2015-01-01T00:00:00Z}
     *
     * @param customEpoch custom offset.
     * @return builder instance
     */
    public SnowflakeCreatorBuilder withCustomEpoch(long customEpoch) {
        this.customEpoch = customEpoch;
        return this;
    }

    /**
     * Sets a custom offset for the epoch millis.
     * <p>
     * Default is the {@code January 1, 2015 Midnight UTC = 2015-01-01T00:00:00Z}
     *
     * @param offset custom offset.
     * @return builder instance
     */
    public SnowflakeCreatorBuilder withCustomEpoch(ZonedDateTime offset) {
        this.customEpoch = offset.toEpochSecond() * 1000;
        return this;
    }

    /**
     * Sets a custom offset for the epoch millis.
     * <p>
     * Default is the {@code January 1, 2015 Midnight UTC = 2015-01-01T00:00:00Z}
     *
     * @param offset custom offset.
     * @return builder instance
     */
    public SnowflakeCreatorBuilder withCustomEpoch(Instant offset) {
        this.customEpoch = offset.toEpochMilli();
        return this;
    }

    public SnowflakeCreator build() {
        return new SnowflakeCreator(workerId, processId == -1 ? nextProcessId() : processId, customEpoch);
    }

    private synchronized long nextProcessId() {
        return CURRENT_PROCESS_ID.compute(workerId, (key, val) -> val == null ? 0 : val + 1);
    }

    /**
     * Inspired by
     * https://github.com/callicoder/java-snowflake/blob/a166af56e24a01dd91d5787f0b85afd5091559c0/src/main/java/com/callicoder/snowflake/Snowflake.java
     */
    private long createNodeId() {
        long nodeId;
        try {
            var builder = new StringBuilder();
            var networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                var networkInterface = networkInterfaces.nextElement();
                var mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    for (var macPort : mac) {
                        builder.append(String.format("%02X", macPort));
                    }
                }
            }
            nodeId = builder.toString().hashCode();
        } catch (SocketException err) {
            log.error("Coult not create default node id. Using fallback.", err);
            nodeId = (new SecureRandom().nextInt());
        }
        return nodeId & MAX_ID;
    }
}
