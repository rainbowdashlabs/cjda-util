package de.chojo.jdautil.conversation.builder;

import de.chojo.jdautil.conversation.Conversation;
import de.chojo.jdautil.conversation.elements.Step;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ConversationBuilder {
    private final Step initalStep;
    private final Map<Integer, Step> steps = new HashMap<>();
    private Consumer<Conversation> onClose = c -> {
    };

    private ConversationBuilder(Step initalStep) {
        this.initalStep = initalStep;
    }

    public static ConversationBuilder builder(Step step) {
        return new ConversationBuilder(step);
    }

    public ConversationBuilder addStep(Integer step, Step conversation) {
        if (steps.containsKey(step)) throw new IllegalArgumentException("Step " + step + " is already defined.");
        steps.put(step, conversation);
        return this;
    }

    public ConversationBuilder onClose(Consumer<Conversation> onClose) {
        this.onClose = onClose;
        return this;
    }

    public void validate() throws IllegalStateException {

    }

    public Conversation build() {
        validate();
        return new Conversation(initalStep, steps, onClose);
    }
}
