/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.chojo.jdautil.wrapper;

import de.chojo.jdautil.parsing.ArgumentUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

class FlagContainerTest {

    @Test
    void of() {
        var s = "hello -wu -n my name --age 17".split("\\s");

        var container = FlagContainer.of(s);

        Assertions.assertTrue(container.has("w"));
        Assertions.assertTrue(container.has("u"));
        Assertions.assertNull(container.get("w"));
        Assertions.assertNull(container.get("u"));

        Assertions.assertTrue(container.has("n"));
        Assertions.assertEquals("my name", container.get("n"));

        Assertions.assertTrue(container.has("age"));
        Assertions.assertEquals("17", container.get("age"));
    }
}