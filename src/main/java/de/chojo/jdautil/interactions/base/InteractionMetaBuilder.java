/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.base;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class InteractionMetaBuilder<T extends InteractionMetaBuilder<T>> {
    private final String name;
    private Set<InteractionContextType> context = EnumSet.of(InteractionContextType.GUILD, InteractionContextType.BOT_DM);
    private DefaultMemberPermissions permission = DefaultMemberPermissions.ENABLED;
    private InteractionScope scope = InteractionScope.GLOBAL;
    private boolean localized = true;

    public InteractionMetaBuilder(String name) {
        this.name = name;
    }

    protected T self() {
        return (T) this;
    }

    public String name() {
        return name;
    }

    public Set<InteractionContextType> getContext() {
        return context;
    }

    /**
     * Sets the context this command can be used in. Default is {@link InteractionContextType#GUILD} and {@link InteractionContextType#BOT_DM}
     * @param context new context
     * @return builder instance
     */
    public T setContext(Set<InteractionContextType> context) {
        this.context = context;
        return self();
    }

    public boolean localized() {
        return localized;
    }

    public DefaultMemberPermissions permission() {
        return permission;
    }

    public InteractionScope scope() {
        return scope;
    }

    public T setPermission(DefaultMemberPermissions permission) {
        this.permission = permission;
        return self();
    }

    public T withPermission(Permission permission, Permission... permissions) {
        var collect = Arrays.stream(permissions).collect(Collectors.toCollection(HashSet::new));
        collect.add(permission);
        this.permission = DefaultMemberPermissions.enabledFor(collect);
        return self();
    }

    public T setScope(InteractionScope scope) {
        this.scope = scope;
        return self();
    }

    public T unlocalized() {
        localized = false;
        return self();
    }
}
