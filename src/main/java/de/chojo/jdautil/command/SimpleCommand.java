package de.chojo.jdautil.command;

import de.chojo.jdautil.wrapper.CommandContext;
import de.chojo.jdautil.wrapper.MessageEventWrapper;
import net.dv8tion.jda.api.Permission;

public abstract class SimpleCommand {
    private final String command;
    private final String[] alias;
    private final String description;
    private final String usage;
    private final Permission permission;

    public SimpleCommand(String command, String[] alias, String description, String usage, Permission permission) {
        this.command = command;
        this.alias = alias == null ? new String[0] : alias;
        this.description = description;
        this.usage = usage;
        this.permission = permission;
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

    public String getUsage() {
        return usage;
    }

    public Permission getPermission() {
        return permission;
    }

    public abstract boolean onCommand(MessageEventWrapper eventWrapper, CommandContext context);
}
