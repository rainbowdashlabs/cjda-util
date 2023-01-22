/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.util;

import java.util.Optional;
import java.util.function.Supplier;

public class SysVar {
    public static String getOrThrow(String property, String environment, Supplier<RuntimeException> throwableSupplier) {
        return get(property, environment).orElseThrow(throwableSupplier);
    }

    public static <T extends Exception> String getOrThrowChecked(String property, String environment, Supplier<T> throwableSupplier) throws T {
        return get(property, environment).orElseThrow(throwableSupplier);
    }

    public static String getOrDefault(String property, String environment, String def) {
        return get(property, environment).orElse(def);
    }

    public static Optional<String> get(String property, String environment) {
        return Optional.ofNullable(prop(property)).orElseGet(() -> env(environment));
    }

    private static Optional<String> prop(String name) {
        return Optional.ofNullable(System.getProperty(name));
    }

    private static Optional<String> env(String name) {
        return Optional.ofNullable(System.getenv(name));
    }
}
