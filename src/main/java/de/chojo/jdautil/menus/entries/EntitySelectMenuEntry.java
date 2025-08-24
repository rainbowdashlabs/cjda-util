/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.menus.entries;

import de.chojo.jdautil.localization.LocalizationContext;
import de.chojo.jdautil.menus.EntryContext;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;

import java.util.function.Consumer;

public class EntitySelectMenuEntry extends MenuEntry<EntitySelectMenu, EntitySelectInteractionEvent> {
    public EntitySelectMenuEntry(EntitySelectMenu component, Consumer<EntryContext<EntitySelectInteractionEvent, EntitySelectMenu>> eventConsumer) {
        super(component, eventConsumer);
    }

    @Override
    public ActionRowChildComponent component(long id, LocalizationContext localizer) {
        return component().createCopy().setId(String.format("%s:%s", id, component().getId()))
                          .setPlaceholder(localizer.localize(component().getPlaceholder()))
                          .build();
    }
}
