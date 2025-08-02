/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.message.provider;

import de.chojo.jdautil.interactions.message.Message;
import de.chojo.jdautil.interactions.message.builder.MessageBuilder;

public class MessageCommand implements MessageProvider<Message> {
    private final Message message;

    public MessageCommand(Message message) {
        this.message = message;
    }

    public MessageCommand(MessageBuilder message) {
        this(message.build());
    }

    @Override
    public Message message() {
        return message;
    }
}
