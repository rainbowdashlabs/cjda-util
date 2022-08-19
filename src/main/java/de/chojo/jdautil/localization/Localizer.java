/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.localization;

import de.chojo.jdautil.localization.util.LocaleProvider;
import de.chojo.jdautil.localization.util.Replacement;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

import static org.slf4j.LoggerFactory.getLogger;


public class Localizer implements ILocalizer {
    private static final Pattern LOCALIZATION_CODE = Pattern.compile("\\$([a-zA-Z.]+?)\\$");
    private static final Pattern SIMPLE_LOCALIZATION_CODE = Pattern.compile("^([a-zA-Z]+?\\.[a-zA-Z.]+)$");
    private static final Logger log = getLogger(Localizer.class);
    private final Map<DiscordLocale, ResourceBundle> languages;
    private final Function<Guild, Optional<DiscordLocale>> languageProvider;
    private final DiscordLocale defaultLanguage;

    public Localizer(Map<DiscordLocale, ResourceBundle> languages, Function<Guild, Optional<DiscordLocale>> languageProvider, DiscordLocale defaultLanguage) {
        this.languages = languages;
        this.languageProvider = languageProvider;
        this.defaultLanguage = defaultLanguage;
    }

    public static Builder builder(DiscordLocale defaultLanguage) {
        return new Builder(defaultLanguage);
    }

    private ResourceBundle getLanguageResource(DiscordLocale localeCode) {
        return languages.getOrDefault(localeCode, languages.get(defaultLanguage));
    }

    /**
     * Get the language string of the locale code.
     *
     * @param language  language
     * @param localetag locale code
     * @return message in the local code or the default language if key is missing.
     */
    public String getLanguageString(DiscordLocale language, String localetag) {
        if (getLanguageResource(language).containsKey(localetag)) {
            return getLanguageResource(language).getString(localetag);
        }
        log.warn("Missing localization for key: {} in language pack: {}. Using Fallback Language en_US",
                localetag, language);
        var bundle = getLanguageResource(defaultLanguage);

        if (!bundle.containsKey(localetag)) {
            log.warn("Missing localisation for key {} in fallback language. Is this intended?", localetag);
        }

        return bundle.containsKey(localetag) ? bundle.getString(localetag) : localetag;
    }

    @Override
    public DiscordLocale getGuildLocale(Guild guild) {
        if (guild == null) {
            return defaultLanguage;
        }
        var guildLocale = languageProvider.apply(guild);
        return guildLocale.orElseGet(guild::getLocale);
    }

    public Set<DiscordLocale> getLanguages() {
        return languages.keySet();
    }

    @Override
    public String localize(String message, Guild guild, Replacement... replacements) {
        var language = getGuildLocale(guild);
        return localize(message, language, replacements);
    }

    @Override
    public Set<DiscordLocale> languages() {
        return languages.keySet();
    }

    @Override
    public DiscordLocale defaultLanguage() {
        return defaultLanguage;
    }

    @Override
    public String localize(String message, DiscordLocale language, Replacement... replacements) {
        if (!languages.containsKey(language)) {
            language = defaultLanguage();
        }
        return translate(message, language, replacements);
    }

    private String translate(String message, DiscordLocale language, Replacement... replacements) {
        if (message == null) {
            return null;
        }
        String result;
        // If the matcher doesn't find any key we assume its a simple message.
        if (!LOCALIZATION_CODE.matcher(message).find()) {
            if (!SIMPLE_LOCALIZATION_CODE.matcher(message).find()) {
                result = message;
            } else {
                result = getLanguageString(language, message);
            }
        } else {
            // find locale codes in message
            var matcher = LOCALIZATION_CODE.matcher(message);
            var keys = new ArrayList<String>();
            while (matcher.find()) {
                keys.add(matcher.group(1));
            }

            result = message;

            for (var match : keys) {
                //Replace current placeholders with replacements
                var languageString = getLanguageString(language, match);
                result = result.replace("$" + match + "$", languageString);
            }
        }
        for (var replacement : replacements) {
            result = replacement.invoke(result);
        }

        if (LOCALIZATION_CODE.matcher(result).find()) {
            return localize(result, language, replacements);
        }

        if (result.isBlank()) {
            log.warn("Result for key {}@{} is empty.", message, language);
        }

        return result;
    }

    @Override
    public LocalizationContext context(LocaleProvider provider) {
        return new LocalizationContext(this, provider);
    }

    public DiscordLocale getDefaultLanguage() {
        return defaultLanguage;
    }

    public static class Builder {
        private final Set<DiscordLocale> languages = new HashSet<>();
        private final Map<DiscordLocale, ResourceBundle> resourceBundles = new EnumMap<>(DiscordLocale.class);
        private final DiscordLocale defaultLanguage;
        private String bundlePath = "locale";
        private Function<Guild, Optional<DiscordLocale>> languageProvider;

        public Builder(DiscordLocale defaultLanguage) {
            this.defaultLanguage = defaultLanguage;
            languages.add(defaultLanguage);
            languageProvider = s -> Optional.of(defaultLanguage);
        }

        public Builder withBundlePath(String bundlePath) {
            this.bundlePath = bundlePath;
            return this;
        }

        public Builder withLanguageProvider(Function<Guild, Optional<DiscordLocale>> languageProvider) {
            this.languageProvider = languageProvider;
            return this;
        }

        public Builder addLanguage(DiscordLocale... languages) {
            this.languages.addAll(Arrays.asList(languages));
            return this;
        }

        public Builder addLanguage(DiscordLocale discordLocale) {
            this.languages.add(discordLocale);
            return this;
        }

        public Localizer build() {
            loadLanguages();
            return new Localizer(resourceBundles, languageProvider, defaultLanguage);
        }


        private void loadLanguages() {
            for (var lang : languages) {
                var locale = Locale.forLanguageTag(lang.getLocale());
                var bundle = ResourceBundle.getBundle(bundlePath, locale);
                resourceBundles.put(lang, bundle);
            }

            log.debug("Loaded {} languages!", languages.size());

            Set<String> keySet = new HashSet<>();
            for (var resourceBundle : resourceBundles.values()) {
                keySet.addAll(resourceBundle.keySet());
            }

            List<String> missingKeys = new ArrayList<>();
            for (var resourceBundle : resourceBundles.values()) {
                for (var key : keySet) {
                    if (!resourceBundle.containsKey(key)) {
                        missingKeys.add(key + "@" + resourceBundle.getLocale());
                    }
                }
            }

            if (!missingKeys.isEmpty()) {
                log.warn("Found missing keys in language packs\n{}", String.join("\n", missingKeys));
            }
        }
    }
}
