/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash.structure.builder;

import de.chojo.jdautil.interactions.slash.Argument;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class ArgumentBuilder {
    private final OptionType type;
    private final String name;
    private final String description;
    private boolean required;
    private boolean autoComplete;

    public ArgumentBuilder(OptionType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public ArgumentBuilder asRequired() {
        required = true;
        return this;
    }

    public ArgumentBuilder withAutoComplete() {
        autoComplete = true;
        return this;
    }

    public Argument build() {
        return new Argument(type, name, description, required, autoComplete);
    }
}
