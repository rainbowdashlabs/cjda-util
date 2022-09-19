/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.user;

import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

@FunctionalInterface
public interface UserHandler {
    void onUser(UserContextInteractionEvent event, EventContext context);
}
