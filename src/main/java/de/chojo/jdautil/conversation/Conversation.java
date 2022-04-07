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
import de.chojo.jdautil.util.Channel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
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
            case FAILED -> sendPrompt(message.getChannel());
            case PROCEED -> {
                step = steps.get(result.next());
                sendPrompt(message.getChannel());
            }
        }
        return result;
    }

    public Result handleInteraction(ComponentInteraction interaction) {
        var result = step.handleButton(new InteractionContext(data, this, interaction, localizer));
        handleResult(result, interaction.getChannel());
        return result;
    }

    private boolean handleResult(Result result, MessageChannel channel) {
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

    private void sendPrompt(MessageChannel channel) {
        sendPrompt(channel, 0);
    }

    private void sendPrompt(MessageChannel messageChannel, int delay) {
        var guild = Channel.guildFromMessageChannel(messageChannel);
        if (step.hasButtons()) {
            messageChannel.sendMessage(localizer.localize(step.prompt(), guild))
                    .setActionRows(step.getActions(localizer, guild))
                    .queueAfter(2, TimeUnit.SECONDS, message -> conversationService.registerButtons(message, this));
        } else {
            messageChannel.sendMessage(localizer.localize(step.prompt(), guild)).queueAfter(delay, TimeUnit.SECONDS);
        }
    }

    public void start(MessageChannel channel) {
        sendPrompt(channel, 2);
    }

    public void proceed(MessageChannel channel, int next) {
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
