/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.message.builder;

import de.chojo.jdautil.interactions.base.InteractionMeta;
import de.chojo.jdautil.interactions.base.InteractionMetaBuilder;
import de.chojo.jdautil.interactions.message.Message;
import de.chojo.jdautil.interactions.message.MessageHandler;
import de.chojo.jdautil.interactions.premium.SKU;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MessageBuilder extends InteractionMetaBuilder<MessageBuilder> implements PartialMessageBuilder {
    private MessageHandler handler;
    private final List<SKU> skus = new ArrayList<>();

    private MessageBuilder(String name) {
        super(name);
    }

    public static PartialMessageBuilder of(String name) {
        return new MessageBuilder(name);
    }

    public MessageBuilder skus(Collection<SKU> skus) {
        this.skus.addAll(skus);
        return this;
    }

    @Override
    public MessageBuilder handler(MessageHandler handler) {
        this.handler = handler;
        return this;
    }

    public Message build() {
        return new Message(new InteractionMeta(name(), getContext(), permission(), scope(), localized(), skus), handler);
    }
}
