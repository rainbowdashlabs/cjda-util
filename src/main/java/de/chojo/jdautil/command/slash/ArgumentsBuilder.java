/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command.slash;

import java.util.ArrayList;
import java.util.List;

public class ArgumentsBuilder {
    private final List<Argument> arguments = new ArrayList<>();

    public ArgumentsBuilder add(Argument argument) {
        arguments.add(argument);
        return this;
    }

    public ArgumentsBuilder add(ArgumentBuilder argument) {
        arguments.add(argument.build());
        return this;
    }

    public Argument[] build() {
        return arguments.toArray(new Argument[0]);
    }
}
