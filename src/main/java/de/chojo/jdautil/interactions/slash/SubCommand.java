/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash;

import de.chojo.jdautil.interactions.slash.structure.Route;
import de.chojo.jdautil.interactions.slash.structure.builder.SubCommandBuilder;
import de.chojo.jdautil.interactions.slash.structure.builder.components.PartialSubCommandBuilder;
import de.chojo.jdautil.interactions.slash.structure.handler.SlashHandler;
import de.chojo.jdautil.interactions.slash.structure.meta.RouteMeta;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;

public class SubCommand implements Route<RouteMeta> {
    private final RouteMeta meta;
    private final SlashHandler handler;
    private final List<Argument> arguments;

    public SubCommand(RouteMeta meta, SlashHandler handler, List<Argument> arguments) {
        this.meta = meta;
        this.handler = handler;
        this.arguments = arguments;
    }

    public static SubCommandBuilder of(String name, String description, SlashHandler handler) {
        return SubCommandBuilder.full(name, description, handler);
    }

    public static PartialSubCommandBuilder of(String name, String description) {
        return SubCommandBuilder.partial(name, description);
    }

    public RouteMeta meta() {
        return meta;
    }

    @Override
    public void onAutoComplete(CommandAutoCompleteInteractionEvent event, EventContext context) {
        handler.onAutoComplete(event, context);
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event, EventContext context) {
        handler.onSlashCommand(event, context);
    }

    public SubcommandData data(ILocalizer localizer) {
        return new SubcommandData(meta.name(), localizer.localize(meta.description()))
                .addOptions(arguments.stream().map(a -> a.data(localizer)).toList());
    }
}
