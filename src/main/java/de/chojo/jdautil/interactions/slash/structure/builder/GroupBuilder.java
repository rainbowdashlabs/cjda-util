/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash.structure.builder;

import de.chojo.jdautil.interactions.premium.SKU;
import de.chojo.jdautil.interactions.slash.Group;
import de.chojo.jdautil.interactions.slash.SubCommand;
import de.chojo.jdautil.interactions.slash.structure.builder.components.PartialGroupBuilder;
import de.chojo.jdautil.interactions.slash.structure.meta.RouteMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GroupBuilder implements PartialGroupBuilder {
    private final String name;
    private final String description;
    private final List<SKU> skus = new ArrayList<>();
    private final List<SubCommand> leaves = new ArrayList<>();

    private GroupBuilder(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static PartialGroupBuilder of(String name, String description) {
        return new GroupBuilder(name, description);
    }

    public GroupBuilder skus(Collection<SKU> skus) {
        this.skus.addAll(skus);
        return this;
    }

    public Group build() {
        return new Group(new RouteMeta(name, description, skus), leaves);
    }

    @Override
    public GroupBuilder subCommand(SubCommandBuilder builder) {
        leaves.add(builder.build());
        return this;
    }
}
