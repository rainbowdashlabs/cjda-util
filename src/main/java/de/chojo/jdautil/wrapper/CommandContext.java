package de.chojo.jdautil.wrapper;

import de.chojo.jdautil.conversation.Conversation;
import de.chojo.jdautil.conversation.ConversationService;
import de.chojo.jdautil.parsing.ArgumentUtil;
import de.chojo.jdautil.parsing.ValueParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class CommandContext {
    private final String label;
    private final FlagContainer flags;
    private final ConversationService conversationHandler;
    private String[] args;

    public CommandContext(String label, String[] args, ConversationService conversationHandler) {
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

    public Optional<Boolean> argBoolean(int index) {
        return parseArg(index, ValueParser::parseBoolean);
    }

    public Optional<Boolean> argBoolean(int index, String aTrue, String aFalse) {
        return parseArg(index, s -> ValueParser.parseBoolean(s, aTrue, aFalse));
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
        conversationHandler.startDialog(eventWrapper.getAuthor(), eventWrapper.getTextChannel(), conversation);
    }

    public CommandContext subContext(String label) {
        return new CommandContext(label, ArgumentUtil.getRangeAsList(args, 1).toArray(new String[0]),
                conversationHandler);
    }

    public boolean hasFlag(@NotNull String flag) {
        return flags.has(flag);
    }

    public boolean hasFlagValue(String flag) {
        return flags.hasValue(flag);
    }

    public <T> T getFlag(@NotNull String flag, Function<@Nullable String, T> map) {
        return flags.get(flag, map);
    }

    @Nullable
    public String getFlag(String flag) {
        return flags.get(flag);
    }

    public Optional<String> getFlagValueIfPresent(String flag) {
        return flags.getIfPresent(flag);
    }

    public <T> Optional<T> getFlagValueIfPresent(@NotNull String flag, Function<String, T> map) {
        return flags.getIfPresent(flag, map);
    }
}
