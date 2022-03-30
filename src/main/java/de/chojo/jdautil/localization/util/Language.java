/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.localization.util;

import java.util.Locale;

public class Language {
    public static final Language ENGLISH = Language.of("en_US", "English");
    public static final Language GERMAN = Language.of("de_DE", "Deutsch");

    String code;
    String language;

    private Language(String code, String language) {
        this.code = code;
        this.language = language;
    }

    public static Language of(String code, String languageName) {
        return new Language(code, languageName);
    }

    public String getCode() {
        return code;
    }

    public String getLanguage() {
        return language;
    }

    public boolean isLanguage(String codeString) {
        if (getCode().equalsIgnoreCase(codeString)) {
            return true;
        }
        return getLanguage().equalsIgnoreCase(codeString);
    }

    public Locale toLocale() {
        var s = getCode().split("_");

        if (s.length == 1) {
            return new Locale(s[0]);
        }
        return new Locale(s[0], s[1]);
    }

    @Override
    public String toString() {
        return "Language{" +
                "code='" + code + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
