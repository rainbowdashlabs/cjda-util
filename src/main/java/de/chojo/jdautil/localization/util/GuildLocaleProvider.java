/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.localization.util;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.DiscordLocale;

import java.util.Optional;

public interface GuildLocaleProvider extends LocaleProvider{
    Guild guild();

    static GuildLocaleProvider of(Guild guild){
        return new GuildLocaleProvider() {
            @Override
            public Guild guild() {
                return guild;
            }

            @Override
            public Optional<DiscordLocale> locale() {
                return Optional.of(guild.getLocale());
            }
        };
    }
}
