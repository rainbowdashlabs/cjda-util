/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.localization;

import de.chojo.jdautil.localization.util.Replacement;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public interface ILocalizer extends LocalizationFunction {
    Pattern localeName = Pattern.compile("\\.?([_\\w-]+?)\\.name$");
    Pattern lowercase = Pattern.compile("^[\\w_-]+$");
    Logger log = getLogger(ILocalizer.class);
    ILocalizer DEFAULT = new ILocalizer() {
        @Override
        public String localize(String message, DiscordLocale language, Replacement... replacements) {
            return message;
        }

        @Override
        public ContextLocalizer getContextLocalizer(Guild guild) {
            return new ContextLocalizer(this, null);
        }

        @Override
        public ContextLocalizer getContextLocalizer(CommandInteraction interaction) {
            return new ContextLocalizer(this, null);
        }

        @Override
        public ContextLocalizer getContextLocalizer(MessageChannel channel) {
            return new ContextLocalizer(this, null);
        }

        @Override
        public DiscordLocale getGuildLocale(Guild guild) {
            return defaultLanguage();
        }

        @Override
        public String localize(String message, CommandInteraction interaction, Replacement... replacements) {
            return message;
        }

        @Override
        public String localize(String message, MessageChannel channel, Replacement... replacements) {
            return message;
        }

        @Override
        public String localize(String message, Replacement... replacements) {
            return message;
        }

        @Override
        public String localize(String message, Guild guild, Replacement... replacements) {
            return message;
        }

        @Override
        public Set<DiscordLocale> languages() {
            return Collections.singleton(DiscordLocale.ENGLISH_US);
        }

        @Override
        public DiscordLocale defaultLanguage() {
            return DiscordLocale.ENGLISH_US;
        }
    };

    String localize(String message, DiscordLocale language, Replacement... replacements);

    ContextLocalizer getContextLocalizer(Guild guild);

    ContextLocalizer getContextLocalizer(CommandInteraction interaction);

    ContextLocalizer getContextLocalizer(MessageChannel channel);

    DiscordLocale getGuildLocale(Guild guild);

    String localize(String message, CommandInteraction interaction, Replacement... replacements);

    String localize(String message, MessageChannel channel, Replacement... replacements);

    String localize(String message, Replacement... replacements);

    @NotNull
    @Override
    default Map<DiscordLocale, String> apply(@NotNull String localizationKey) {
        return languages().stream().collect(Collectors.toMap(lang -> lang, lang -> localizeChecked(localizationKey, lang)));
    }

    @NotNull
    private String localizeChecked(String key, DiscordLocale locale) {
        var localize = localize(key, locale);
        if (localize == null) {
            log.warn("Result for key {}@{} is null.", key, locale);
            return "null";
        }
        var nameMatcher = localeName.matcher(localize);
        if (nameMatcher.find()) {
            localize = nameMatcher.group(1);
            log.warn("No value for {}. Defaulting to {}", key, localize);
        }
        if (localize.isBlank()) {
            log.warn("Result for key {}@{} is empty.", key, locale);
        }
        log.trace("Localized key {} for {}", key, locale);

        if (key.endsWith("name")) {
            if (!lowercase.matcher(localize).matches() && localize.toLowerCase().equals(localize)) {
                log.warn("Key {}@{} has invalid value \"{}\"", key, locale, localize);
            }
        }

        return localize;
    }

    @NotNull
    default LocalizationFunction prefixedLocalizer(String prefix) {
        return key -> languages().stream().collect(Collectors.toMap(lang -> lang, lang -> localizeChecked("%s.%s".formatted(prefix, key), lang)));
    }

    String localize(String message, Guild guild, Replacement... replacements);

    Set<DiscordLocale> languages();

    DiscordLocale defaultLanguage();

    default Optional<DiscordLocale> getLanguage(String language) {
        return languages().stream().filter(lang -> lang.getLocale().equalsIgnoreCase(language)).findFirst();
    }
}
