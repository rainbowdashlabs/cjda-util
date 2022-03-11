/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command;

public class SimpleSubCommand {
    private final String name;
    private final SimpleArgument[] args;
    private final String description;

    public SimpleSubCommand(String name, SimpleArgument[] args, String description) {
        this.name = name;
        this.args = args;
        this.description = description;
    }

    public String name() {
        return name;
    }

    public SimpleArgument[] args() {
        return args;
    }

    public String description() {
        return description;
    }
}
