/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.util;

import java.util.function.Consumer;

public class Consumers {
    public static <T> Consumer<T> empty() {
        return t -> {
        };
    }
}
