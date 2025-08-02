/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash.structure.meta;

import de.chojo.jdautil.interactions.base.DescriptiveMeta;
import de.chojo.jdautil.interactions.base.InteractionScope;
import de.chojo.jdautil.interactions.premium.SKU;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

import java.util.List;
import java.util.Set;

public class CommandMeta extends DescriptiveMeta {
    public CommandMeta(String name, String description, Set<InteractionContextType> context, DefaultMemberPermissions permission, InteractionScope scope, boolean localized, List<SKU> skus) {
        super(name, description, context, permission, scope, localized, skus);
    }
}
