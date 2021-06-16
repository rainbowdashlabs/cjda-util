package de.chojo.jdautil.conversation.elements;

import net.dv8tion.jda.api.entities.Message;

import java.util.Map;

public class Context {
    private Message message;
    private Map<String, Object> data;

    public Context(Map<String, Object> data, Message message) {
        this.data = data;
        this.message = message;
    }

    public Message message() {
        return message;
    }

    public Object put(String key, Object value) {
        return data.put(key, value);
    }

    public Object putIfAbsent(String key, Object value) {
        return data.putIfAbsent(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) data.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(String key, T defaultValue) {
        return (T) data.getOrDefault(key, defaultValue);
    }
}
