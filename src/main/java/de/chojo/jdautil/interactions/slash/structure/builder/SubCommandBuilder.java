/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash.structure.builder;

import de.chojo.jdautil.interactions.slash.Argument;
import de.chojo.jdautil.interactions.slash.SubCommand;
import de.chojo.jdautil.interactions.slash.structure.builder.argument.ArgumentBuilder;
import de.chojo.jdautil.interactions.slash.structure.builder.components.PartialSubCommandBuilder;
import de.chojo.jdautil.interactions.slash.structure.handler.SlashHandler;
import de.chojo.jdautil.interactions.slash.structure.meta.RouteMeta;
import net.dv8tion.jda.api.entities.Entitlement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SubCommandBuilder implements PartialSubCommandBuilder {
    private final String name;
    private final String description;
    private final List<Argument> arguments = new ArrayList<>();
    private SlashHandler handler;
    private final List<Entitlement> entitlements = new ArrayList<>();

    private SubCommandBuilder(String name, String description, SlashHandler handler) {
        this.name = name;
        this.description = description;
        this.handler = handler;
    }

    public static SubCommandBuilder full(String name, String description, SlashHandler handler) {
        return new SubCommandBuilder(name, description, handler);
    }

    public static PartialSubCommandBuilder partial(String name, String description) {
        return new SubCommandBuilder(name, description, null);
    }

    @Override
    public SubCommandBuilder handler(SlashHandler handler) {
        this.handler = handler;
        return this;
    }

    public SubCommandBuilder entitlements(Collection<Entitlement> entitlements) {
        this.entitlements.addAll(entitlements);
        return this;
    }

    public SubCommand build() {
        return new SubCommand(new RouteMeta(name, description, entitlements), handler, arguments);
    }

    public SubCommandBuilder argument(ArgumentBuilder argument) {
        arguments.add(argument.build());
        return this;
    }
}
