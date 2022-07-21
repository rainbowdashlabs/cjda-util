/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.message;

import de.chojo.jdautil.interactions.base.Meta;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

public class MessageMeta implements Meta {

    private final String name;
    private final boolean guildOnly;
    private final DefaultMemberPermissions permission;

    public MessageMeta(String name, boolean guildOnly, DefaultMemberPermissions permission) {
        this.name = name;
        this.guildOnly = guildOnly;
        this.permission = permission;
    }

    @Override
    public String name() {
        return name;
    }

    public boolean isGuildOnly() {
        return guildOnly;
    }

    public DefaultMemberPermissions permission() {
        return permission;
    }
}
