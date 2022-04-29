/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.modals.handler;

import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class TextInputHandlerBuilder {
    TextInput.Builder builder;
    Consumer<ModalMapping> handler;


    public TextInputHandlerBuilder(String id, String label, TextInputStyle style) {
        builder = TextInput.create(id, label, style);
    }

    /**
     * Sets the input as optional
     *
     * @return builder instance
     */
    @Nonnull
    public TextInputHandlerBuilder asOptional() {
        builder.setRequired(false);
        return this;
    }

    /**
     * {@link TextInput.Builder#setMinLength(int)}
     *
     * @return builder instance
     */
    @Nonnull
    public TextInputHandlerBuilder withMinLength(int minLength) {
        builder.setMinLength(minLength);
        return this;
    }

    /**
     * {@link TextInput.Builder#setMaxLength(int)}
     *
     * @return builder instance
     */
    @Nonnull
    public TextInputHandlerBuilder withMaxLength(int maxLength) {
        builder.setMaxLength(maxLength);
        return this;
    }

    /**
     * {@link TextInput.Builder#setRequiredRange(int, int)}
     *
     * @return builder instance
     */
    @Nonnull
    public TextInputHandlerBuilder withRequiredRange(int min, int max) {
        builder.setRequiredRange(min, max);
        return this;
    }

    /**
     * {@link TextInput.Builder#setValue(String)}
     *
     * @return builder instance
     */
    @Nonnull
    public TextInputHandlerBuilder withValue(@Nullable String value) {
        builder.setValue(value);
        return this;
    }

    /**
     * {@link TextInput.Builder#setPlaceholder(String)}
     *
     * @return builder instance
     */
    @Nonnull
    public TextInputHandlerBuilder withPlaceholder(@Nullable String placeholder) {
        builder.setPlaceholder(placeholder);
        return this;
    }

    /**
     * Set a handler which will handle the input of the text iput
     *
     * @param handler a handler to accept the modal mapping
     * @return builder instance
     */
    public TextInputHandlerBuilder withHandler(Consumer<ModalMapping> handler) {
        this.handler = handler;
        return this;
    }

    public TextInputHandler build() {
        return new TextInputHandler(builder, handler);
    }
}
