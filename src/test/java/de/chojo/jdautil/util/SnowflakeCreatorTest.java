/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SnowflakeCreatorTest {

    private static final SnowflakeCreator CREATOR = SnowflakeCreator.builder().build();

    @Test
    public void create() {
        Assertions.assertNotEquals(CREATOR.nextId(), CREATOR.nextId(), "Creator yielded same ids");
        System.out.println(CREATOR.nextId());
        var creator2 = SnowflakeCreator.builder().build();
        Assertions.assertNotEquals(CREATOR.nextId(), creator2.nextId());
    }

    @Test
    public void bench(){
        var ids = 10000000;
        var refRuntime = ids / 4095.0 * 1.1;
        var start = System.currentTimeMillis();
        for (var i = 0; i < ids; i++) {
            CREATOR.nextId();
        }

        var runtime = System.currentTimeMillis() - start;
        Assertions.assertTrue(runtime < refRuntime, "Runtime is longer than the reference runtime. Might be a performance issue");
    }
}
