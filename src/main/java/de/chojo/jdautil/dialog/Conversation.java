package de.chojo.jdautil.dialog;

import de.chojo.jdautil.dialog.prompts.EndPrompt;
import de.chojo.jdautil.dialog.prompts.Prompt;
import de.chojo.jdautil.wrapper.MessageEventWrapper;

import java.util.HashMap;
import java.util.Map;

public class Conversation {
    private final Map<String, Object> results = new HashMap<>();
    private Prompt currentPrompt;

    public Conversation(Prompt prompt) {
        currentPrompt = prompt;
    }

    public boolean invoke(MessageEventWrapper eventWrapper) {
        var context = new ConversationContext(eventWrapper, results);
        currentPrompt = currentPrompt.invoke(context);
        if (currentPrompt == null) {
            return false;
        }
        return currentPrompt instanceof EndPrompt;
    }

    public Prompt getCurrentPrompt() {
        return currentPrompt;
    }

    public String getPromptText(MessageEventWrapper wrapper) {
        return currentPrompt.getPromptText(new ConversationContext(wrapper, results));
    }
}
