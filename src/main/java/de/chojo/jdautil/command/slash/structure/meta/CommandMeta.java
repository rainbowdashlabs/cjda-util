/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command.slash.structure.meta;

import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

public class CommandMeta extends RouteMeta {
    private final DefaultMemberPermissions permission;
    private final boolean guildOnly;

    public CommandMeta(String name, String description, DefaultMemberPermissions permission, boolean guildOnly) {
        super(name, description);
        this.permission = permission;
        this.guildOnly = guildOnly;
    }

    public DefaultMemberPermissions permission() {
        return permission;
    }

    public boolean isGuildOnly() {
        return guildOnly;
    }
}
