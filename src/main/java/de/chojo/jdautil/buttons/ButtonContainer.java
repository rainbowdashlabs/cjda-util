/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.buttons;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.List;

class ButtonContainer {
    private final List<ButtonEntry> entry;
    private final long ownerId;

    public ButtonContainer(List<ButtonEntry> entry, User user) {
        this.entry = entry;
        this.ownerId = user == null ? 0 : user.getIdLong();
    }

    public void invoke(ButtonInteractionEvent event, String id) {
        entry.stream().filter(b -> id.equals(b.button().getId())).findFirst().ifPresent(b -> b.interactionConsumer().accept(event));
    }

    public boolean canInteract(User user) {
        return ownerId == 0 || user.getIdLong() == ownerId;
    }
}
