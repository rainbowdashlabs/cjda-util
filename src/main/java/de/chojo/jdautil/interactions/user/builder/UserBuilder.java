/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.user.builder;

import de.chojo.jdautil.interactions.base.InteractionMeta;
import de.chojo.jdautil.interactions.base.InteractionMetaBuilder;
import de.chojo.jdautil.interactions.user.User;
import de.chojo.jdautil.interactions.user.UserHandler;
import net.dv8tion.jda.api.entities.Entitlement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserBuilder extends InteractionMetaBuilder<UserBuilder> implements PartialUserBuilder {
    private UserHandler handler;
    private final List<Entitlement> entitlements = new ArrayList<>();

    private UserBuilder(String name) {
        super(name);
    }

    public static PartialUserBuilder of(String name) {
        return new UserBuilder(name);
    }

    public UserBuilder entitlements(Collection<Entitlement> entitlements) {
        this.entitlements.addAll(entitlements);
        return this;
    }

    @Override
    public UserBuilder handler(UserHandler handler) {
        this.handler = handler;
        return this;
    }

    public User build() {
        return new User(new InteractionMeta(name(), getContext(), permission(), scope(), localized(), entitlements), handler);
    }
}
