package de.chojo.jdautil.conversation.elements;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.util.Collection;
import java.util.function.Function;

public class Step {
    private final String prompt;
    private final Function<Context, Result> handle;
    private final ButtonDialog dialog;

    private Step(String prompt, Function<Context, Result> handle) {
        this.prompt = prompt;
        this.handle = handle;
        this.dialog = null;
    }

    private Step(String prompt, ButtonDialog buttonDialog) {
        this.handle = null;
        this.prompt = prompt;
        dialog = buttonDialog;
    }

    public static Step of(String prompt, Function<Context, Result> handle) {
        return new Step(prompt, handle);
    }

    public Result handleMessage(Context message) {
        return handle.apply(message);
    }

    public Result handleButton(ButtonClickEvent event) {
        return dialog.handle(event);
    }

    public boolean canConsume() {
        return handle != null;
    }

    public boolean hasPrompt() {
        return prompt != null && !prompt.isBlank();
    }

    public static Step message(String prompt, Function<Context, Result> handle) {
        return new Step(prompt, handle);
    }

    public static Step button(String prompt, ButtonDialog buttonDialog) {
        return new Step(prompt, buttonDialog);
    }

    public String prompt() {
        return prompt;
    }

    public boolean hasButtons() {
        return dialog != null;
    }

    public Collection<? extends ActionRow> getActions() {
        return dialog.getActions();
    }
}
