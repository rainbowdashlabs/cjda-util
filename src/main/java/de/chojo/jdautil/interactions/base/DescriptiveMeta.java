/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.base;

import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

public abstract class DescriptiveMeta extends InteractionMeta {
    private final String description;

    public DescriptiveMeta(String name, String description, boolean guildOnly, DefaultMemberPermissions permission, InteractionScope scope, boolean localized) {
        super(name, guildOnly, permission, scope, localized);
        this.description = description;
    }

    public String description() {
        return description;
    }
}
