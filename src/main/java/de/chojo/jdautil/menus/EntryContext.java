/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.menus;

import de.chojo.jdautil.menus.entries.MenuEntry;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public record EntryContext<Event extends ComponentInteraction, Component extends ActionComponent>(
        Event event, MenuEntry<Component, Event> entry, MenuContainer container) {

    public void refresh() {
        event.deferEdit().setComponents(container.actionRows()).queue();
    }

    public void refresh(Message message) {
        event.editMessage(MessageEditData.fromMessage(message)).setComponents(container.actionRows()).queue();
    }

    public void refresh(MessageEmbed... message) {
        event.editMessageEmbeds(message).setComponents(container.actionRows()).queue();
    }

    public void refresh(String message) {
        event.editMessage(message).setComponents(container.actionRows()).queue();
    }
}
