/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.menus;

import de.chojo.jdautil.localization.LocalizationContext;
import de.chojo.jdautil.menus.entries.ButtonEntry;
import de.chojo.jdautil.menus.entries.MenuEntry;
import de.chojo.jdautil.menus.entries.SelectMenuEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.util.List;
import java.util.Optional;

public class MenuContainer {
    private final long id;
    private final LocalizationContext localizer;
    private final List<de.chojo.jdautil.menus.entries.MenuEntry<?, ?>> entries;
    private final long ownerId;

    MenuContainer(long id, LocalizationContext localizer, List<MenuEntry<?, ?>> entries, User user) {
        this.id = id;
        this.localizer = localizer;
        this.entries = entries;
        this.ownerId = user == null ? 0 : user.getIdLong();
    }

    public void invoke(GenericComponentInteractionCreateEvent event, String id) {
        var menuEntry = entries.stream().filter(b -> id.equals(b.id())).findFirst();
        if (menuEntry.isEmpty()) return;
        var entry = menuEntry.get();

        if (event instanceof ButtonInteractionEvent buttonEvent && entry instanceof ButtonEntry buttonEntry) {
            buttonEntry.invoke(new EntryContext<>(buttonEvent, buttonEntry, this));
        }

        if (event instanceof SelectMenuInteractionEvent selectMenuEvent && entry instanceof SelectMenuEntry selectMenuEntry) {
            selectMenuEntry.invoke(new EntryContext<>(selectMenuEvent, selectMenuEntry, this));
        }
    }

    public List<ActionComponent> components() {
        return entries.stream()
                .filter(MenuEntry::visible)
                .map(e -> e.component(id, localizer))
                .toList();
    }

    public Optional<MenuEntry<?, ?>> entry(String id) {
        return entries.stream().filter(e -> e.id().equals(id)).findFirst();
    }

    public List<ActionRow> actionRows() {
        return ActionRow.partitionOf(components());
    }

    public List<MenuEntry<?, ?>> entries() {
        return entries;
    }

    public boolean canInteract(User user) {
        return ownerId == 0 || user.getIdLong() == ownerId;
    }
}
