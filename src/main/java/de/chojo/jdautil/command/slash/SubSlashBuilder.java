/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command.slash;

import java.util.ArrayList;
import java.util.List;

public class SubSlashBuilder {
    List<SubSlash> subCommands = new ArrayList<>();

    public SubSlashBuilder add(String name, String description, Argument... args) {
        subCommands.add(new SubSlash(name, args, description));
        return this;
    }

    public SubSlash[] build() {
        return subCommands.toArray(new SubSlash[0]);
    }
}
