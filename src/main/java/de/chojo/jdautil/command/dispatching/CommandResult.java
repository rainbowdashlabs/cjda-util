/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command.dispatching;

import de.chojo.jdautil.command.SimpleCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public record CommandResult<T extends SimpleCommand>(SlashCommandInteractionEvent event, boolean failed,
                                                     Throwable exception, CommandExecutionContext<T> context) {

    public static <T extends SimpleCommand> CommandResult<T> success(SlashCommandInteractionEvent event, CommandExecutionContext<T> context) {
        return new CommandResult<>(event, false, null, context);
    }

    public static <T extends SimpleCommand> CommandResult<T> failed(SlashCommandInteractionEvent event, CommandExecutionContext<T> context, Throwable exception) {
        return new CommandResult<>(event, true, exception, context);
    }
}
