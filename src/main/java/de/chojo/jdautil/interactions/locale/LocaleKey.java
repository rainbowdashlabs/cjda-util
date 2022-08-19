/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.locale;

public final class LocaleKey {
    private LocaleKey() {
        throw new UnsupportedOperationException("This is a utility class.");
    }

    public static String name(String... args){
        return "%s.name".formatted(String.join(".", args));
    }
    public static String description(String... args){
        return "%s.description".formatted(String.join(".", args));
    }
}
