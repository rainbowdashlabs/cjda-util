/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.modals.handler;

import de.chojo.jdautil.localization.LocalizationContext;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class TextInputHandler {
    private final TextInput.Builder input;
    private final Consumer<ModalMapping> mapping;

    TextInputHandler(@NotNull TextInput.Builder input, @NotNull Consumer<ModalMapping> mapping) {
        this.input = input;
        this.mapping = mapping;
    }

    public static TextInputHandlerBuilder builder(String id, String label, TextInputStyle style) {
        return new TextInputHandlerBuilder(id, label, style);
    }

    public void handle(ModalMapping mapping) {
        this.mapping.accept(mapping);
    }

    public TextInput input(LocalizationContext localizer) {
        return input.setLabel(localizer.localize(input.getLabel()))
                .setPlaceholder(localizer.localize(input.getPlaceholder()))
                .build();
    }

    public String id(){
        return input.getId();
    }
}
