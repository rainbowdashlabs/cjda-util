package de.chojo.jdautil.dialog.prompts;

import de.chojo.jdautil.dialog.ConversationContext;

import java.util.regex.Pattern;

public abstract class RegexInputPrompt extends StringInputPrompt {
    private final Pattern pattern;
    private final String valueKey;

    public RegexInputPrompt(Pattern pattern, String valueKey) {
        this.pattern = pattern;
        this.valueKey = valueKey;
    }

    @Override
    public void parse(ConversationContext context) {
        var contentRaw = context.getEventWrapper().getMessage().getContentRaw();
        if (pattern.matcher(contentRaw).matches()) {
            context.setResult(valueKey, contentRaw);
        }
    }

    @Override
    public abstract Prompt nextPrompt(ConversationContext context);

    @Override
    public abstract String getPromptText(ConversationContext context);
}
