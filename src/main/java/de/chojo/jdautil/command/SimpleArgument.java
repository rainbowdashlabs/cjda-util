/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command;

import net.dv8tion.jda.api.interactions.commands.OptionType;

public class SimpleArgument {
    private final OptionType type;
    private final String name;
    private final String description;
    private final boolean required;
    private final boolean autoComplete;

    SimpleArgument(OptionType type, String name, String description, boolean required, boolean autoComplete) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.required = required;
        this.autoComplete = autoComplete;
    }

    /**
     * @deprecated Use {@link #builder(OptionType, String, String)} instead
     */
    @Deprecated(forRemoval = true)
    public static SimpleArgument of(OptionType type, String name, String description, boolean required) {
        return new SimpleArgument(type, name, description, required, false);
    }

    /**
     * @deprecated Use {@link #builder(OptionType, String, String)} instead
     */
    @Deprecated(forRemoval = true)
    public static SimpleArgument of(OptionType type, String name, String description) {
        return new SimpleArgument(type, name, description, false, false);
    }

    public static SimpleArgumentBuilder builder(OptionType type, String name, String description) {
        return new SimpleArgumentBuilder(type, name, description);
    }

    public static SimpleArgumentBuilder string(String name, String description) {
        return builder(OptionType.STRING, name, description);
    }

    public static SimpleArgumentBuilder integer(String name, String description) {
        return builder(OptionType.INTEGER, name, description);
    }

    public static SimpleArgumentBuilder bool(String name, String description) {
        return builder(OptionType.BOOLEAN, name, description);
    }

    public static SimpleArgumentBuilder user(String name, String description) {
        return builder(OptionType.USER, name, description);
    }

    public static SimpleArgumentBuilder role(String name, String description) {
        return builder(OptionType.ROLE, name, description);
    }

    public static SimpleArgumentBuilder attachment(String name, String description) {
        return builder(OptionType.ATTACHMENT, name, description);
    }

    public static SimpleArgumentBuilder channel(String name, String description) {
        return builder(OptionType.CHANNEL, name, description);
    }

    public static SimpleArgumentBuilder number(String name, String description) {
        return builder(OptionType.NUMBER, name, description);
    }

    public static SimpleArgumentBuilder mentionable(String name, String description) {
        return builder(OptionType.MENTIONABLE, name, description);
    }

    public String name() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }

    public String description() {
        return description;
    }

    public OptionType type() {
        return type;
    }

    public boolean autoComplete() {
        return autoComplete;
    }

}
