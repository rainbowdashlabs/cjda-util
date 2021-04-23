package de.chojo.jdautil.dialog.prompts;

import de.chojo.jdautil.dialog.ConversationContext;

public abstract class StringInputPrompt extends Prompt {
    @Override
    public boolean waitForInput() {
        return true;
    }

    @Override
    public abstract void parse(ConversationContext context);

    @Override
    public abstract Prompt nextPrompt(ConversationContext context);

    @Override
    public abstract String getPromptText(ConversationContext context);
}
