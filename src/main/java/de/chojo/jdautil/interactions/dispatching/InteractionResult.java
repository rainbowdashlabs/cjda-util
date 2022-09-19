/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.dispatching;

import de.chojo.jdautil.interactions.slash.Slash;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;

public record InteractionResult<T extends Slash>(GenericCommandInteractionEvent event, boolean failed,
                                                 Throwable exception, InteractionContext context) {

    public static <T extends Slash> InteractionResult<T> success(GenericCommandInteractionEvent event, InteractionContext context) {
        return new InteractionResult<>(event, false, null, context);
    }

    public static <T extends Slash> InteractionResult<T> failed(GenericCommandInteractionEvent event, InteractionContext context, Throwable exception) {
        return new InteractionResult<>(event, true, exception, context);
    }
}
