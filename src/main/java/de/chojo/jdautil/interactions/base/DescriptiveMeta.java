/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.base;

import net.dv8tion.jda.api.entities.Entitlement;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

import java.util.List;
import java.util.Set;

public abstract class DescriptiveMeta extends InteractionMeta {
    private final String description;

    public DescriptiveMeta(String name, String description, Set<InteractionContextType> contextTypes, DefaultMemberPermissions permission, InteractionScope scope, boolean localized, List<Entitlement> entitlements) {
        super(name, contextTypes, permission, scope, localized, entitlements);
        this.description = description;
    }

    public String description() {
        return description;
    }
}
