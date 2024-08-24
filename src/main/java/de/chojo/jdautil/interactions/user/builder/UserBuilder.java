/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.user.builder;

import de.chojo.jdautil.interactions.base.InteractionMeta;
import de.chojo.jdautil.interactions.base.InteractionMetaBuilder;
import de.chojo.jdautil.interactions.user.User;
import de.chojo.jdautil.interactions.user.UserHandler;

public class UserBuilder extends InteractionMetaBuilder<UserBuilder> implements PartialUserBuilder {
    private UserHandler handler;

    private UserBuilder(String name) {
        super(name);
    }

    public static PartialUserBuilder of(String name) {
        return new UserBuilder(name);
    }

    @Override
    public UserBuilder handler(UserHandler handler) {
        this.handler = handler;
        return this;
    }

    public User build() {
        return new User(new InteractionMeta(name(), isGuildOnly(), permission(), scope(), localized()), handler);
    }
}
