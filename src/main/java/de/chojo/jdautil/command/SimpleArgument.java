/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.chojo.jdautil.command;

import net.dv8tion.jda.api.interactions.commands.OptionType;

public class SimpleArgument {
    private final OptionType type;
    private final String name;
    private final String description;
    private final boolean required;

    private SimpleArgument(OptionType type, String name, String description, boolean required) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.required = required;
    }

    public static SimpleArgument of(OptionType type, String name, String description, boolean required) {
        return new SimpleArgument(type, name, description, required);
    }

    public static SimpleArgument of(OptionType type, String name, String description) {
        return new SimpleArgument(type, name, description, false);
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public boolean isRequired() {
        return required;
    }

    public OptionType type() {
        return type;
    }
}
