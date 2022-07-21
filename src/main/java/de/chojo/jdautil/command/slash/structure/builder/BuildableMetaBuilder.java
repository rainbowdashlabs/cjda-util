/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command.slash.structure.builder;

import de.chojo.jdautil.command.slash.structure.Command;

public interface BuildableMetaBuilder {
    Command build();
}
