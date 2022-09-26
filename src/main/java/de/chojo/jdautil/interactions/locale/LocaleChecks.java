/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.locale;

import de.chojo.jdautil.localization.ILocalizer;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;

import java.util.Locale;

import static org.slf4j.LoggerFactory.getLogger;

public final class LocaleChecks {
    private LocaleChecks() {
        throw new UnsupportedOperationException("This is a utility class.");
    }

    private static final Logger log = getLogger(LocaleChecks.class);

    public static void checkCommandName(ILocalizer localizer, String prefix, String key) {
        checkLength(localizer, prefix, key, 1, 32);
        checkSnakeCase(localizer, prefix, key);
    }

    public static void checkCommandName(String name) {
        checkLength(name, 1, 32);
        checkSnakeCase(name);
    }

    public static void checkOptionName(ILocalizer localizer, String prefix, String key) {
        checkLength(localizer, prefix, key, 1, OptionData.MAX_NAME_LENGTH);
        checkSnakeCase(localizer, prefix, key);
    }

    public static void checkOptionName(String name) {
        checkLength(name, 1, OptionData.MAX_NAME_LENGTH);
        checkSnakeCase(name);
    }

    private static void checkLength(ILocalizer localizer, String prefix, String key, int min, int max) {
        localizer.keyMap(prefix, key).entrySet().stream()
                 .filter(e -> e.getValue().trim().length() < min || e.getValue().length() > max)
                 .forEach(e -> log.warn("Key {}.{}@{} has invalid length ({}). {}-{}",
                         prefix, key, e.getKey(), e.getValue(), min, max));
    }

    private static void checkLength(String name, int min, int max) {
        if (name.length() < min || name.length() > max) {
            log.warn("value \"{}\" has invalid length ({}). {}-{}", name, name.length(), min, max);
        }
    }

    private static void checkSnakeCase(ILocalizer localizer, String prefix, String key) {
        localizer.keyMap(prefix, key).entrySet().stream()
                 .filter(e -> !e.getValue().equals(e.getValue().toLowerCase().replace(" ", "_")))
                 .forEach(e -> log.warn("Key {}.{}@{} is not snake_case ({}). Should be {}",
                         prefix, key, e.getKey(), e.getValue(), e.getValue().toLowerCase().replace(" ", "_")));
    }

    private static void checkSnakeCase(String name) {
        if (!name.equals(name.toLowerCase().replace(" ", "_"))) {
            log.warn("Value \"{}\" is not snake_case. Should be {}",
                    name, name.toLowerCase(Locale.ROOT).replace(" ", "_"));
        }
    }

    public static void checkCommandDescription(ILocalizer localizer, String command, String description) {
        checkLength(localizer, command, description, 1, CommandData.MAX_DESCRIPTION_LENGTH);
    }

    public static void checkCommandDescription(String description) {
        checkLength(description, 1, CommandData.MAX_DESCRIPTION_LENGTH);
    }

    public static void checkOptionDescription(ILocalizer localizer, String command, String description) {
        checkLength(localizer, command, description, 1, OptionData.MAX_DESCRIPTION_LENGTH);
    }

    public static void checkOptionDescription(String description) {
        checkLength(description, 1, OptionData.MAX_DESCRIPTION_LENGTH);
    }
}
