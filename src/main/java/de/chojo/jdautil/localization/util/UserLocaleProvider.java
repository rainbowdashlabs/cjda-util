/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.localization.util;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.DiscordLocale;

import java.util.Optional;

public interface UserLocaleProvider extends LocaleProvider {
    User user();

    static UserLocaleProvider of(User user, DiscordLocale userLocale) {
        return new UserLocaleProvider() {
            @Override
            public User user() {
                return user;
            }

            @Override
            public Optional<DiscordLocale> locale() {
                return Optional.ofNullable(userLocale);
            }
        };
    }
}
