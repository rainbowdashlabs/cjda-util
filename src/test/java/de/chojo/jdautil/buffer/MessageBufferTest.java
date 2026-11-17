/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.buffer;

import net.dv8tion.jda.api.sharding.ShardManager;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MessageBufferTest {

    @Test
    void defaultFormatterFormatsTheTimestamp() {
        var buffer = new MessageBuffer.Builder(Mockito.mock(ShardManager.class), 1L).build();

        assertDoesNotThrow(() -> buffer.add("line"));
    }

    @Test
    void customFormatterWithoutZoneFormatsTheTimestamp() {
        var buffer = new MessageBuffer.Builder(Mockito.mock(ShardManager.class), 1L)
                .formatter("yyyy-MM-dd HH:mm")
                .build();

        assertDoesNotThrow(() -> buffer.add("line"));
    }
}
