package de.chojo.jdautil.command;

import org.jetbrains.annotations.Nullable;

public class SimpleSubCommand {
    private final String name;
    private final String args;
    private final String description;

    public SimpleSubCommand(String name, String args, String description) {
        this.name = name;
        this.args = args;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public String getArgs() {
        return args;
    }

    public String getDescription() {
        return description;
    }
}
