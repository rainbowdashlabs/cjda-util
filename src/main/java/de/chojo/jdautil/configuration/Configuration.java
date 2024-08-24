/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import de.chojo.jdautil.configuration.exception.ConfigurationException;
import de.chojo.jdautil.util.SysVar;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.slf4j.LoggerFactory.getLogger;

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
