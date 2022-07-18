/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command.dispatching;

import de.chojo.jdautil.command.slash.Slash;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;

public record CommandResult<T extends Slash>(GenericCommandInteractionEvent event, boolean failed,
                                             Throwable exception, InteractionContext<T> context) {

    public static <T extends Slash> CommandResult<T> success(GenericCommandInteractionEvent event, InteractionContext<T> context) {
        return new CommandResult<>(event, false, null, context);
    }

    public static <T extends Slash> CommandResult<T> failed(GenericCommandInteractionEvent event, InteractionContext<T> context, Throwable exception) {
        return new CommandResult<>(event, true, exception, context);
    }
}
