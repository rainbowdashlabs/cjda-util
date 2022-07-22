/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash.provider;

import de.chojo.jdautil.interactions.slash.Slash;
import de.chojo.jdautil.interactions.slash.structure.builder.SlashBuilder;

public class SlashCommand implements SlashProvider<Slash> {
    private final Slash slash;

    public SlashCommand(Slash slash) {
        this.slash = slash;
    }

    public SlashCommand(SlashBuilder slash) {
        this(slash.build());
    }

    @Override
    public Slash slash() {
        return slash;
    }
}
