package de.chojo.jdautil.command;

import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.localization.Localizer;
import de.chojo.jdautil.wrapper.CommandContext;
import de.chojo.jdautil.wrapper.MessageEventWrapper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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

    public abstract boolean onCommand(MessageEventWrapper eventWrapper, CommandContext context);

    public abstract void onSlashCommand(SlashCommandEvent event);

    public SimpleSubCommand[] getSubCommands() {
        return subCommands;
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

    public CommandData getCommandData(ILocalizer localizer) {
        var commandData = new CommandData(command, localizer.localize(description));
        if (subCommands() != null) {
            List<SubcommandData> subcommands = new ArrayList<>(subCommands().length);
            for (var subCommand : getSubCommands()) {
                var subCmdData = new SubcommandData(subCommand.name(), localizer.localize(subCommand.description()));
                for (var arg : subCommand.args()) {
                    subCmdData.addOption(arg.type(), arg.name(), localizer.localize(arg.description()), arg.isRequired());
                }
            }
            commandData.addSubcommands(subcommands);
        } else if (args() != null) {
            for (var arg : args()) {
                commandData.addOption(arg.type(), arg.name(), localizer.localize(arg.description()), arg.isRequired());
            }
        }

        if (args() != null && subCommands() != null) {
            throw new IllegalStateException("Commands can't have subcommands and arguments... Sorry.");
        }

        return commandData;
    }
}
