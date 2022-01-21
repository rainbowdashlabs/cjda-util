/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.chojo.jdautil.localization.util;

public enum Format {
    BOLD("**"), ITALIC("*"), UNDERLINE("__"), CODE_BLOCK("```"), CODE("`");

    private final String format;

    Format(String format) {
        this.format = format;
    }

    public String apply(String s) {
        return this.format + s + this.format;
    }
}
