/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.configuration;

/**
 * Wrapper class for configuration files.
 *
 * @param <T> type of config class
 */
public class Configuration<T> extends BaseConfiguration<T> {

    private Configuration(T config) {
        super(config);
    }

    public static <T> Configuration<T> create(T def) {
        var configuration = new Configuration<>(def);
        configuration.reload();
        return configuration;
    }
}
