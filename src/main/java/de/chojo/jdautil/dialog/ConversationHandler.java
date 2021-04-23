package de.chojo.jdautil.dialog;

import de.chojo.jdautil.wrapper.ChannelLocation;
import de.chojo.jdautil.wrapper.MessageEventWrapper;

import java.awt.Dialog;
import java.util.HashMap;
import java.util.Map;

public class ConversationHandler {
    // Guild -> Channel -> User -> Dialogue
    private final Map<ChannelLocation, Conversation> conversations = new HashMap<>();

    /**
     * Invoke a dialog in this context.
     *
     * @param eventWrapper wrapper of message Event
     *
     * @return true if a dialog was invoked.
     */
    public boolean invoke(MessageEventWrapper eventWrapper) {
        if (eventWrapper.isUpdate()) return false;

        var content = eventWrapper.getMessage().getContentRaw();
        if ("exit".equalsIgnoreCase(content) || "cancel".equalsIgnoreCase(content)) {
            if (removeDialog(eventWrapper)) {
                eventWrapper.getChannel().sendMessage("Canceled.").queue();
            }
            return true;
        }

        var dialog = getDialog(eventWrapper);
        if (dialog != null) {
            if (dialog.invoke(eventWrapper)) {
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
            eventWrapper.getChannel().sendMessage("A dialog is already in progress. Finish dialog or type \"exit\" to end the current dialog.").queue();
            return;
        }

        var promptText = conversation.getPromptText(eventWrapper);
        if (promptText.isBlank()) return;
        eventWrapper.getChannel().sendMessage(promptText).queue();

        conversations.putIfAbsent(eventWrapper.getChannelLocation(), conversation);
    }

    public Conversation getDialog(MessageEventWrapper eventWrapper) {
        return conversations.get(eventWrapper.getChannelLocation());
    }
}
