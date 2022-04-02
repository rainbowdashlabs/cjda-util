/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command;

import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.localization.util.Language;
import de.chojo.jdautil.wrapper.SlashCommandContext;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public abstract class SimpleCommand {
    private final CommandMeta meta;

    protected SimpleCommand(CommandMeta meta) {
        this.meta = meta;
    }

    protected SimpleCommand(CommandMetaBuilder meta) {
        this(meta.build());
    }

    public static SubCommandBuilder subCommandBuilder() {
        return new SubCommandBuilder();
    }

    public static ArgumentBuilder argsBuilder() {
        return new ArgumentBuilder();
    }

    public CommandMeta meta() {
        return meta;
    }

    /**
     * @deprecated Replaced by {@link #meta()}
     */
    @Deprecated(forRemoval = true)
    public String command() {
        return meta.name();
    }

    /**
     * @deprecated Replaced by {@link #meta()}
     */
    @Deprecated(forRemoval = true)
    public String description() {
        return meta.description();
    }

    /**
     * @deprecated Replaced by {@link #meta()}
     */
    @Deprecated(forRemoval = true)
    public SimpleArgument[] args() {
        return meta.argument();
    }

    /**
     * @deprecated Replaced by {@link #meta()}
     */
    @Deprecated(forRemoval = true)
    public SimpleSubCommand[] subCommands() {
        return meta.subCommands();
    }

    /**
     * @deprecated Replaced by {@link #meta()}
     */
    @Deprecated(forRemoval = true)
    public boolean needsPermission() {
        return meta.defaultEnabled();
    }

    /**
     * @deprecated Replaced by {@link #meta()}
     */
    @Deprecated(forRemoval = true)
    public SimpleSubCommand[] getSubCommands() {
        return meta.subCommands();
    }

    public abstract void onSlashCommand(SlashCommandInteractionEvent event, SlashCommandContext context);

    public void onAutoComplete(CommandAutoCompleteInteractionEvent event, SlashCommandContext slashCommandContext) {

    }

    public CommandData getCommandData(ILocalizer localizer, Language lang) {
        return meta.toCommandData(localizer, lang);
    }

    protected Message wrap(MessageEmbed embed) {
        return new MessageBuilder(embed).build();
    }

    protected Message wrap(String message) {
        return new MessageBuilder(message).build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleCommand)) return false;

        SimpleCommand that = (SimpleCommand) o;

        return meta.equals(that.meta);
    }

    @Override
    public int hashCode() {
        return meta.hashCode();
    }
}
