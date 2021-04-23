package de.chojo.jdautil.wrapper;

import de.chojo.jdautil.dialog.Conversation;
import de.chojo.jdautil.dialog.ConversationHandler;
import de.chojo.jdautil.parsing.ArgumentUtil;
import de.chojo.jdautil.parsing.ValueParser;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class CommandContext {
    private final String label;
    private String[] args;
    private final FlagContainer flags;
    private ConversationHandler conversationHandler;

    public CommandContext(String label, String[] args, ConversationHandler conversationHandler) {
        this.label = label;
        this.args = args;
        flags = FlagContainer.of(args);
        this.conversationHandler = conversationHandler;
    }

    public boolean argsEmpty() {
        return args.length == 0;
    }

    public void parseQuoted() {
        args = ArgumentUtil.parseQuotedArgs(args);
    }

    public void splitArgs() {
        args = String.join(" ", args).split("\\s");
    }

    public Optional<String> argString(int index) {
        if (index < args.length) {
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

    public Optional<Long> argDouble(int index) {
        return parseArg(index, ValueParser::parseLong);
    }

    public List<String> args(int from) {
        return ArgumentUtil.getRangeAsList(args, from);
    }

    public List<String> args() {
        return Arrays.asList(args);
    }

    public String[] argsArray() {
        return args.clone();
    }

    public List<String> args(int from, int to) {
        return ArgumentUtil.getRangeAsList(args, from, to);
    }

    private <T> Optional<T> parseArg(int index, Function<String, Optional<T>> map) {
        var s = argString(index);
        return s.isEmpty() ? Optional.empty() : map.apply(s.get());
    }

    public String label() {
        return label;
    }

    public void startDialog(MessageEventWrapper eventWrapper, Conversation conversation) {
        conversationHandler.startDialog(eventWrapper, conversation);
    }

    public CommandContext subCommandcontext(String label) {
        return new CommandContext(label, ArgumentUtil.getRangeAsList(args, 1).toArray(new String[0]),
                conversationHandler);
    }
}
