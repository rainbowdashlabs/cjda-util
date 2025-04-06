/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.base;

import de.chojo.jdautil.interactions.premium.SKU;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class InteractionMeta implements Meta, SkuMeta {
    private final String name;
    private final Set<InteractionContextType> context;
    private final DefaultMemberPermissions permission;
    private final InteractionScope scope;
    private final boolean localized;
    private final List<SKU> skus;

    public InteractionMeta(String name, Set<InteractionContextType> contextTypes, DefaultMemberPermissions permission, InteractionScope scope, boolean localized, List<SKU> skus) {
        this.name = name;
        this.context = contextTypes;
        this.permission = permission;
        this.scope = scope;
        this.localized = localized;
        this.skus = skus;
    }

    public Set<InteractionContextType> context() {
        return context;
    }

    @Override
    public Collection<SKU> sku() {
        return skus;
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
