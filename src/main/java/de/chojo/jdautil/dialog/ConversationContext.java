package de.chojo.jdautil.dialog;

import de.chojo.jdautil.wrapper.MessageEventWrapper;

import java.util.Map;

public class ConversationContext {
    private final MessageEventWrapper eventWrapper;
    private final Map<String, Object> results;
    private boolean success;


    public ConversationContext(MessageEventWrapper eventWrapper, Map<String, Object> results) {
        this.eventWrapper = eventWrapper;
        this.results = results;
    }

    public MessageEventWrapper getEventWrapper() {
        return eventWrapper;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setResult(String key, Object result) {
        success = true;
        this.results.put(key, result);
    }


}
