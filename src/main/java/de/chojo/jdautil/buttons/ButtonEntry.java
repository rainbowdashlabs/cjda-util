/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.buttons;

import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;

import java.util.function.Consumer;

public class ButtonEntry {
    private final Button button;
    private final Consumer<ButtonInteraction> interactionConsumer;

    private ButtonEntry(Button button, Consumer<ButtonInteraction> interactionConsumer) {
        this.button = button;
        this.interactionConsumer = interactionConsumer;
    }

    public static ButtonEntry of(Button button, Consumer<ButtonInteraction> event) {
        return new ButtonEntry(button, event);
    }

    public Button button() {
        return button;
    }

    public Consumer<ButtonInteraction> interactionConsumer() {
        return interactionConsumer;
    }
}
