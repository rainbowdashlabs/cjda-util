/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.message;

import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;

@FunctionalInterface
public interface MessageHandler {
    void onMessage(MessageContextInteractionEvent event, EventContext context);
}
