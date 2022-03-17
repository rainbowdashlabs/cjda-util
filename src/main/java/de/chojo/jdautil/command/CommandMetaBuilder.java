package de.chojo.jdautil.command;

public class CommandMetaBuilder {
    private String name;
    private String description;
    private ArgumentBuilder argument = SimpleCommand.argsBuilder();
    private SubCommandBuilder subCommands = SimpleCommand.subCommandBuilder();
    private boolean defaultEnabled = true;

    public CommandMetaBuilder(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public CommandMetaBuilder addArguments(SimpleArgument argument) {
        this.argument.add(argument);
        return this;
    }
    public CommandMetaBuilder addArguments(SimpleArgumentBuilder argument) {
        this.argument.add(argument.build());
        return this;
    }

    public CommandMetaBuilder addSubCommands(String name, String description, SimpleArgument[] arguments) {
        this.subCommands.add(name, description, arguments);
        return this;
    }

    public CommandMetaBuilder withPermission() {
        this.defaultEnabled = false;
        return this;
    }

    public CommandMeta build() {
        return new CommandMeta(name, description, argument.build(), subCommands.build(), defaultEnabled);
    }
}
