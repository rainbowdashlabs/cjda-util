/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.parsing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ArgumentUtilTest {

    @Test
    void parseQuotedArgs() {
        var args = "These \"are some\" nicely splittet \"arguments\" right?".split("\\s");

        args = ArgumentUtil.parseQuotedArgs(args);

        Assertions.assertEquals("These", args[0]);
        Assertions.assertEquals("are some", args[1]);
        Assertions.assertEquals("nicely", args[2]);
        Assertions.assertEquals("splittet", args[3]);
        Assertions.assertEquals("arguments", args[4]);
        Assertions.assertEquals("right?", args[5]);
    }
}