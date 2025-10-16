/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.modals.handler;

import de.chojo.jdautil.localization.LocalizationContext;
import net.dv8tion.jda.api.components.ModalTopLevelComponent;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class TextInputHandler {
    private final TextInput.Builder input;
    private final Consumer<ModalMapping> mapping;
    private final String label;

    TextInputHandler(@NotNull TextInput.Builder input, @NotNull Consumer<ModalMapping> mapping, String label) {
        this.input = input;
        this.mapping = mapping;
        this.label = label;
    }

    public static TextInputHandlerBuilder builder(String id, String label, TextInputStyle style) {
        return new TextInputHandlerBuilder(id, label, style);
    }

    public void handle(ModalMapping mapping) {
        this.mapping.accept(mapping);
    }

    public ModalTopLevelComponent input(LocalizationContext localizer) {
        var textInput = input.setPlaceholder(localizer.localize(input.getPlaceholder()))
             .build();
        return Label.of(localizer.localize(label), textInput);
    }

    public String id() {
        return input.getCustomId();
    }
}
