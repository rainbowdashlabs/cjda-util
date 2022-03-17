package de.chojo.jdautil.command;

import java.util.ArrayList;
import java.util.List;

public class SubCommandBuilder {
    List<SimpleSubCommand> subCommands = new ArrayList<>();

    public SubCommandBuilder add(String name, String description, SimpleArgument... args) {
        subCommands.add(new SimpleSubCommand(name, args, description));
        return this;
    }

    public SimpleSubCommand[] build() {
        return subCommands.toArray(new SimpleSubCommand[0]);
    }
}
