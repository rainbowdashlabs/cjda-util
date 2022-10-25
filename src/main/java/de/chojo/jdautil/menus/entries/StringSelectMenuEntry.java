/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.menus.entries;

import de.chojo.jdautil.localization.LocalizationContext;
import de.chojo.jdautil.menus.EntryContext;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.function.Consumer;

public class StringSelectMenuEntry extends MenuEntry<StringSelectMenu, StringSelectInteractionEvent> {
    public StringSelectMenuEntry(StringSelectMenu component, Consumer<EntryContext<StringSelectInteractionEvent, StringSelectMenu>> eventConsumer) {
        super(component, eventConsumer);
    }

    @Override
    public ActionComponent component(long id, LocalizationContext localizer) {
        var options = component().getOptions().stream().map(opt -> opt.withLabel(localizer.localize(opt.getLabel()))
                                                                      .withDescription(localizer.localize(opt.getDescription())))
                                 .toList();
        return StringSelectMenu.create(String.format("%s:%s", id, component().getId()))
                .addOptions(options)
                .setPlaceholder(localizer.localize(component().getPlaceholder()))
                .build();
}
}
