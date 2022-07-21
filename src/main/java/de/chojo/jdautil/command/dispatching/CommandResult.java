/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command.dispatching;

import de.chojo.jdautil.command.slash.structure.Command;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;

public record CommandResult<T extends Command>(GenericCommandInteractionEvent event, boolean failed,
                                               Throwable exception, InteractionContext context) {

    public static <T extends Command> CommandResult<T> success(GenericCommandInteractionEvent event, InteractionContext context) {
        return new CommandResult<>(event, false, null, context);
    }

    public static <T extends Command> CommandResult<T> failed(GenericCommandInteractionEvent event, InteractionContext context, Throwable exception) {
        return new CommandResult<>(event, true, exception, context);
    }
}
