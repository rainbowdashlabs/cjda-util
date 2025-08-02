/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
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

public abstract class BaseConfiguration<T> {
    private static final Logger log = getLogger(Configuration.class);
    protected final ObjectMapper objectMapper;
    protected T config;

    protected BaseConfiguration(T config, ObjectMapper mapper) {
        objectMapper = mapper;
        this.config = config;
    }

    protected BaseConfiguration(T config) {
        this(config, JsonMapper.builder()
                .configure(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS, true)
                .build()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
                .setDefaultPrettyPrinter(new DefaultPrettyPrinter()));
    }

    public void reload() {
        try {
            reloadFile();
        } catch (IOException e) {
            log.info("Could not load config", e);
            throw new ConfigurationException("Could not load config file", e);
        }
        try {
            save();
        } catch (IOException e) {
            log.error("Could not save config.", e);
        }
    }

    private void save() throws IOException {
        try (var writer = objectMapper.writerWithDefaultPrettyPrinter().writeValues(getConfig().toFile())) {
            writer.write(config);
        }
    }

    private void reloadFile() throws IOException {
        forceConsistency();
        config = (T) objectMapper.readValue(getConfig().toFile(), config.getClass());
    }

    private void forceConsistency() throws IOException {
        Files.createDirectories(getConfig().getParent());
        if (!getConfig().toFile().exists()) {
            if (getConfig().toFile().createNewFile()) {
                save();
                throw new ConfigurationException("Please configure the config.");
            }
        }
    }

    private Path getConfig() {
        var home = new File(".").getAbsoluteFile().getParentFile().toPath();
        var variable = SysVar.envOrPropOrThrow("BOT_CONFIG", "bot.config",
                () -> new ConfigurationException("Set property -Dbot.config=<config path> or environment variable BOT_CONFIG."));
        log.info("Found variable for config file");
        return Paths.get(home.toString(), variable);
    }

    public T config() {
        return config;
    }
}
