/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.menus.entries;

import de.chojo.jdautil.localization.LocalizationContext;
import de.chojo.jdautil.menus.EntryContext;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

import java.util.function.Consumer;

public class StringSelectMenuEntry extends MenuEntry<StringSelectMenu, StringSelectInteractionEvent> {
    public StringSelectMenuEntry(StringSelectMenu component, Consumer<EntryContext<StringSelectInteractionEvent, StringSelectMenu>> eventConsumer) {
        super(component, eventConsumer);
    }

    @Override
    public ActionRowChildComponent component(long id, LocalizationContext localizer) {
        var options = component().getOptions().stream().map(opt -> opt.withLabel(localizer.localize(opt.getLabel()))
                                                                      .withDescription(localizer.localize(opt.getDescription())))
                                 .toList();
        return StringSelectMenu.create(String.format("%s:%s", id, component().getCustomId()))
                               .addOptions(options)
                               .setPlaceholder(localizer.localize(component().getPlaceholder()))
                               .build();
    }
}
