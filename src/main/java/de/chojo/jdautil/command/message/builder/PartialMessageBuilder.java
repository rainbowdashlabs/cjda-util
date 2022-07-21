/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command.message.builder;

import de.chojo.jdautil.command.message.MessageHandler;

public interface PartialMessageBuilder {
    MessageBuilder handler(MessageHandler handler);
}
