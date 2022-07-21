/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash;

import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface SlashHandler extends SlashCommandHandler, AutoCompleteHandler {
    static SlashHandler of(SlashCommandHandler slash, AutoCompleteHandler completion) {
        return new SlashHandler() {
            @Override
            public void onSlashCommand(SlashCommandInteractionEvent event, EventContext context) {
                slash.onSlashCommand(event, context);
            }

            @Override
            public void onAutoComplete(CommandAutoCompleteInteractionEvent event, EventContext context) {
                completion.onAutoComplete(event, context);
            }
        };
    }
}
