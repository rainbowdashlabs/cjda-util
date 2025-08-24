/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.conversation.elements;

import de.chojo.jdautil.localization.ILocalizer;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.components.actionrow.ActionRow;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

public class Step {
    private final String prompt;
    private final Function<ConversationContext, Result> handle;
    private final ButtonDialog dialog;

    private Step(String prompt, Function<ConversationContext, Result> handle, ButtonDialog dialog) {
        this.prompt = prompt;
        this.handle = handle;
        this.dialog = dialog;
    }

    /**
     * Create a new step builder with a message prompt
     *
     * @param prompt prompt to send
     * @param handle handles the input and returns a {@link Result}
     * @return new builder instance
     */
    public static Step.Builder message(String prompt, Function<ConversationContext, Result> handle) {
        return builder(prompt).message(handle);
    }

    /**
     * Create a new step builder with a message prompt
     *
     * @param prompt  prompt to send
     * @param buttons a {@link ButtonDialog}
     * @return new builder instance
     */
    public static Step.Builder button(String prompt, Consumer<ButtonDialog> buttons) {
        return builder(prompt).button(buttons);
    }

    private static Builder builder(String prompt) {
        return new Builder(prompt);
    }

    /**
     * Handles a message
     *
     * @param message message context
     * @return result
     */
    public Result handleMessage(ConversationContext message) {
        return handle.apply(message);
    }

    /**
     * Handles a button interaction
     *
     * @param context interaction context
     * @return result
     */
    public Result handleButton(InteractionContext context) {
        return dialog.handle(context);
    }

    /**
     * Checks for a message handler
     *
     * @return true if there is one
     */
    public boolean hasMessage() {
        return handle != null;
    }

    /**
     * Get the prompt
     *
     * @return prompt
     */
    public String prompt() {
        return prompt;
    }

    /**
     * Checks for a button handler
     *
     * @return true if there is one
     */
    public boolean hasButtons() {
        return dialog != null;
    }

    /**
     * Get the action rows for this step
     *
     * @param localizer localizer for button and message content
     * @param guild     guild
     * @return list of action rows
     */
    public Collection<ActionRow> getActions(ILocalizer localizer, Guild guild) {
        return dialog.getActions(localizer, guild);
    }

    public static class Builder {
        private final String prompt;
        private Function<ConversationContext, Result> handle;
        private ButtonDialog dialog;

        public Builder(String prompt) {
            this.prompt = prompt;
        }

        public Builder button(Consumer<ButtonDialog> buttons) {
            this.dialog = new ButtonDialog();
            buttons.accept(dialog);
            return this;
        }

        public Builder message(Function<ConversationContext, Result> handle) {
            this.handle = handle;
            return this;
        }

        public Step build() {
            return new Step(prompt, handle, dialog);
        }
    }
}
