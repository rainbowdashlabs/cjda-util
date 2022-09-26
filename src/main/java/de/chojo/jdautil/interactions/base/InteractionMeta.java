/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.base;

import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

public class InteractionMeta implements Meta {
    private final String name;
    private final boolean guildOnly;
    private final DefaultMemberPermissions permission;
    private final InteractionScope scope;
    private boolean localized;

    public InteractionMeta(String name, boolean guildOnly, DefaultMemberPermissions permission, InteractionScope scope, boolean localized) {
        this.name = name;
        this.guildOnly = guildOnly;
        this.permission = permission;
        this.scope = scope;
        this.localized = localized;
    }

    public boolean isGuildOnly() {
        return guildOnly;
    }

    @Override
    public String name() {
        return name;
    }

    public DefaultMemberPermissions permission() {
        return permission;
    }

    public InteractionScope scope() {
        return scope;
    }

    public boolean localized() {
        return localized;
    }
}
