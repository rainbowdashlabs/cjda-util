package de.chojo.jdautil.wrapper;

import de.chojo.jdautil.parsing.ValueParser;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;

public class CommandContext {
    private final String label;
    private final String[] args;
    private final OptionSet optionSet;

    public CommandContext(String label, String[] args) {
        this.label = label;
        this.args = args;
        optionSet = new OptionParser().parse(args);
    }

    public boolean argsEmpty() {
        return args.length == 0;
    }

    public Optional<String> argString(int index) {
        if (index <= args.length) {
            return Optional.ofNullable(args[index]);
        }
        return Optional.empty();
    }

    public Optional<Integer> argInt(int index) {
        return parseArg(index, ValueParser::parseInt);
    }

    public Optional<Long> argLong(int index) {
        return parseArg(index, ValueParser::parseLong);
    }

    private <T> Optional<T> parseArg(int index, Function<String, Optional<T>> map) {
        Optional<String> s = argString(index);
        return s.isEmpty() ? Optional.empty() : map.apply(s.get());
    }

    public String label() {
        return label;
    }
}
