package de.chojo.jdautil.conversation;

import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.wrapper.ChannelLocation;
import de.chojo.jdautil.wrapper.MessageEventWrapper;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ConversationService extends ListenerAdapter {
    private final ILocalizer localizer;
    // Guild -> Channel -> User -> Dialogue
    private final Map<ChannelLocation, Conversation> conversations = new HashMap<>();
    private final Map<ChannelLocation, Conversation> button = new HashMap<>();

    public ConversationService(ILocalizer localizer) {
        this.localizer = localizer;
    }

    /**
     * Invoke a dialog in this context.
     *
     * @param eventWrapper wrapper of message Event
     * @return true if a dialog was invoked.
     */
    public boolean invoke(MessageEventWrapper eventWrapper) {
        if (eventWrapper.isUpdate()) return false;

        var content = eventWrapper.getMessage().getContentRaw();
        var cancel = localizer.localize("conversation.canceled", eventWrapper.getGuild());
        var exit = localizer.localize("conversation.exit", eventWrapper.getGuild());
        if (exit.equalsIgnoreCase(content) || cancel.equalsIgnoreCase(content)) {
            if (removeDialog(eventWrapper.getAuthor(), eventWrapper.getTextChannel())) {
                var canceled = localizer.localize("conversation.canceled", eventWrapper.getGuild());
                eventWrapper.getChannel().sendMessage(canceled).queue();
            }
            return true;
        }

        var dialog = getDialog(eventWrapper);
        if (dialog != null) {
            if (dialog.handleMessage(eventWrapper.getMessage())) {
                dialog.close();
                removeDialog(eventWrapper.getAuthor(), eventWrapper.getTextChannel());
            }
            return true;
        }
        return false;
    }

    public boolean dialogInProgress(User user, TextChannel channel) {
        return conversations.containsKey(ChannelLocation.of(user, channel));
    }

    public boolean removeDialog(User user, TextChannel channel) {
        return conversations.remove(ChannelLocation.of(user, channel)) != null;
    }

    public void startDialog(User user, TextChannel channel, Conversation conversation) {
        if (dialogInProgress(user, channel)) {
            var message = localizer.localize("conversation.inProgress", channel.getGuild());
            channel.sendMessage(message).queue();
            return;
        }

        conversation.inject(user, localizer, this);
        conversation.start(channel);
        conversations.putIfAbsent(ChannelLocation.of(user, channel), conversation);
    }

    public Conversation getDialog(MessageEventWrapper eventWrapper) {
        return conversations.get(eventWrapper.getChannelLocation());
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        if (!event.isAcknowledged()) {
            event.deferEdit().queue();
        }
        var location = ChannelLocation.of(event.getUser(), event.getTextChannel());
        if (button.containsKey(location)) {
            var dialog = button.get(location);
            var result = dialog.handleInteraction(event);
            switch (result.type()) {
                case PROCEED -> button.remove(location);
                case FINISH -> {
                    button.remove(location);
                    dialog.close();
                    removeDialog(event.getUser(), event.getTextChannel());
                }
            }
        }
    }

    public void registerButtons(Message message, Conversation conversation) {
        button.put(ChannelLocation.of(conversation.owner(), message.getTextChannel()), conversation);
    }
}
