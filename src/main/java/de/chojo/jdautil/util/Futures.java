/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.util;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class Futures {
    private Futures() {
        throw new UnsupportedOperationException("This is a utility class.");
    }

    public static <T> BiConsumer<T, Throwable> whenComplete(Consumer<T> value, Consumer<Throwable> error) {
        return (t, throwable) -> {
            if (throwable != null) {
                error.accept(throwable);
                return;
            }
            value.accept(t);
        };
    }
}
