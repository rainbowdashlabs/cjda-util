/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command;

import de.chojo.jdautil.command.dispatching.CommandPermissionCheck;
import de.chojo.jdautil.command.dispatching.ManagerRoles;

public class CommandMetaBuilder {
    private String name;
    private String description;
    private ArgumentBuilder argument = SimpleCommand.argsBuilder();
    private SubCommandBuilder subCommands = SimpleCommand.subCommandBuilder();
    private boolean defaultEnabled = true;
    private CommandPermissionCheck permissionCheck;
    private ManagerRoles managerRoles;

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

    public CommandMetaBuilder withPermission() {
        this.defaultEnabled = false;
        return this;
    }

    public CommandMetaBuilder withPermissionCheck(CommandPermissionCheck permissionCheck) {
        this.permissionCheck = permissionCheck;
        return withPermission();
    }

    public CommandMetaBuilder withManagerRoles(ManagerRoles managerRoles) {
        this.managerRoles = managerRoles;
        return withPermission();
    }

    public CommandMeta build() {
        return new CommandMeta(name, description, argument.build(), subCommands.build(), defaultEnabled, permissionCheck, managerRoles);
    }
}
