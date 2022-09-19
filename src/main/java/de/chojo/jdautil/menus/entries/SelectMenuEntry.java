/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.menus.entries;

import de.chojo.jdautil.localization.LocalizationContext;
import de.chojo.jdautil.menus.EntryContext;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

import java.util.function.Consumer;

public class SelectMenuEntry extends MenuEntry<SelectMenu, SelectMenuInteractionEvent> {
    public SelectMenuEntry(SelectMenu component, Consumer<EntryContext<SelectMenuInteractionEvent, SelectMenu>> eventConsumer) {
        super(component, eventConsumer);
    }

    @Override
    public ActionComponent component(long id, LocalizationContext localizer) {
        var copy = component().createCopy();
        copy.setId(String.format("%s:%s", id, copy.getId()))
                .setPlaceholder(localizer.localize(copy.getPlaceholder()));
        var options = copy.getOptions().stream().map(opt -> opt.withLabel(localizer.localize(opt.getLabel()))
                        .withDescription(localizer.localize(opt.getDescription())))
                .toList();
        copy.getOptions().clear();
        copy.addOptions(options);
        return copy.build();
    }
}
