/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.message.builder;

import de.chojo.jdautil.interactions.base.InteractionMeta;
import de.chojo.jdautil.interactions.base.InteractionMetaBuilder;
import de.chojo.jdautil.interactions.message.Message;
import de.chojo.jdautil.interactions.message.MessageHandler;

public class MessageBuilder extends InteractionMetaBuilder<MessageBuilder> implements PartialMessageBuilder {
    private MessageHandler handler;

    private MessageBuilder(String name) {
        super(name);
    }

    public static PartialMessageBuilder of(String name) {
        return new MessageBuilder(name);
    }

    @Override
    public MessageBuilder handler(MessageHandler handler) {
        this.handler = handler;
        return this;
    }

    public Message build() {
        return new Message(new InteractionMeta(name(), isGuildOnly(), permission(), scope(), localized()), handler);
    }
}
