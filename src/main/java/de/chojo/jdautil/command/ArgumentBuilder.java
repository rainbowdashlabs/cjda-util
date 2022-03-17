package de.chojo.jdautil.command;

import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ArgumentBuilder {
    List<SimpleArgument> arguments = new ArrayList<>();

    @Deprecated(forRemoval = true)
    public ArgumentBuilder add(OptionType type, String name, String description, boolean required) {
        arguments.add(SimpleArgument.of(type, name, description, required));
        return this;
    }

    @Deprecated(forRemoval = true)
    public ArgumentBuilder add(OptionType type, String name, String description) {
        arguments.add(SimpleArgument.builder(type, name, description).build());
        return this;
    }

    @Deprecated(forRemoval = true)
    public ArgumentBuilder add(OptionType type, String name, String description, Consumer<SimpleArgumentBuilder> modify) {
        var builder = SimpleArgument.builder(type, name, description);
        modify.accept(builder);
        arguments.add(builder.build());
        return this;
    }

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
