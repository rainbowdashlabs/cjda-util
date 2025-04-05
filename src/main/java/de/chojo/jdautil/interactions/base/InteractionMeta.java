/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.base;

import net.dv8tion.jda.api.entities.Entitlement;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class InteractionMeta implements Meta, EntitlementMeta {
    private final String name;
    private final Set<InteractionContextType> guildOnly;
    private final DefaultMemberPermissions permission;
    private final InteractionScope scope;
    private final boolean localized;
    private final List<Entitlement> entitlements;

    public InteractionMeta(String name, Set<InteractionContextType> contextTypes, DefaultMemberPermissions permission, InteractionScope scope, boolean localized, List<Entitlement> entitlements) {
        this.name = name;
        this.guildOnly = contextTypes;
        this.permission = permission;
        this.scope = scope;
        this.localized = localized;
        this.entitlements = entitlements;
    }

    public Set<InteractionContextType> contextTypes() {
        return guildOnly;
    }

    @Override
    public Collection<Entitlement> entitlements() {
        return entitlements;
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
