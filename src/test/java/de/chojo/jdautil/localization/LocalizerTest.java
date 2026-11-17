/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.localization;

import de.chojo.jdautil.localization.util.LocaleProvider;
import de.chojo.jdautil.localization.util.UserLocaleProvider;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class LocalizerTest {

    @Test
    void languageProviderWinsOverGuildLocaleOnEveryPath() {
        var localizer = Localizer.builder(DiscordLocale.ENGLISH_US)
                .addLanguage(DiscordLocale.GERMAN)
                .withLanguageProvider(guild -> Optional.of(DiscordLocale.ENGLISH_US))
                .build();
        var guild = germanGuild();

        assertEquals("Hello", localizer.localize("test.greeting", guild));
        assertEquals("Hello", localizer.localize("test.greeting", LocaleProvider.guild(guild)));
    }

    @Test
    void guildLocaleIsUsedWithoutLanguageProvider() {
        var localizer = Localizer.builder(DiscordLocale.ENGLISH_US)
                .addLanguage(DiscordLocale.GERMAN)
                .build();
        var guild = germanGuild();

        assertEquals("Hallo", localizer.localize("test.greeting", guild));
        assertEquals("Hallo", localizer.localize("test.greeting", LocaleProvider.guild(guild)));
    }

    @Test
    void unknownGuildLocaleFallsBackToDefaultLanguage() {
        var localizer = Localizer.builder(DiscordLocale.ENGLISH_US).build();

        assertEquals("Hello", localizer.localize("test.greeting", LocaleProvider.guild(germanGuild())));
    }

    @Test
    void customEmbedCodeIsReplaced() {
        var localizer = Localizer.builder(DiscordLocale.ENGLISH_US)
                .embedCode("\\{", "\\}")
                .build();

        assertEquals("Say Hello!", localizer.localize("Say {test.greeting}!", DiscordLocale.ENGLISH_US, null));
    }

    @Test
    void defaultEmbedCodeIsReplaced() {
        var localizer = Localizer.builder(DiscordLocale.ENGLISH_US).build();

        assertEquals("Say Hello!", localizer.localize("Say $test.greeting$!", DiscordLocale.ENGLISH_US, null));
    }

    @Test
    void emptyProviderUsesDefaultLanguage() {
        var localizer = Localizer.builder(DiscordLocale.ENGLISH_US)
                .addLanguage(DiscordLocale.GERMAN)
                .build();

        assertEquals("Hello", localizer.localize("test.greeting", LocaleProvider.empty()));
    }

    @Test
    void userProviderUsesUserLanguage() {
        var localizer = Localizer.builder(DiscordLocale.ENGLISH_US)
                .addLanguage(DiscordLocale.GERMAN)
                .build();
        var user = Mockito.mock(User.class);

        assertEquals("Hallo", localizer.localize("test.greeting", UserLocaleProvider.of(user, DiscordLocale.GERMAN)));
        assertEquals("Hello", localizer.localize("test.greeting", UserLocaleProvider.of(user, DiscordLocale.FRENCH)));
    }

    @Test
    void guildLocaleCodeProviderOverridesBundle() {
        var localizer = Localizer.builder(DiscordLocale.ENGLISH_US)
                .withGuildLocaleCodeProvider((guild, key) -> Optional.of("Howdy"))
                .build();

        assertEquals("Howdy", localizer.localize("test.greeting", germanGuild()));
    }

    private static Guild germanGuild() {
        var guild = Mockito.mock(Guild.class);
        when(guild.getLocale()).thenReturn(DiscordLocale.GERMAN);
        return guild;
    }
}
