package de.chojo.jdautil.dialog.prompts;

import de.chojo.jdautil.dialog.ConversationContext;

public abstract class Prompt {

    public abstract boolean waitForInput();

    public abstract void parse(ConversationContext context);

    public final Prompt next(ConversationContext context) {
        var prompt = nextPrompt(context);
        var promptText = prompt.getPromptText(context);
        if (!promptText.isBlank()) context.getEventWrapper().getChannel().sendMessage(promptText).queue();
        if (!prompt.waitForInput()) return prompt.next(context);
        return prompt;
    }

    public abstract Prompt nextPrompt(ConversationContext context);

    public abstract String getPromptText(ConversationContext context);

    public final Prompt invoke(ConversationContext context) {
        if (waitForInput()) {
            parse(context);
            if (!context.isSuccess()) {
                var failText = getFailText(context);
                if (!failText.isBlank()) {
                    context.getEventWrapper().getChannel().sendMessage(failText).queue();
                }
                return null;
            }
            var successText = getSuccessText(context);
            if (!successText.isBlank()) {
                context.getEventWrapper().getChannel().sendMessage(successText).queue();
            }
            return next(context);
        } else {
            return next(context);
        }
    }

    public String getFailText(ConversationContext context) {
        return "";
    }

    public String getSuccessText(ConversationContext context) {
        return "";
    }
}
