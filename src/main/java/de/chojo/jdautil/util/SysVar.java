/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.util;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class SysVar {
    @Deprecated
    public static String getOrThrow(String property, String environment, Supplier<RuntimeException> throwableSupplier) {
        return get(property, environment).orElseThrow(throwableSupplier);
    }

    @Deprecated
    public static <T extends Exception> String getOrThrowChecked(String property, String environment, Supplier<T> throwableSupplier) throws T {
        return get(property, environment).orElseThrow(throwableSupplier);
    }

    public static <T extends Exception> String envOrPropOrThrow(String env, String prop, Supplier<T> throwableSupplier) throws T {
        return envOrProp(env, prop).orElseThrow(throwableSupplier);
    }

    public static <T extends Exception> String propOrEnvOrThrow(String property, String environment, Supplier<T> throwableSupplier) throws T {
        return propOrEnv(property, environment).orElseThrow(throwableSupplier);
    }

    public static <T extends Exception> String envOrPropOrThrow(String property, String environment) throws T {
        return envOrProp(property, environment).orElseThrow(() -> UnknownVariableException.forEnvOrProp(environment, property));
    }

    public static <T extends Exception> String propOrEnvOrThrow(String property, String environment) throws T {
        return propOrEnv(property, environment).orElseThrow(() -> UnknownVariableException.forEnvOrProp(environment, property));
    }

    public static String getOrDefault(String property, String environment, String def) {
        return get(property, environment).orElse(def);
    }

    @Deprecated
    public static Optional<String> get(String prop, String env) {
        return Optional.ofNullable(prop(prop)).orElseGet(() -> env(env));
    }

    public static String envOrProp(String env, String prop, String def) {
        return envOrProp(env, prop).orElse(def);
    }

    public static String propOrEnv(String prop, String env, String def) {
        return propOrEnv(prop, env).orElse(def);
    }

    public static Optional<String> envOrProp(String env, String prop) {
        return Optional.ofNullable(env(env)).orElseGet(() -> prop(prop));
    }

    public static Optional<String> propOrEnv(String prop, String env) {
        return get(List.of(() -> prop(prop), () -> env(env)));
    }

    private static Optional<String> prop(String name) {
        return Optional.ofNullable(System.getProperty(name));
    }

    private static Optional<String> env(String name) {
        return Optional.ofNullable(System.getenv(name));
    }

    public static Optional<String> get(List<Supplier<Optional<String>>> suppliers) {
        for (Supplier<Optional<String>> supplier : suppliers) {
            Optional<String> opt = supplier.get();
            if (opt.isPresent()) return opt;
        }
        return Optional.empty();
    }

    public static class UnknownVariableException extends RuntimeException {
        public UnknownVariableException(String message) {
            super(message);
        }

        public static UnknownVariableException forProp(String prop) {
            return new UnknownVariableException("Property %s is not set".formatted(prop));
        }

        public static UnknownVariableException forEnv(String env) {
            return new UnknownVariableException("Environment variable %s is not set".formatted(env));
        }

        public static UnknownVariableException forEnvOrProp(String env, String prop) {
            return new UnknownVariableException("Neither environment variable %s nor prop %s are set".formatted(env, prop));
        }
    }
}
