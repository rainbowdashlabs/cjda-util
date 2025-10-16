/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.menus.entries;

import de.chojo.jdautil.localization.LocalizationContext;
import de.chojo.jdautil.menus.EntryContext;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.function.Consumer;

public class ButtonEntry extends MenuEntry<Button, ButtonInteractionEvent> {
    public ButtonEntry(Button component, Consumer<EntryContext<ButtonInteractionEvent, Button>> eventConsumer) {
        super(component, eventConsumer);
    }

    @Override
    public ActionRowChildComponent component(long id, LocalizationContext localizer) {
        if (component().getCustomId() == null) {
            if (component().getLabel().isBlank()) {
                return component();
            }
            return component().withLabel(localizer.localize(component().getLabel()));
        }
        if (component().getLabel().isBlank()) {
            return component().withCustomId(String.format("%s:%s", id, component().getCustomId()));
        }
        return component().withCustomId(String.format("%s:%s", id, component().getCustomId()))
                          .withLabel(localizer.localize(component().getLabel()));
    }
}
