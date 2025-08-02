/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.message.builder;

import de.chojo.jdautil.interactions.message.MessageHandler;

public interface PartialMessageBuilder {
    MessageBuilder handler(MessageHandler handler);
}
