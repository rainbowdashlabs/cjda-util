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

    public static SubCommandBuilder subCommandBuilder() {
        return new SubCommandBuilder();
    }

    public static ArgumentBuilder argsBuilder() {
        return new ArgumentBuilder();
    }

    public String command() {
        return meta.name();
    }

    public String description() {
        return meta.description();
    }

    public SimpleArgument[] args() {
        return meta.argument();
    }

    public SimpleSubCommand[] subCommands() {
        return meta.subCommands();
    }

    public boolean needsPermission() {
        return meta.defaultEnabled();
    }

    public abstract void onSlashCommand(SlashCommandInteractionEvent event, SlashCommandContext context);

    public SimpleSubCommand[] getSubCommands() {
        return meta.subCommands();
    }

    public void onTabcomplete(CommandAutoCompleteInteractionEvent event, SlashCommandContext slashCommandContext) {

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
