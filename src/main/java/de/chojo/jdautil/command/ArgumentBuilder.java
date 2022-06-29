/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command;

import java.util.ArrayList;
import java.util.List;

public class ArgumentBuilder {
    private final List<SimpleArgument> arguments = new ArrayList<>();

    public ArgumentBuilder add(SimpleArgument argument) {
        arguments.add(argument);
        return this;
    }

    public ArgumentBuilder add(SimpleArgumentBuilder argument) {
        arguments.add(argument.build());
        return this;
    }

    public SimpleArgument[] build() {
        return arguments.toArray(new SimpleArgument[0]);
    }
}
