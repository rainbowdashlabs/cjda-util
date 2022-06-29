/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command;

import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ArgumentBuilder {
    List<SimpleArgument> arguments = new ArrayList<>();

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
