/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash.structure.meta;

import de.chojo.jdautil.interactions.base.DescriptiveMeta;
import de.chojo.jdautil.interactions.base.InteractionScope;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

public class CommandMeta extends DescriptiveMeta {
    public CommandMeta(String name, String description, boolean guildOnly, DefaultMemberPermissions permission, InteractionScope scope, boolean localized) {
        super(name, description, guildOnly, permission, scope, localized);
    }
}
