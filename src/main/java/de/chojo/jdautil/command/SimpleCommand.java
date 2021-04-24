package de.chojo.jdautil.command;

import de.chojo.jdautil.wrapper.CommandContext;
import de.chojo.jdautil.wrapper.MessageEventWrapper;
import net.dv8tion.jda.api.Permission;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class SimpleCommand {
    private final String command;
    private final String[] alias;
    private final String description;
    private final String args;
    private final SimpleSubCommand[] subCommands;
    private final Permission permission;

    public SimpleCommand(String command, @Nullable String[] alias, String description, @Nullable String args, @Nullable SimpleSubCommand[] subCommands, Permission permission) {
        this.command = command;
        this.alias = alias == null ? new String[0] : alias;
        this.description = description;
        this.args = args;
        this.subCommands = subCommands == null ? new SimpleSubCommand[0] : subCommands;
        this.permission = permission;
    }

    public static SubCommandBuilder subCommandBuilder() {
        return new SubCommandBuilder();
    }

    public String getCommand() {
        return command;
    }

    public String[] getAlias() {
        return alias;
    }

    public String getDescription() {
        return description;
    }

    public @Nullable
    String getArgs() {
        return args;
    }

    public Permission getPermission() {
        return permission;
    }

    public abstract boolean onCommand(MessageEventWrapper eventWrapper, CommandContext context);

    public SimpleSubCommand[] getSubCommands() {
        return subCommands;
    }

    public static class SubCommandBuilder {
        List<SimpleSubCommand> subCommands = new ArrayList<>();

        public SubCommandBuilder add(String name, String args, String description) {
            subCommands.add(new SimpleSubCommand(name, args, description));
            return this;
        }

        public SimpleSubCommand[] build() {
            return subCommands.toArray(new SimpleSubCommand[0]);
        }
    }
}
