/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.modals.handler;

import de.chojo.jdautil.localization.ContextLocalizer;
import de.chojo.jdautil.localization.ILocalizer;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.Modal;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class ModalHandler {
    private final String label;
    private final Map<String, TextInputHandler> inputs;
    private final Consumer<ModalInteractionEvent> handler;

    ModalHandler(String label, Map<String, TextInputHandler> inputs, Consumer<ModalInteractionEvent> handler) {
        this.label = label;
        this.inputs = inputs;
        this.handler = handler;
    }

    public static ModalHandlerBuilder builder(String label) {
        return new ModalHandlerBuilder(label);
    }

    public Modal createModal(String id, ContextLocalizer localizer) {
        var inputs = this.inputs.values().stream().map(intput -> intput.input(localizer)).toList();
        return Modal.create(id, localizer.localize(label))
                .addActionRow(inputs)
                .build();
    }

    public void handle(ModalInteractionEvent event) {
        for (var mapping : event.getValues()) {
            Optional.ofNullable(inputs.get(mapping.getId())).ifPresent(handler -> handler.handle(mapping));
        }
        handler.accept(event);
    }
}
