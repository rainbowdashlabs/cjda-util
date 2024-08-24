/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash;

import de.chojo.jdautil.interactions.locale.LocaleChecks;
import de.chojo.jdautil.interactions.slash.structure.Route;
import de.chojo.jdautil.interactions.slash.structure.builder.GroupBuilder;
import de.chojo.jdautil.interactions.slash.structure.builder.components.PartialGroupBuilder;
import de.chojo.jdautil.interactions.slash.structure.meta.RouteMeta;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.localization.util.LocaleProvider;
import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class Group implements Route<RouteMeta> {
    private static final Logger log = getLogger(Group.class);
    private final RouteMeta meta;
    private final List<SubCommand> subCommands;

    public Group(RouteMeta routeMeta, List<SubCommand> subCommands) {
        meta = routeMeta;
        this.subCommands = subCommands;
    }

    public static PartialGroupBuilder of(String name, String description) {
        return GroupBuilder.of(name, description);
    }
    public static PartialGroupBuilder group(String name, String description) {
        return of(name, description);
    }

    @Override
    public RouteMeta meta() {
        return meta;
    }

    public void onSlashCommand(SlashCommandInteractionEvent event, EventContext context) {
        var commandPath = event.getFullCommandName().split("\\s");

        if (commandPath.length != 3) {
            log.warn("End of route is reached at {} in group {}", event.getFullCommandName(), meta.name());
            return;
        }

        for (var route : subCommands) {
            if (commandPath[2].equalsIgnoreCase(route.meta().name())) {
                route.onSlashCommand(event, context);
                return;
            }
        }
    }

    public void onAutoComplete(CommandAutoCompleteInteractionEvent event, EventContext context) {
        var commandPath = event.getFullCommandName().split("\\s");

        if (commandPath.length != 3) {
            log.warn("End of route is reached at {} in group {}", event.getFullCommandName(), meta.name());
            return;
        }

        for (var route : subCommands) {
            if (commandPath[2].equalsIgnoreCase(route.meta().name())) {
                route.onAutoComplete(event, context);
                return;
            }
        }
    }

    @Override
    public Collection<Collection<? extends Route<RouteMeta>>> routes() {
        return Collections.singletonList(subCommands);
    }

    public SubcommandGroupData data(Slash slash, ILocalizer localizer) {
        LocaleChecks.checkCommandName(localizer, "command", "%s.%s.name".formatted(slash.meta().name(), meta.name()));
        LocaleChecks.checkCommandDescription(localizer, "command", "%s.%s.description".formatted(slash.meta().name(), meta.name()));
        return new SubcommandGroupData(meta.name(), localizer.localize(meta.description(), LocaleProvider.empty()))
                .addSubcommands(subCommands.stream().map(s -> s.data(slash, this, localizer)).toList());
    }

    public SubcommandGroupData data() {
        LocaleChecks.checkCommandName(meta.name());
        LocaleChecks.checkCommandDescription(meta.name());
        return new SubcommandGroupData(meta.name(), meta.description())
                .addSubcommands(subCommands.stream().map(SubCommand::data).toList());
    }
}
