package de.chojo.jdautil.conversation.elements;

import de.chojo.jdautil.conversation.Conversation;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.NotNull;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class Context {
    private final Conversation conversation;
    private final Message message;
    private final Map<String, Object> data;

    public Context(Conversation conversation, Map<String, Object> data, Message message) {
        this.conversation = conversation;
        this.data = data;
        this.message = message;
    }

    public Conversation conversation() {
        return conversation;
    }

    public Message message() {
        return message;
    }

    public MessageAction reply(String message) {
        return this.message.reply(message);
    }

    @Nonnull
    public User getAuthor() {
        return message.getAuthor();
    }

    @Nullable
    public Member getMember() {
        return message.getMember();
    }

    @Nonnull
    public String getContentDisplay() {
        return message.getContentDisplay();
    }

    @Nonnull
    public String getContentRaw() {
        return message.getContentRaw();
    }

    @Nonnull
    public String getContentStripped() {
        return message.getContentStripped();
    }

    @Nonnull
    public Guild getGuild() {
        return message.getGuild();
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction reply(@NotNull CharSequence content) {
        return message.reply(content);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction reply(@NotNull MessageEmbed content) {
        return message.reply(content);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction reply(@NotNull Message content) {
        return message.reply(content);
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
