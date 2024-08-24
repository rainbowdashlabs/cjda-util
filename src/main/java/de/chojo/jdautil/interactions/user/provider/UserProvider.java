/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.user.provider;

import de.chojo.jdautil.interactions.user.User;

public interface UserProvider<T extends User> {
    T user();
}
