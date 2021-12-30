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

public class MessageContext extends Context {
    private final Message message;

    public MessageContext(Conversation conversation, Map<String, Object> data, Message message) {
        super(conversation, data);
        this.message = message;
    }


    @Override
    public Message message() {
        return message;
    }

    @Override
    public MessageAction reply(String message) {
        return this.message.reply(message);
    }

    @Override
    @Nonnull
    public User getAuthor() {
        return message.getAuthor();
    }

    @Override
    @Nullable
    public Member getMember() {
        return message.getMember();
    }

    @Override
    @Nonnull
    public String getContentDisplay() {
        return message.getContentDisplay();
    }

    @Override
    @Nonnull
    public String getContentRaw() {
        return message.getContentRaw();
    }

    @Override
    @Nonnull
    public String getContentStripped() {
        return message.getContentStripped();
    }

    @Override
    @Nonnull
    public Guild getGuild() {
        return message.getGuild();
    }

    @Override
    @CheckReturnValue
    @Nonnull
    public MessageAction reply(@NotNull CharSequence content) {
        return message.reply(content);
    }

    @Override
    @CheckReturnValue
    @Nonnull
    public MessageAction reply(@NotNull MessageEmbed content) {
        return message.replyEmbeds(content);
    }

    @Override
    @CheckReturnValue
    @Nonnull
    public MessageAction reply(@NotNull Message content) {
        return message.reply(content);
    }

}
