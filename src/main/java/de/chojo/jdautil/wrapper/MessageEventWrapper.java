package de.chojo.jdautil.wrapper;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageUpdateEvent;


public class MessageEventWrapper {
    private final JDA jda;
    private final long messageId;
    private final long responseNumber;
    private final Guild guild;
    private final Member member;
    private final boolean isWebhookMessage;
    private final Message message;
    private final User author;
    private final TextChannel textChannel;

    private MessageEventWrapper(JDA jda, long messageId, long responseNumber, Guild guild, Member member,
                                boolean isWebhookMessage, Message message, User author, TextChannel textChannel) {
        this.jda = jda;
        this.messageId = messageId;
        this.responseNumber = responseNumber;
        this.guild = guild;
        this.member = member;
        this.isWebhookMessage = isWebhookMessage;
        this.message = message;
        this.author = author;
        this.textChannel = textChannel;
    }

    public static MessageEventWrapper create(GuildMessageReceivedEvent event) {
        return new MessageEventWrapper(
                event.getJDA(),
                event.getMessageIdLong(),
                event.getResponseNumber(),
                event.getGuild(),
                event.getMember(),
                event.isWebhookMessage(),
                event.getMessage(),
                event.getAuthor(),
                event.getChannel()
        );
    }

    public static MessageEventWrapper create(GuildMessageUpdateEvent event) {
        return new MessageEventWrapper(
                event.getJDA(),
                event.getMessageIdLong(),
                event.getResponseNumber(),
                event.getGuild(),
                event.getMember(),
                false,
                event.getMessage(),
                event.getAuthor(),
                event.getChannel()
        );
    }

    public static MessageEventWrapper create(PrivateMessageReceivedEvent event) {
        return new MessageEventWrapper(
                event.getJDA(),
                event.getMessageIdLong(),
                event.getResponseNumber(),
                null,
                null,
                false,
                event.getMessage(),
                event.getAuthor(),
                null
        );
    }

    public static MessageEventWrapper create(PrivateMessageUpdateEvent event) {
        return new MessageEventWrapper(
                event.getJDA(),
                event.getMessageIdLong(),
                event.getResponseNumber(),
                null,
                null,
                false,
                event.getMessage(),
                event.getAuthor(),
                null
        );
    }

    public MessageChannel getChannel() {
        if (isGuild()) {
            return textChannel;
        }
        return author.openPrivateChannel().complete();
    }

    public boolean isGuild() {
        return guild != null;
    }

    public boolean isPrivate() {
        return !isGuild();
    }

    public JDA getJda() {
        return jda;
    }

    public long getMessageIdLong() {
        return messageId;
    }
    public String getMessageId() {
        return String.valueOf(messageId);
    }

    public long getResponseNumber() {
        return responseNumber;
    }

    public Guild getGuild() {
        return guild;
    }

    public Member getMember() {
        return member;
    }

    public boolean isWebhookMessage() {
        return isWebhookMessage;
    }

    public Message getMessage() {
        return message;
    }

    public User getAuthor() {
        return author;
    }

    public TextChannel getTextChannel() {
        return textChannel;
    }
}
