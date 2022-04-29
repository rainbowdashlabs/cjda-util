/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.modals.handler;

import de.chojo.jdautil.localization.ContextLocalizer;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import java.util.function.Consumer;

public class TextInputHandler {
    TextInput.Builder input;
    Consumer<ModalMapping> mapping;

    TextInputHandler(TextInput.Builder input, Consumer<ModalMapping> mapping) {
        this.input = input;
        this.mapping = mapping;
    }

    public static TextInputHandlerBuilder builder(String id, String label, TextInputStyle style) {
        return new TextInputHandlerBuilder(id, label, style);
    }

    public void handle(ModalMapping mapping) {
        this.mapping.accept(mapping);
    }

    public TextInput input(ContextLocalizer localizer) {
        return input.setLabel(localizer.localize(input.getLabel()))
                .setPlaceholder(localizer.localize(input.getPlaceholder()))
                .build();
    }

    public String id(){
        return input.getId();
    }
}
