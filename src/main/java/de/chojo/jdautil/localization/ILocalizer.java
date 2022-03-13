/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.localization;

import de.chojo.jdautil.localization.util.Language;
import de.chojo.jdautil.localization.util.Replacement;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.util.Collections;
import java.util.Set;

public interface ILocalizer {
    ILocalizer DEFAULT = new ILocalizer() {
        @Override
        public String localize(String message, Language language, Replacement... replacements) {
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
        public Language getGuildLocale(Guild guild) {
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
        public Set<Language> languages() {
            return Collections.singleton(Language.ENGLISH);
        }

        @Override
        public Language defaultLanguage() {
            return Language.ENGLISH;
        }
    };

    String localize(String message, Language language, Replacement... replacements);

    ContextLocalizer getContextLocalizer(Guild guild);

    ContextLocalizer getContextLocalizer(CommandInteraction interaction);

    ContextLocalizer getContextLocalizer(MessageChannel channel);

    Language getGuildLocale(Guild guild);

    String localize(String message, CommandInteraction interaction, Replacement... replacements);

    String localize(String message, MessageChannel channel, Replacement... replacements);

    String localize(String message, Replacement... replacements);

    String localize(String message, Guild guild, Replacement... replacements);

    Set<Language> languages();

    Language defaultLanguage();
}
