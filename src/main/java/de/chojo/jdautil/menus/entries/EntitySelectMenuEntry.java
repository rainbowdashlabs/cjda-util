/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.menus.entries;

import de.chojo.jdautil.localization.LocalizationContext;
import de.chojo.jdautil.menus.EntryContext;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectInteraction;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.function.Consumer;

public class EntitySelectMenuEntry extends MenuEntry<EntitySelectMenu, EntitySelectInteractionEvent> {
    public EntitySelectMenuEntry(EntitySelectMenu component, Consumer<EntryContext<EntitySelectInteractionEvent, EntitySelectMenu>> eventConsumer) {
        super(component, eventConsumer);
    }

    @Override
    public ActionComponent component(long id, LocalizationContext localizer) {
        return component().createCopy().setId(String.format("%s:%s", id, component().getId()))
                .setPlaceholder(localizer.localize(component().getPlaceholder()))
                .build();
}
}
