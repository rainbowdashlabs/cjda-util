/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.message.provider;

import de.chojo.jdautil.interactions.message.Message;

public interface MessageProvider<T extends Message> {
    T message();
}
