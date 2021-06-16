package de.chojo.jdautil.conversation;

import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.wrapper.ChannelLocation;
import de.chojo.jdautil.wrapper.MessageEventWrapper;

import java.util.HashMap;
import java.util.Map;

public class ConversationService {
    private final ILocalizer localizer;
    // Guild -> Channel -> User -> Dialogue
    private final Map<ChannelLocation, Conversation> conversations = new HashMap<>();

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
            if (removeDialog(eventWrapper)) {
                var canceled = localizer.localize("conversation.canceled", eventWrapper.getGuild());
                eventWrapper.getChannel().sendMessage(canceled).queue();
            }
            return true;
        }

        var dialog = getDialog(eventWrapper);
        if (dialog != null) {
            if (dialog.handle(eventWrapper.getMessage())) {
                dialog.close();
                removeDialog(eventWrapper);
            }
            return true;
        }
        return false;
    }

    public boolean dialogInProgress(MessageEventWrapper eventWrapper) {
        return conversations.containsKey(eventWrapper.getChannelLocation());
    }

    public boolean removeDialog(MessageEventWrapper eventWrapper) {
        return conversations.remove(eventWrapper.getChannelLocation()) != null;
    }

    public void startDialog(MessageEventWrapper eventWrapper, Conversation conversation) {
        if (dialogInProgress(eventWrapper)) {
            var message = localizer.localize("conversation.inProgress", eventWrapper.getGuild());
            eventWrapper.getChannel().sendMessage(message).queue();
            return;
        }

        conversation.addLocalizer(localizer);
        conversation.start(eventWrapper.getTextChannel());
        conversations.putIfAbsent(eventWrapper.getChannelLocation(), conversation);
    }

    public Conversation getDialog(MessageEventWrapper eventWrapper) {
        return conversations.get(eventWrapper.getChannelLocation());
    }
}
