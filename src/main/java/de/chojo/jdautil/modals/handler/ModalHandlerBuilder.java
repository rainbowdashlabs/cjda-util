/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.modals.handler;

import de.chojo.jdautil.util.Consumers;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ModalHandlerBuilder {
    private final String label;
    private final Map<String, TextInputHandler> inputs = new LinkedHashMap<>();
    private Consumer<ModalInteractionEvent> handler = Consumers.empty();

    ModalHandlerBuilder(String label) {
        this.label = label;
    }

    /**
     * Adds a input handler.
     *
     * @param handler handler
     * @return builder instance
     */
    public ModalHandlerBuilder addInput(TextInputHandler handler) {
        inputs.put(handler.id(), handler);
        return this;
    }

    /**
     * Adds a input handler.
     *
     * @param handler handler
     * @return builder instance
     */
    public ModalHandlerBuilder addInput(TextInputHandlerBuilder handler) {
        return addInput(handler.build());
    }

    /**
     * Add a handler which will handle the event.
     * Called after the individual handlers of every {@link TextInputHandler} are called
     *
     * @param handler handler to handle even
     * @return builder instance
     */
    public ModalHandlerBuilder withHandler(Consumer<ModalInteractionEvent> handler) {
        this.handler = handler;
        return this;
    }

    public ModalHandler build() {
        return new ModalHandler(label, inputs, handler);
    }
}
