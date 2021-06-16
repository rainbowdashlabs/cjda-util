package de.chojo.jdautil.conversation;

import de.chojo.jdautil.conversation.elements.Context;
import de.chojo.jdautil.conversation.elements.Result;
import de.chojo.jdautil.conversation.elements.Step;
import de.chojo.jdautil.localization.ILocalizer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Conversation {
    private final Map<Integer, Step> steps;
    private final Map<String, Object> data = new HashMap<>();
    private final Consumer<Conversation> onClose;
    private Step step;
    private ILocalizer localizer;
    private ConversationService conversationService;
    private User owner;

    public Conversation(Step initalStep, Map<Integer, Step> steps, Consumer<Conversation> onClose) {
        this.steps = steps;
        this.onClose = onClose;
        step = initalStep;
    }

    public boolean handleMessage(Message message) {
        if (step.hasButtons()) return false;
        var context = new Context(this, data, message);
        var result = step.handleMessage(context);
        return switch (result.type()) {
            case FAILED -> {
                sendPrompt(message.getTextChannel());
                yield false;
            }
            case PROCEED -> {
                step = steps.get(result.next());
                sendPrompt(message.getTextChannel());
                yield false;
            }
            case FINISH -> true;
            case FREEZE -> false;
        };
    }

    public Result handleInteraction(ButtonClickEvent event) {
        var result = step.handleButton(event);
        handleResult(result, event.getTextChannel());
        return result;
    }

    private boolean handleResult(Result result, TextChannel channel) {
        return switch (result.type()) {
            case FAILED -> {
                sendPrompt(channel);
                yield false;
            }
            case PROCEED -> {
                step = steps.get(result.next());
                sendPrompt(channel);
                yield false;
            }
            case FINISH -> true;
            case FREEZE -> false;
        };

    }

    private void sendPrompt(TextChannel textChannel) {
        if (step.hasButtons()) {
            textChannel.sendMessage(localizer.localize(step.prompt(), textChannel.getGuild()))
                    .setActionRows(step.getActions())
                    .queue(message -> conversationService.registerButtons(message, this));
        } else {
            textChannel.sendMessage(localizer.localize(step.prompt(), textChannel.getGuild())).queue();
        }
    }

    public void start(TextChannel channel) {
        sendPrompt(channel);
    }

    public void proceed(TextChannel channel, int next) {
        step = steps.get(next);
        sendPrompt(channel);
    }

    public void close() {
        onClose.accept(this);
    }

    void inject(User owner, ILocalizer localizer, ConversationService service) {
        this.owner = owner;
        this.localizer = localizer;
        this.conversationService = service;
    }

    public User owner() {
        return owner;
    }
}
