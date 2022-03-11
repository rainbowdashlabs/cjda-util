/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.conversation.elements;

import de.chojo.jdautil.conversation.Conversation;

import java.util.Map;

public abstract class Context implements ConversationContext {
    private final Conversation conversation;
    private final Map<String, Object> data;

    public Context(Conversation conversation, Map<String, Object> data) {
        this.conversation = conversation;
        this.data = data;
    }

    @Override
    public Conversation conversation() {
        return conversation;
    }

    @Override
    public Object put(String key, Object value) {
        return data.put(key, value);
    }

    @Override
    public Object putIfAbsent(String key, Object value) {
        return data.putIfAbsent(key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) data.get(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(String key, T defaultValue) {
        return (T) data.getOrDefault(key, defaultValue);
    }
}
