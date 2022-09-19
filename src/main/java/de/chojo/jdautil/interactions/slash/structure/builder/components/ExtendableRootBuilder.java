/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash.structure.builder.components;

import de.chojo.jdautil.interactions.slash.structure.builder.GroupBuilder;
import de.chojo.jdautil.interactions.slash.structure.builder.SubCommandBuilder;

public interface ExtendableRootBuilder extends BuildableMetaBuilder {
    ExtendableRootBuilder group(GroupBuilder builder);

    ExtendableRootBuilder subCommand(SubCommandBuilder builder);
}
