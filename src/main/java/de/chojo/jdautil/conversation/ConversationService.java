/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.conversation;

import de.chojo.jdautil.container.Pair;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.util.Channel;
import de.chojo.jdautil.wrapper.UserChannelKey;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ConversationService extends ListenerAdapter {
    private final ILocalizer localizer;
    // Guild -> Channel -> User -> Dialogue
    private final Map<UserChannelKey, Conversation> conversations = new HashMap<>();
    private final Map<UserChannelKey, Pair<Long, Conversation>> button = new HashMap<>();

    public ConversationService(ILocalizer localizer) {
        this.localizer = localizer;
    }

    /**
     * Invoke a dialog in this context.
     *
     * @param event wrapper of message Event
     * @return true if a dialog was invoked.
     */
    public boolean invoke(MessageReceivedEvent event) {
        var guild = event.isFromGuild() ? event.getGuild() : null;
        var content = event.getMessage().getContentRaw();
        var cancel = localizer.localize("conversation.cancel", guild);
        var exit = localizer.localize("conversation.exit", guild);

        var location = UserChannelKey.of(event.getAuthor(), event.getChannel());

        if (exit.equalsIgnoreCase(content) || cancel.equalsIgnoreCase(content)) {
            if (dialogInProgress(event.getAuthor(), event.getChannel())) {
                endConversation(location, event.getChannel(), false);
            }
            return true;
        }

        var dialog = getDialog(location);
        if (dialog == null) return false;

        var result = dialog.handleMessage(event.getMessage());
        switch (result.type()) {
            case PROCEED -> resolveButton(location);
            case FINISH -> endConversation(location, event.getChannel(), true);
        }
        return true;
    }

    public void startDialog(User user, MessageChannel channel, Conversation conversation) {
        var guild = Channel.guildFromMessageChannel(channel);
        if (dialogInProgress(user, channel)) {
            var message = localizer.localize("conversation.inProgress", guild);
            channel.sendMessage(message).queue();
            return;
        }

        channel.sendMessage(localizer.localize("conversation.start", guild)).queueAfter(2, TimeUnit.SECONDS, message -> {
            conversation.inject(user, localizer, this);
            conversation.start(channel);
            conversations.putIfAbsent(UserChannelKey.of(user, channel), conversation);
        });
    }

    public void startDialog(IReplyCallback event, Conversation conversation) {
        var guild = event.isFromGuild() ? event.getGuild() : null;
        if (dialogInProgress(event.getUser(), event.getMessageChannel())) {
            var message = localizer.localize("conversation.inProgress", guild);
            event.reply(message).queue();
            return;
        }

        event.reply(localizer.localize("conversation.start", guild)).queueAfter(2, TimeUnit.SECONDS, message -> {
            conversation.inject(event.getUser(), localizer, this);
            conversation.start(event.getMessageChannel());
            conversations.putIfAbsent(UserChannelKey.of(event.getUser(), event.getMessageChannel()), conversation);
        });
    }

    public Conversation getDialog(UserChannelKey userChannelKey) {
        return conversations.get(userChannelKey);
    }

    public boolean dialogInProgress(User user, MessageChannel channel) {
        return conversations.containsKey(UserChannelKey.of(user, channel));
    }

    public void endConversation(UserChannelKey key, MessageChannel channel, boolean finished) {
        resolveButton(key);
        removeDialog(key);
        var guild = Channel.guildFromMessageChannel(channel);
        var message = localizer.localize(finished ? "conversation.finished" : "conversation.canceled", guild);
        channel.sendMessage(message).queue();
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (!event.isAcknowledged()) event.deferEdit().queue();

        var location = UserChannelKey.of(event.getUser(), event.getMessageChannel());
        if (button.containsKey(location)) {
            var dialog = button.get(location);
            if (dialog.first != event.getMessageIdLong()) return;

            var result = dialog.second.handleInteraction(event);

            switch (result.type()) {
                case PROCEED -> resolveButton(location);
                case FINISH -> endConversation(location, event.getMessageChannel(), true);
            }
        }
    }

    public void registerButtons(Message message, Conversation conversation) {
        button.put(UserChannelKey.of(conversation.owner(), message.getChannel()), Pair.of(message.getIdLong(), conversation));
    }

    private void resolveButton(UserChannelKey userChannelKey) {
        button.remove(userChannelKey);
    }

    private void removeDialog(UserChannelKey key) {
        var remove = conversations.remove(key);
        if (remove != null) {
            remove.close();
        }
    }
}
