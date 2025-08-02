/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash.structure.builder;

import de.chojo.jdautil.interactions.base.InteractionScope;
import de.chojo.jdautil.interactions.premium.SKU;
import de.chojo.jdautil.interactions.slash.Argument;
import de.chojo.jdautil.interactions.slash.Group;
import de.chojo.jdautil.interactions.slash.Slash;
import de.chojo.jdautil.interactions.slash.SubCommand;
import de.chojo.jdautil.interactions.slash.structure.builder.argument.ArgumentBuilder;
import de.chojo.jdautil.interactions.slash.structure.builder.components.BuildableMetaBuilder;
import de.chojo.jdautil.interactions.slash.structure.builder.components.ExtendableRootBuilder;
import de.chojo.jdautil.interactions.slash.structure.builder.components.RootArgumentBuilder;
import de.chojo.jdautil.interactions.slash.structure.builder.components.RootMetaBuilder;
import de.chojo.jdautil.interactions.slash.structure.handler.SlashHandler;
import de.chojo.jdautil.interactions.slash.structure.meta.CommandMeta;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class which allows to create grouped commands.
 * The limitations are implemented as defined
 * <a href="https://discord.com/developers/docs/interactions/application-commands#subcommands-and-subcommand-groups">here</a>
 */
public class SlashBuilder implements RootArgumentBuilder, ExtendableRootBuilder, RootMetaBuilder, BuildableMetaBuilder {
    private final String name;
    private final String description;
    private final List<Group> groups = new ArrayList<>();
    private final List<SubCommand> leaves = new ArrayList<>();
    private final List<Argument> arguments = new ArrayList<>();
    private DefaultMemberPermissions permission = DefaultMemberPermissions.ENABLED;
    private Set<InteractionContextType> context = Set.of(InteractionContextType.GUILD, InteractionContextType.BOT_DM);
    private SlashHandler handler;
    private InteractionScope scope = InteractionScope.GLOBAL;
    private boolean localized = true;
    private final List<SKU> skus = new ArrayList<>();

    public SlashBuilder(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public ExtendableRootBuilder group(GroupBuilder builder) {
        groups.add(builder.build());
        return this;
    }

    @Override
    public ExtendableRootBuilder subCommand(SubCommandBuilder builder) {
        leaves.add(builder.build());
        return this;
    }

    @Override
    public RootArgumentBuilder command(SlashHandler handler) {
        this.handler = handler;
        return this;
    }

    @Override
    public RootArgumentBuilder argument(ArgumentBuilder argument) {
        arguments.add(argument.build());
        return this;
    }

    public RootMetaBuilder skus(Collection<SKU> skus) {
        this.skus.addAll(skus);
        return this;
    }

    @Override
    public RootMetaBuilder withPermission(Permission permission, Permission... permissions) {
        var collect = Arrays.stream(permissions).collect(Collectors.toCollection(HashSet::new));
        collect.add(permission);
        this.permission = DefaultMemberPermissions.enabledFor(collect);
        return this;
    }

    /**
     * Marks a command as public command.
     * <p>
     * This is the default value.
     *
     * @return builder
     */
    @Override
    public RootMetaBuilder publicCommand() {
        this.permission = DefaultMemberPermissions.ENABLED;
        return this;
    }

    /**
     * Marks a command as admin command.
     * <p>
     * The command will only be accessible for administrators of a guild.
     *
     * @return builder
     */
    @Override
    public RootMetaBuilder adminCommand() {
        this.permission = DefaultMemberPermissions.DISABLED;
        return this;
    }

    @Override
    public RootMetaBuilder guildOnly() {
        this.context = Set.of(InteractionContextType.GUILD);
        return this;
    }

    @Override
    public RootMetaBuilder scope(InteractionScope scope) {
        this.scope = scope;
        return this;
    }

    @Override
    public RootMetaBuilder unlocalized() {
        localized = false;
        return this;
    }

    public boolean localized() {
        return localized;
    }


    @Override
    public Slash build() {
        var meta = new CommandMeta(name, description, context, permission, scope, localized, skus);
        return new Slash(meta, handler, groups, leaves, arguments);
    }
}
