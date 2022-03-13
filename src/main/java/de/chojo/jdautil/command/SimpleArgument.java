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

    private SimpleArgument(OptionType type, String name, String description, boolean required, boolean autoComplete) {
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

    public boolean autoComplete() {
        return autoComplete;
    }

    public static Builder builder(OptionType type, String name, String description) {
        return new Builder(type, name, description);
    }

    public static class Builder {
        private final OptionType type;
        private final String name;
        private final String description;
        private boolean required;
        private boolean autoComplete;

        public Builder(OptionType type, String name, String description) {
            this.type = type;
            this.name = name;
            this.description = description;
        }

        public Builder asRequired() {
            required = true;
            return this;
        }

        public Builder withAutoComplete() {
            autoComplete = true;
            return this;
        }

        public SimpleArgument build() {
            return new SimpleArgument(type, name, description, required, autoComplete);
        }
    }
}
