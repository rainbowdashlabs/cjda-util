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
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class SimpleCommand {
    private final String command;
    private final String[] alias;
    private final String description;
    private final SimpleArgument[] args;
    private final SimpleSubCommand[] subCommands;
    private final Permission permission;

    protected SimpleCommand(String command, @Nullable String[] alias, String description, @Nullable SimpleSubCommand[] subCommands, Permission permission) {
        this.command = command;
        this.alias = alias == null ? new String[0] : alias;
        this.description = description;
        this.args = null;
        this.subCommands = subCommands == null ? new SimpleSubCommand[0] : subCommands;
        this.permission = permission;
    }

    protected SimpleCommand(String command, @Nullable String[] alias, String description, @Nullable SimpleArgument[] args, Permission permission) {
        this.command = command;
        this.alias = alias == null ? new String[0] : alias;
        this.description = description;
        this.args = args;
        this.subCommands = null;
        this.permission = permission;
    }

    public static SubCommandBuilder subCommandBuilder() {
        return new SubCommandBuilder();
    }

    public static ArgumentBuilder argsBuilder() {
        return new ArgumentBuilder();
    }

    public String command() {
        return command;
    }

    public String[] alias() {
        return alias;
    }

    public String description() {
        return description;
    }

    public SimpleArgument[] args() {
        return args;
    }

    public SimpleSubCommand[] subCommands() {
        return subCommands;
    }

    public Permission permission() {
        return permission;
    }

    public abstract void onSlashCommand(SlashCommandInteractionEvent event, SlashCommandContext context);

    public SimpleSubCommand[] getSubCommands() {
        return subCommands;
    }

    public void onTabcomplete(CommandAutoCompleteInteractionEvent event, SlashCommandContext slashCommandContext) {

    }

    public static class SubCommandBuilder {
        List<SimpleSubCommand> subCommands = new ArrayList<>();

        public SubCommandBuilder add(String name, String description, SimpleArgument... args) {
            subCommands.add(new SimpleSubCommand(name, args, description));
            return this;
        }

        public SimpleSubCommand[] build() {
            return subCommands.toArray(new SimpleSubCommand[0]);
        }
    }

    public static class ArgumentBuilder {
        List<SimpleArgument> arguments = new ArrayList<>();

        @Deprecated(forRemoval = true)
        public ArgumentBuilder add(OptionType type, String name, String description, boolean required) {
            arguments.add(SimpleArgument.of(type, name, description, required));
            return this;
        }

        public ArgumentBuilder add(OptionType type, String name, String description) {
            arguments.add(SimpleArgument.builder(type, name, description).build());
            return this;
        }

        public ArgumentBuilder add(OptionType type, String name, String description, Consumer<SimpleArgument.Builder> modify) {
            var builder = SimpleArgument.builder(type, name, description);
            modify.accept(builder);
            arguments.add(builder.build());
            return this;
        }

        public SimpleArgument[] build() {
            return arguments.toArray(new SimpleArgument[0]);
        }
    }

    public CommandData getCommandData(ILocalizer localizer, Language lang) {
        var commandData = Commands.slash(command, localizer.localize(description, lang));
        if (subCommands() != null) {
            List<SubcommandData> subcommands = new ArrayList<>(subCommands().length);
            for (var subCommand : getSubCommands()) {
                var subCmdData = new SubcommandData(subCommand.name(), localizer.localize(subCommand.description(), lang));
                for (var arg : subCommand.args()) {
                    subCmdData.addOption(arg.type(), arg.name(), localizer.localize(arg.description(), lang), arg.isRequired());
                }
                subcommands.add(subCmdData);
            }
            commandData.addSubcommands(subcommands);
        } else if (args() != null) {
            for (var arg : args()) {
                commandData.addOption(arg.type(), arg.name(), localizer.localize(arg.description(), lang), arg.isRequired(), arg.autoComplete());
            }
        }

        if (args() != null && subCommands() != null) {
            throw new IllegalStateException("Commands can't have subcommands and arguments... Sorry.");
        }

        commandData.setDefaultEnabled(permission == Permission.UNKNOWN);

        return commandData;
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
        if (o == null || getClass() != o.getClass()) return false;

        var that = (SimpleCommand) o;

        if (!Objects.equals(command, that.command)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(alias, that.alias)) return false;
        if (!Objects.equals(description, that.description)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(args, that.args)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(subCommands, that.subCommands)) return false;
        return permission == that.permission;
    }

    @Override
    public int hashCode() {
        int result = command != null ? command.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(alias);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(args);
        result = 31 * result + Arrays.hashCode(subCommands);
        result = 31 * result + (permission != null ? permission.hashCode() : 0);
        return result;
    }
}
