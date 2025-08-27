/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash;

import de.chojo.jdautil.interactions.base.CommandDataProvider;
import de.chojo.jdautil.interactions.locale.LocaleChecks;
import de.chojo.jdautil.interactions.slash.structure.Route;
import de.chojo.jdautil.interactions.slash.structure.builder.SlashBuilder;
import de.chojo.jdautil.interactions.slash.structure.builder.components.RootMetaBuilder;
import de.chojo.jdautil.interactions.slash.structure.handler.SlashHandler;
import de.chojo.jdautil.interactions.slash.structure.meta.CommandMeta;
import de.chojo.jdautil.interactions.slash.structure.meta.RouteMeta;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.localization.util.LocaleProvider;
import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.List;

import static de.chojo.jdautil.util.Premium.checkAndReplyPremium;
import static de.chojo.jdautil.util.Premium.isNotEntitled;
import static de.chojo.jdautil.util.Premium.replyPremium;
import static org.slf4j.LoggerFactory.getLogger;

public class Slash implements CommandDataProvider {
    private static final Logger log = getLogger(Slash.class);
    private final SlashHandler handler;
    private final List<Group> groups;
    private final List<SubCommand> leaves;
    private final List<Argument> arguments;
    private final CommandMeta meta;

    public Slash(CommandMeta meta, SlashHandler handler, List<Group> groups, List<SubCommand> leaves, List<Argument> arguments) {
        this.meta = meta;
        this.handler = handler;
        this.groups = groups;
        this.leaves = leaves;
        this.arguments = arguments;
    }

    public static RootMetaBuilder of(String name, String description) {
        return new SlashBuilder(name, description);
    }

    public static RootMetaBuilder slash(String name, String description) {
        return of(name, description);
    }

    public Collection<Collection<? extends Route<RouteMeta>>> routes() {
        return List.of(groups, leaves);
    }

    @Override
    public CommandMeta meta() {
        return meta;
    }

    public void onSlashCommand(SlashCommandInteractionEvent event, EventContext context) {

        if (handler != null) {
            if (checkAndReplyPremium(context, meta)) {
                return;
            }

            handler.onSlashCommand(event, context);
            return;
        }

        var commandPath = event.getFullCommandName().split("\\s");

        if (commandPath.length == 1) {
            log.warn("End of route is reached at {} in {}", event.getFullCommandName(), meta.name());
            return;
        }

        for (var routeGroup : routes()) {
            for (Route<RouteMeta> route : routeGroup) {
                if (commandPath[1].equalsIgnoreCase(route.meta().name())) {
                    route.onSlashCommand(event, context);
                    return;
                }
            }
        }
    }

    public void onAutoComplete(CommandAutoCompleteInteractionEvent event, EventContext context) {

        if (handler != null) {
            if (isNotEntitled(context, meta)) {
                event.replyChoices().complete();
                return;
            }
            handler.onAutoComplete(event, context);
            return;
        }

        var commandPath = event.getFullCommandName().split("\\s");

        if (commandPath.length == 1) {
            log.warn("End of route is reached at {} in {}", event.getFullCommandName(), meta.name());
            return;
        }

        for (var routeGroup : routes()) {
            for (Route<RouteMeta> route : routeGroup) {
                if (commandPath[1].equalsIgnoreCase(route.meta().name())) {
                    route.onAutoComplete(event, context);
                    return;
                }
            }
        }
    }

    @Override
    public CommandData toCommandData(ILocalizer localizer) {
        if (meta.localized()) {
            LocaleChecks.checkCommandName(localizer, "command", "%s.name".formatted(meta.name()));
            LocaleChecks.checkCommandDescription(localizer, "command", "%s.description".formatted(meta.name()));

            var slash = Commands.slash(meta.name(), localizer.localize(meta.description(), LocaleProvider.empty()))
                                .setDefaultPermissions(meta.permission())
                                .setContexts(meta.context())
                                .setLocalizationFunction(localizer.prefixedLocalizer("command"));
            if (!groups.isEmpty())
                slash.addSubcommandGroups(groups.stream().map(g -> g.data(this, localizer)).toList());
            if (!leaves.isEmpty()) slash.addSubcommands(leaves.stream().map(s -> s.data(this, localizer)).toList());
            if (!arguments.isEmpty()) slash.addOptions(arguments.stream().map(a -> a.data(this, localizer)).toList());
            return slash;
        }
        LocaleChecks.checkCommandName(meta.name());
        LocaleChecks.checkCommandDescription(meta.description());

        var slash = Commands.slash(meta.name(), meta.description())
                            .setDefaultPermissions(meta.permission())
                            .setContexts(meta.context());
        if (!groups.isEmpty()) slash.addSubcommandGroups(groups.stream().map(Group::data).toList());
        if (!leaves.isEmpty()) slash.addSubcommands(leaves.stream().map(SubCommand::data).toList());
        if (!arguments.isEmpty()) slash.addOptions(arguments.stream().map(Argument::data).toList());
        return slash;
    }
}
