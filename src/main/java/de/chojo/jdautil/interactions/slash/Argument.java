/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash;

import de.chojo.jdautil.interactions.slash.structure.builder.ArgumentBuilder;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.localization.util.LocaleProvider;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public record Argument(OptionType type, String name, String description, boolean required, boolean autoComplete) {

    /**
     * @deprecated Use {@link #builder(OptionType, String, String)} instead
     */
    @Deprecated(forRemoval = true)
    public static Argument of(OptionType type, String name, String description, boolean required) {
        return new Argument(type, name, description, required, false);
    }

    /**
     * @deprecated Use {@link #builder(OptionType, String, String)} instead
     */
    @Deprecated(forRemoval = true)
    public static Argument of(OptionType type, String name, String description) {
        return new Argument(type, name, description, false, false);
    }

    public static ArgumentBuilder builder(OptionType type, String name, String description) {
        return new ArgumentBuilder(type, name, description);
    }

    /**
     * @deprecated Replaced by {@link Argument#text(String, String)}
     */
    @Deprecated(forRemoval = true)
    public static ArgumentBuilder string(String name, String description) {
        return builder(OptionType.STRING, name, description);
    }

    public static ArgumentBuilder text(String name, String description) {
        return builder(OptionType.STRING, name, description);
    }

    public static ArgumentBuilder integer(String name, String description) {
        return builder(OptionType.INTEGER, name, description);
    }

    public static ArgumentBuilder bool(String name, String description) {
        return builder(OptionType.BOOLEAN, name, description);
    }

    public static ArgumentBuilder user(String name, String description) {
        return builder(OptionType.USER, name, description);
    }

    public static ArgumentBuilder role(String name, String description) {
        return builder(OptionType.ROLE, name, description);
    }

    public static ArgumentBuilder attachment(String name, String description) {
        return builder(OptionType.ATTACHMENT, name, description);
    }

    public static ArgumentBuilder channel(String name, String description) {
        return builder(OptionType.CHANNEL, name, description);
    }

    public static ArgumentBuilder number(String name, String description) {
        return builder(OptionType.NUMBER, name, description);
    }

    public static ArgumentBuilder mentionable(String name, String description) {
        return builder(OptionType.MENTIONABLE, name, description);
    }

    public OptionData data(ILocalizer localizer) {
        return new OptionData(type, name, localizer.localize(description, LocaleProvider.empty()), required(), autoComplete());
    }
}
