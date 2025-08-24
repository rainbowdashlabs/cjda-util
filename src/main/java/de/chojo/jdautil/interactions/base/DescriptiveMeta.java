/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.base;

import de.chojo.jdautil.interactions.premium.SKU;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

import java.util.List;
import java.util.Set;

public abstract class DescriptiveMeta extends InteractionMeta {
    private final String description;

    public DescriptiveMeta(String name, String description, Set<InteractionContextType> contextTypes, DefaultMemberPermissions permission, InteractionScope scope, boolean localized, List<SKU> skus) {
        super(name, contextTypes, permission, scope, localized, skus);
        this.description = description;
    }

    public String description() {
        return description;
    }
}
