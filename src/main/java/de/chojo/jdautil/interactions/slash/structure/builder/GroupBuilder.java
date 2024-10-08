/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash.structure.builder;

import de.chojo.jdautil.interactions.slash.Group;
import de.chojo.jdautil.interactions.slash.SubCommand;
import de.chojo.jdautil.interactions.slash.structure.builder.components.PartialGroupBuilder;
import de.chojo.jdautil.interactions.slash.structure.meta.RouteMeta;

import java.util.ArrayList;
import java.util.List;

public class GroupBuilder implements PartialGroupBuilder {
    private final String name;
    private final String description;
    private final List<SubCommand> leaves = new ArrayList<>();

    private GroupBuilder(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static PartialGroupBuilder of(String name, String description) {
        return new GroupBuilder(name, description);
    }

    public Group build() {
        return new Group(new RouteMeta(name, description), leaves);
    }

    @Override
    public GroupBuilder subCommand(SubCommandBuilder builder) {
        leaves.add(builder.build());
        return this;
    }
}
