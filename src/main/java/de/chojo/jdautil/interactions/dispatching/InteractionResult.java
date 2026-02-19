/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.dispatching;

import de.chojo.jdautil.interactions.slash.Slash;
import de.chojo.jdautil.util.SlashCommandUtil;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;

public record InteractionResult(GenericCommandInteractionEvent event, boolean failed,
                                                 Throwable exception, InteractionContext context) {

    public static InteractionResult success(GenericCommandInteractionEvent event, InteractionContext context) {
        return new InteractionResult(event, false, null, context);
    }

    public static InteractionResult failed(GenericCommandInteractionEvent event, InteractionContext context, Throwable exception) {
        return new InteractionResult(event, true, exception, context);
    }

    public String commandAsString() {
        return SlashCommandUtil.commandAsString(event);
    }
}
