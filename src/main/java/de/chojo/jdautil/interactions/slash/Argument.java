/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash;

import de.chojo.jdautil.interactions.locale.LocaleChecks;
import de.chojo.jdautil.interactions.locale.LocaleKey;
import de.chojo.jdautil.interactions.slash.structure.builder.argument.ArgumentBuilder;
import de.chojo.jdautil.interactions.slash.structure.builder.ArgumentBuilderImpl;
import de.chojo.jdautil.interactions.slash.structure.builder.argument.IntegerArgumentBuilder;
import de.chojo.jdautil.interactions.slash.structure.builder.argument.NumberArgumentBuilder;
import de.chojo.jdautil.interactions.slash.structure.builder.argument.StringArgumentBuilder;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.localization.util.LocaleProvider;
import de.chojo.jdautil.util.Consumers;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record Argument(OptionType type, String name, String description, boolean required, boolean autoComplete, @NotNull Consumer<OptionData> min, @NotNull Consumer<OptionData> max) {

    /**
     * @deprecated Use {@link #builder(OptionType, String, String)} instead
     */
    @Deprecated(forRemoval = true)
    public static Argument of(OptionType type, String name, String description, boolean required) {
        return new Argument(type, name, description, required, false, Consumers.empty(), Consumers.empty());
    }

    /**
     * @deprecated Use {@link #builder(OptionType, String, String)} instead
     */
    @Deprecated(forRemoval = true)
    public static Argument of(OptionType type, String name, String description) {
        return new Argument(type, name, description, false, false,Consumers.empty(), Consumers.empty());
    }

    public static ArgumentBuilderImpl builder(OptionType type, String name, String description) {
        return new ArgumentBuilderImpl(type, name, description);
    }

    /**
     * @deprecated Replaced by {@link Argument#text(String, String)}
     */
    @Deprecated(forRemoval = true)
    public static StringArgumentBuilder string(String name, String description) {
        return builder(OptionType.STRING, name, description);
    }

    public static StringArgumentBuilder text(String name, String description) {
        return builder(OptionType.STRING, name, description);
    }

    public static IntegerArgumentBuilder integer(String name, String description) {
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

    public static NumberArgumentBuilder number(String name, String description) {
        return builder(OptionType.NUMBER, name, description);
    }

    public static ArgumentBuilder mentionable(String name, String description) {
        return builder(OptionType.MENTIONABLE, name, description);
    }

    private OptionData applySize(OptionData data){
        min().accept(data);
        max().accept(data);
        return data;
    }

    public OptionData data(ILocalizer localizer) {
        return applySize(new OptionData(type, name, localizer.localize(description, LocaleProvider.empty()), required(), autoComplete()));
    }

    public OptionData data(Slash slash, Group group, SubCommand subCommand, ILocalizer localizer) {
        LocaleChecks.checkOptionName(localizer, "command", LocaleKey.name(slash.meta().name(), group.meta().name(), subCommand.meta().name(),"option", name()));
        LocaleChecks.checkOptionDescription(localizer, "command", LocaleKey.description(slash.meta().name(), group.meta().name(), subCommand.meta().name(),"option", name()));

        return data(localizer);
    }

    public OptionData data(Slash slash, SubCommand subCommand, ILocalizer localizer) {
        LocaleChecks.checkOptionName(localizer, "command", LocaleKey.name(slash.meta().name(), subCommand.meta().name(), "option",name()));
        LocaleChecks.checkOptionDescription(localizer, "command", LocaleKey.description(slash.meta().name(), subCommand.meta().name(),"option", name()));

        return data(localizer);
    }

    public OptionData data(Slash slash, ILocalizer localizer) {
        LocaleChecks.checkOptionName(localizer, "command", LocaleKey.name(slash.meta().name(),"option", name()));
        LocaleChecks.checkOptionDescription(localizer, "command", LocaleKey.description(slash.meta().name(), "option",name()));

        return data(localizer);
    }

    public OptionData data() {
        LocaleChecks.checkOptionName(name());
        LocaleChecks.checkOptionDescription(name());
        return applySize(new OptionData(type, name, description, required(), autoComplete()));
    }
}
