/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.conversation;

import de.chojo.jdautil.conversation.elements.InteractionContext;
import de.chojo.jdautil.conversation.elements.MessageContext;
import de.chojo.jdautil.conversation.elements.Result;
import de.chojo.jdautil.conversation.elements.Step;
import de.chojo.jdautil.localization.ILocalizer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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

    public Result handleMessage(Message message) {
        if (!step.hasMessage()) return Result.freeze();
        var context = new MessageContext(this, data, message);
        var result = step.handleMessage(context);
        switch (result.type()) {
            case FAILED -> sendPrompt(message.getTextChannel());
            case PROCEED -> {
                step = steps.get(result.next());
                sendPrompt(message.getTextChannel());
            }
        }
        return result;
    }

    public Result handleInteraction(ComponentInteraction interaction) {
        var result = step.handleButton(new InteractionContext(data, this, interaction));
        handleResult(result, interaction.getTextChannel());
        return result;
    }

    private boolean handleResult(Result result, TextChannel channel) {
        return switch (result.type()) {
            case FAILED -> {
                sendPrompt(channel);
                yield false;
            }
            case PROCEED -> {
                proceed(channel, result.next());
                yield false;
            }
            case FINISH -> true;
            case FREEZE -> false;
        };

    }

    private void sendPrompt(TextChannel textChannel) {
        sendPrompt(textChannel, 0);
    }

    private void sendPrompt(TextChannel textChannel, int delay) {
        if (step.hasButtons()) {
            textChannel.sendMessage(localizer.localize(step.prompt(), textChannel.getGuild()))
                    .setActionRows(step.getActions(localizer, textChannel.getGuild()))
                    .queueAfter(2, TimeUnit.SECONDS, message -> conversationService.registerButtons(message, this));
        } else {
            textChannel.sendMessage(localizer.localize(step.prompt(), textChannel.getGuild())).queueAfter(delay, TimeUnit.SECONDS);
        }
    }

    public void start(TextChannel channel) {
        sendPrompt(channel, 2);
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
