/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.menus;

import de.chojo.jdautil.menus.entries.MenuEntry;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;

public record EntryContext<Event extends GenericComponentInteractionCreateEvent, Component extends ActionComponent>(
        Event event, MenuEntry<Component, Event> entry, MenuContainer container) {

    public void refresh() {
        event.deferEdit().setActionRows(container.actionRows()).queue();
    }
}
