/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.user.provider;

import de.chojo.jdautil.interactions.user.User;

public class UserCommand implements UserProvider<User> {
    private final User user;

    public UserCommand(User user) {
        this.user = user;
    }

    @Override
    public User user() {
        return user;
    }
}
