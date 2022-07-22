/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.message.provider;

import de.chojo.jdautil.interactions.message.Message;

public class MessageCommand implements MessageProvider<Message> {
    private final Message message;

    public MessageCommand(Message message) {
        this.message = message;
    }

    @Override
    public Message message() {
        return message;
    }
}
