/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

public class CommandMetaBuilder {
    private String name;
    private String description;
    private ArgumentBuilder argument = SimpleCommand.argsBuilder();
    private SubCommandBuilder subCommands = SimpleCommand.subCommandBuilder();
    private DefaultMemberPermissions permission = DefaultMemberPermissions.ENABLED;
    private boolean guildOnly;

    public CommandMetaBuilder(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public CommandMetaBuilder addArgument(SimpleArgument argument) {
        this.argument.add(argument);
        return this;
    }

    public CommandMetaBuilder addArgument(SimpleArgumentBuilder argument) {
        this.argument.add(argument.build());
        return this;
    }

    public CommandMetaBuilder addSubCommand(String name, String description, SimpleArgument... arguments) {
        this.subCommands.add(name, description, arguments);
        return this;
    }

    public CommandMetaBuilder addSubCommand(String name, String description, ArgumentBuilder arguments) {
        this.subCommands.add(name, description, arguments.build());
        return this;
    }

    public CommandMetaBuilder withPermission(Permission... permissions) {
        this.permission = DefaultMemberPermissions.enabledFor(permissions);
        return this;
    }

    /**
     * Marks a command as public command.
     * <p>
     * This is the default value.
     *
     * @return builder
     */
    public CommandMetaBuilder publicCommand() {
        this.permission = DefaultMemberPermissions.ENABLED;
        return this;
    }

    /**
     * Marks a command as admin command.
     * <p>
     * The command will only be accessable for administrators of a guild.
     *
     * @return builder
     */
    public CommandMetaBuilder adminCommand() {
        this.permission = DefaultMemberPermissions.DISABLED;
        return this;
    }

    public CommandMetaBuilder guildOnly() {
        this.guildOnly = true;
        return this;
    }

    public CommandMeta build() {
        return new CommandMeta(name, description, argument.build(), subCommands.build(), permission, guildOnly);
    }
}
