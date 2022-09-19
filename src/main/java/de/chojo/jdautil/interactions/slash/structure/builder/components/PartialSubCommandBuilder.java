/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash.structure.builder.components;

import de.chojo.jdautil.interactions.slash.structure.builder.SubCommandBuilder;
import de.chojo.jdautil.interactions.slash.structure.handler.SlashHandler;
import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.function.BiConsumer;

public interface PartialSubCommandBuilder {
    SubCommandBuilder handler(SlashHandler handler);

    default SubCommandBuilder handler(BiConsumer<SlashCommandInteractionEvent, EventContext> slash, BiConsumer<CommandAutoCompleteInteractionEvent, EventContext> completion) {
        return handler(new SlashHandler() {
            @Override
            public void onSlashCommand(SlashCommandInteractionEvent event, EventContext context) {
                slash.accept(event, context);
            }

            @Override
            public void onAutoComplete(CommandAutoCompleteInteractionEvent event, EventContext context) {
                completion.accept(event, context);
            }
        });
    }
}
