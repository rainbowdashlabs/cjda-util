/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.user.builder;

import de.chojo.jdautil.interactions.user.User;
import de.chojo.jdautil.interactions.user.UserHandler;
import de.chojo.jdautil.interactions.user.UserMeta;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

public class UserBuilder implements PartialUserBuilder {
    private final String name;
    private boolean guildOnly;
    private DefaultMemberPermissions permissions = DefaultMemberPermissions.ENABLED;
    private UserHandler handler;

    private UserBuilder(String name) {
        this.name = name;
    }

    public static PartialUserBuilder of(String name) {
        return new UserBuilder(name);
    }

    @Override
    public UserBuilder handler(UserHandler handler) {
        this.handler = handler;
        return this;
    }

    public UserBuilder withPermission(Permission permission, Permission... permissions) {
        var collect = Arrays.stream(permissions).collect(Collectors.toCollection(HashSet::new));
        collect.add(permission);
        this.permissions = DefaultMemberPermissions.enabledFor(collect);
        return this;
    }

    public UserBuilder guildOnly(boolean guildOnly) {
        this.guildOnly = guildOnly;
        return this;
    }

    public User build() {
        return new User(new UserMeta(name, guildOnly, permissions), handler);
    }
}
