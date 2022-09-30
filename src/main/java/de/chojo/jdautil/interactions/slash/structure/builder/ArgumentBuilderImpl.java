/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash.structure.builder;

import de.chojo.jdautil.interactions.slash.Argument;
import de.chojo.jdautil.interactions.slash.structure.builder.argument.CompletableArgumentBuilder;
import de.chojo.jdautil.interactions.slash.structure.builder.argument.IntegerArgumentBuilder;
import de.chojo.jdautil.interactions.slash.structure.builder.argument.NumberArgumentBuilder;
import de.chojo.jdautil.interactions.slash.structure.builder.argument.StringArgumentBuilder;
import de.chojo.jdautil.util.Consumers;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.function.Consumer;

public class ArgumentBuilderImpl implements StringArgumentBuilder, IntegerArgumentBuilder, NumberArgumentBuilder, CompletableArgumentBuilder {
    private final OptionType type;
    private final String name;
    private final String description;
    private boolean required;
    private boolean autoComplete;
    private Consumer<OptionData> min = Consumers.empty();
    private Consumer<OptionData> max = Consumers.empty();

    public ArgumentBuilderImpl(OptionType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    @Override
    public ArgumentBuilderImpl asRequired() {
        required = true;
        return this;
    }

    @Override
    public ArgumentBuilderImpl withAutoComplete() {
        autoComplete = true;
        return this;
    }

    @Override
    public ArgumentBuilderImpl minLength(int min) {
        this.min = opt -> opt.setMinLength(min);
        return this;
    }

    @Override
    public ArgumentBuilderImpl maxLength(int max) {
        this.max = opt -> opt.setMaxLength(max);
        return this;
    }

    @Override
    public ArgumentBuilderImpl min(long min) {
        this.min = opt -> opt.setMinValue(min);
        return this;
    }

    @Override
    public ArgumentBuilderImpl max(long max) {
        this.max = opt -> opt.setMaxValue(max);
        return this;
    }

    @Override
    public ArgumentBuilderImpl min(double min) {
        this.min = opt -> opt.setMinValue(min);
        return this;
    }

    @Override
    public ArgumentBuilderImpl max(double max) {
        this.max = opt -> opt.setMaxValue(max);
        return this;
    }

    public Argument build() {
        return new Argument(type, name, description, required, autoComplete, min, max);
    }
}
