/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.localization;

import de.chojo.jdautil.localization.util.GuildLocaleProvider;
import de.chojo.jdautil.localization.util.LocaleProvider;
import de.chojo.jdautil.localization.util.Replacement;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.DiscordLocale;
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
    Pattern options = Pattern.compile("\\.options\\.[_\\w-]+?\\.(?:name|description)$");
    Pattern lowercase = Pattern.compile("^[\\w_-]+$");
    Logger log = getLogger(ILocalizer.class);
    ILocalizer DEFAULT = new ILocalizer() {
        @Override
        public String localize(String message, DiscordLocale language, Replacement... replacements) {
            return message;
        }

        @Override
        public LocalizationContext context(LocaleProvider provider) {
            return new LocalizationContext(this, provider);
        }

        @Override
        public DiscordLocale getGuildLocale(Guild guild) {
            return defaultLanguage();
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

    LocalizationContext context(LocaleProvider guild);

    DiscordLocale getGuildLocale(Guild guild);

    @NotNull
    @Override
    default Map<DiscordLocale, String> apply(@NotNull String localizationKey) {
        return languages().stream()
                          .collect(Collectors.toMap(lang -> lang, lang -> localizeChecked(localizationKey, lang)));
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
            if (!("false".equals(System.getProperty("cjda.localisation.error.name")) && key.endsWith(".name"))) {
                log.warn("No value for {}. Defaulting to {}", key, localize);
            }
        }

        if (localize.isBlank()) {
            log.warn("Result for key {}@{} is empty.", key, locale);
        }
        log.trace("Localized key {} for {}", key, locale);

        if (key.startsWith("command") && key.endsWith("name")) {
            if (!lowercase.matcher(localize).matches() && localize.toLowerCase().equals(localize)) {
                log.warn("Key {}@{} has invalid value \"{}\"", key, locale, localize);
            }
        }

        if (key.equals(localize) && options.matcher(key).find()) {
            log.warn("Falling back on legacy options code for {}", key);
            return localizeChecked(key.replace(".options.", "."), locale);
        }
        return localize;
    }

    @NotNull
    default LocalizationFunction prefixedLocalizer(String prefix) {
        return key -> languages().stream()
                                 .collect(Collectors.toMap(lang -> lang, lang -> localizeChecked("%s.%s".formatted(prefix, key), lang)));
    }

    default Map<DiscordLocale, String> keyMap(String prefix, String key) {
        return languages().stream()
                          .collect(Collectors.toMap(lang -> lang, lang -> localizeChecked("%s.%s".formatted(prefix, key), lang)));
    }

    String localize(String message, Guild guild, Replacement... replacements);

    Set<DiscordLocale> languages();

    DiscordLocale defaultLanguage();

    default Optional<DiscordLocale> getLanguage(String language) {
        return languages().stream().filter(lang -> lang.getLocale().equalsIgnoreCase(language)).findFirst();
    }

    default String localize(String message, LocaleProvider provider, Replacement... replacements) {
        var optLocale = provider.locale();
        if (optLocale.isEmpty()) {
            return localize(message, defaultLanguage(), replacements);
        }
        var locale = optLocale.get();
        if (provider instanceof GuildLocaleProvider guildProvider) {
            if (locale == DiscordLocale.ENGLISH_US) {
                locale = getGuildLocale(guildProvider.guild());
            }
        }
        return localize(message, locale, replacements);
    }
}
