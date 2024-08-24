/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.localization.util;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.Interaction;

import java.util.Optional;

public interface LocaleProvider {

    static LocaleProvider user(Interaction interaction) {
        if (interaction == null) return empty();
        return UserLocaleProvider.of(interaction.getUser(), interaction.getUserLocale());
    }

    static LocaleProvider guild(Interaction interaction) {
        if (interaction == null) return empty();
        return interaction.isFromGuild() ? GuildLocaleProvider.of(interaction.getGuild()) : UserLocaleProvider.of(interaction.getUser(), interaction.getUserLocale());
    }

    static LocaleProvider guild(Guild guild) {
        if (guild == null) return empty();
        return GuildLocaleProvider.of(guild);
    }

    static LocaleProvider channel(GuildChannel channel) {
        if (channel == null) return empty();
        return GuildLocaleProvider.of(channel.getGuild());
    }

    static LocaleProvider channel(Channel channel) {
        if (channel instanceof GuildChannel c) {
            return channel(c);
        }
        return Optional::empty;
    }

    static LocaleProvider empty() {
        return Optional::empty;
    }

    static LocaleProvider guild(Message message) {
        return message.isFromGuild() ? guild(message.getGuild()) : empty();
    }

    Optional<DiscordLocale> locale();
}
