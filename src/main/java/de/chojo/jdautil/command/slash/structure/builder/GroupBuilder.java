/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command.slash.structure.builder;

import de.chojo.jdautil.command.slash.structure.Group;
import de.chojo.jdautil.command.slash.structure.SubCommand;
import de.chojo.jdautil.command.slash.structure.builder.components.PartialGroupBuilder;
import de.chojo.jdautil.command.slash.structure.meta.RouteMeta;

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
