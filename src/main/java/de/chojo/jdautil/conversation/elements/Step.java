package de.chojo.jdautil.conversation.elements;

import java.util.function.Function;

public class Step {
    private final String prompt;
    private final Function<Context, Result> handle;

    private Step(String prompt, Function<Context, Result> handle) {
        this.prompt = prompt;
        this.handle = handle;
    }

    public static Step of(String prompt, Function<Context, Result> handle) {
        return new Step(prompt, handle);
    }

    public Result handle(Context message) {
        return handle.apply(message);
    }

    public boolean canConsume() {
        return handle != null;
    }

    public boolean hasPrompt() {
        return prompt != null && !prompt.isBlank();
    }

    public static Builder create(String prompt, Function<Context, Result> handle) {
        return new Builder(prompt, handle);
    }

    public String prompt() {
        return prompt;
    }

    public static class Builder {
        private String prompt;
        private Function<Context, Result> handle;

        public Builder(String prompt, Function<Context, Result> handle) {
            this.prompt = prompt;
            this.handle = handle;
        }

        public Step build() {
            return new Step(prompt, handle);
        }
    }
}
