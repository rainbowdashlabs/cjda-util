/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command.slash;

public class SubSlash {
    private final String name;
    private final Argument[] args;
    private final String description;

    public SubSlash(String name, Argument[] args, String description) {
        this.name = name;
        this.args = args;
        this.description = description;
    }

    public String name() {
        return name;
    }

    public Argument[] args() {
        return args;
    }

    public String description() {
        return description;
    }
}
