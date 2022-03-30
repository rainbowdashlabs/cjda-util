/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command;

import net.dv8tion.jda.api.interactions.commands.OptionType;

public class SimpleArgumentBuilder {
    private final OptionType type;
    private final String name;
    private final String description;
    private boolean required;
    private boolean autoComplete;

    public SimpleArgumentBuilder(OptionType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public SimpleArgumentBuilder asRequired() {
        required = true;
        return this;
    }

    public SimpleArgumentBuilder withAutoComplete() {
        autoComplete = true;
        return this;
    }

    public SimpleArgument build() {
        return new SimpleArgument(type, name, description, required, autoComplete);
    }
}
