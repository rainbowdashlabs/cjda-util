/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command.slash;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

public class SlashMetaBuilder {
    private String name;
    private String description;
    private ArgumentsBuilder argument = Slash.argsBuilder();
    private SubSlashBuilder subCommands = Slash.subCommandBuilder();
    private DefaultMemberPermissions permission = DefaultMemberPermissions.ENABLED;
    private boolean guildOnly;

    public SlashMetaBuilder(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public SlashMetaBuilder addArgument(Argument argument) {
        this.argument.add(argument);
        return this;
    }

    public SlashMetaBuilder addArgument(ArgumentBuilder argument) {
        this.argument.add(argument.build());
        return this;
    }

    public SlashMetaBuilder addSubCommand(String name, String description, Argument... arguments) {
        this.subCommands.add(name, description, arguments);
        return this;
    }

    public SlashMetaBuilder addSubCommand(String name, String description, ArgumentsBuilder arguments) {
        this.subCommands.add(name, description, arguments.build());
        return this;
    }

    public SlashMetaBuilder withPermission(Permission... permissions) {
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
    public SlashMetaBuilder publicCommand() {
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
    public SlashMetaBuilder adminCommand() {
        this.permission = DefaultMemberPermissions.DISABLED;
        return this;
    }

    public SlashMetaBuilder guildOnly() {
        this.guildOnly = true;
        return this;
    }

    public SlashMeta build() {
        return new SlashMeta(name, description, argument.build(), subCommands.build(), permission, guildOnly);
    }
}
