package de.chojo.jdautil.dialog.prompts;

import de.chojo.jdautil.dialog.ConversationContext;

public class EndPrompt extends Prompt {
    public static final Prompt END_PROMPT = new EndPrompt();

    @Override
    public boolean waitForInput() {
        return true;
    }

    @Override
    public void parse(ConversationContext context) {
    }

    @Override
    public Prompt nextPrompt(ConversationContext context) {
        return null;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        return "Conversation Ended.";
    }
}
