/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.chojo.jdautil.conversation.elements;

import de.chojo.jdautil.localization.ILocalizer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.components.ActionRow;

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

    public Result handleMessage(ConversationContext message) {
        return handle.apply(message);
    }

    public Result handleButton(InteractionContext context) {
        return dialog.handle( context);
    }

    public boolean hasMessage() {
        return handle != null;
    }

    public static Step.Builder message(String prompt, Function<ConversationContext, Result> handle) {
        return builder(prompt).message(handle);
    }

    public static Step.Builder button(String prompt, Consumer<ButtonDialog> buttons) {
        return builder(prompt).button(buttons);
    }

    public String prompt() {
        return prompt;
    }

    public boolean hasButtons() {
        return dialog != null;
    }

    public Collection<? extends ActionRow> getActions(ILocalizer localizer, Guild guild) {
        return dialog.getActions(localizer, guild);
    }

    private static Builder builder(String prompt) {
        return new Builder(prompt);
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
