/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command.dispatching;

import de.chojo.jdautil.command.CommandMeta;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

@FunctionalInterface
public interface PermissionCheck<Meta extends CommandMeta> {
    boolean hasPermission(GenericInteractionCreateEvent event, Meta meta);
}
