package de.chojo.jdautil.dialog.prompts;

import de.chojo.jdautil.dialog.ConversationContext;

public abstract class MessagePrompt extends Prompt {
    @Override
    public boolean waitForInput() {
        return false;
    }

    @Override
    public void parse(ConversationContext context) {
    }

    @Override
    public abstract Prompt nextPrompt(ConversationContext context);

    @Override
    public abstract String getPromptText(ConversationContext context);
}
