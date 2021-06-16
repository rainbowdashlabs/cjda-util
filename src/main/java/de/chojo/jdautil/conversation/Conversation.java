package de.chojo.jdautil.conversation;

import de.chojo.jdautil.conversation.elements.Context;
import de.chojo.jdautil.conversation.elements.Step;
import de.chojo.jdautil.localization.ILocalizer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Conversation {
    private final Map<Integer, Step> steps;
    private final Map<String, Object> data = new HashMap<>();
    private final Consumer<Conversation> onClose;
    private Step step;
    private ILocalizer localizer;

    public Conversation(Step initalStep, Map<Integer, Step> steps, Consumer<Conversation> onClose) {
        this.steps = steps;
        this.onClose = onClose;
        step = initalStep;
    }

    public boolean handle(Message message) {
        var context = new Context(data, message);
        var result = step.handle(context);
        switch (result.type()) {
            case FAILED -> {
                sendPrompt(message.getTextChannel());
                return false;
            }
            case PROCEED -> {
                step = steps.get(result.next());
                sendPrompt(message.getTextChannel());
                return false;
            }
            case FINISH -> {
                return true;
            }
        }
        return true;
    }

    void addLocalizer(ILocalizer localizer) {
        this.localizer = localizer;
    }

    private void sendPrompt(TextChannel textChannel) {
        if (step.hasPrompt()) {
            textChannel.sendMessage(localizer.localize(step.prompt(), textChannel.getGuild())).queue();
        }
    }

    public void start(TextChannel channel) {
        sendPrompt(channel);
    }

    public void close() {
        onClose.accept(this);
    }
}
