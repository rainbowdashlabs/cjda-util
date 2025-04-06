/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash.structure;

import de.chojo.jdautil.interactions.slash.structure.handler.SlashHandler;
import de.chojo.jdautil.interactions.slash.structure.meta.RouteMeta;
import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Collections;

import static de.chojo.jdautil.util.Premium.isNotEntitled;
import static org.slf4j.LoggerFactory.getLogger;

public interface Route<T extends RouteMeta> extends SlashHandler {
    Logger log = getLogger(Route.class);

    T meta();

    default boolean isRoute(String name) {
        return name.equalsIgnoreCase(meta().name());
    }

    @Override
    default void onSlashCommand(SlashCommandInteractionEvent event, EventContext context) {
        var commandPath = event.getFullCommandName().split("\\s?%s\\s?".formatted(meta().name()));

        if (commandPath.length != 2) {
            log.warn("End of route is reached on a branch at {}.", event.getFullCommandName());
            return;
        }

        for (var routes : routes()) {
            for (var route : routes) {
                if (route.isRoute(commandPath[1])) {

                    route.onSlashCommand(event, context);
                    return;
                }
            }
        }

        log.warn("No matching route found for {}.", event.getFullCommandName());
    }

    @Override
    default void onAutoComplete(CommandAutoCompleteInteractionEvent event, EventContext context) {
        var commandPath = event.getFullCommandName().split("\\s?%s\\s?".formatted(meta().name()));

        if (commandPath.length != 2) {
            log.warn("End of route is reached on a branch at {}", event.getFullCommandName());
            return;
        }

        for (var routes : routes()) {
            for (var route : routes) {
                if (route.isRoute(commandPath[1])) {

                    if (isNotEntitled(event, route.meta())) return;

                    route.onAutoComplete(event, context);
                    return;
                }
            }
        }
        log.warn("No matching route found for {}.", event.getFullCommandName());
    }

    default Collection<Collection<? extends Route<T>>> routes() {
        return Collections.emptyList();
    }
}
