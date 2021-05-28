package de.chojo.jdautil.wrapper;

import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.localization.util.Replacement;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageUpdateEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import javax.annotation.CheckReturnValue;
import java.util.Collections;
import java.util.concurrent.TimeUnit;


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
    private final boolean isUpdate;
    private ILocalizer localizer;

    private MessageEventWrapper(JDA jda, long messageId, long responseNumber, Guild guild, Member member,
                                boolean isWebhookMessage, Message message, User author, TextChannel textChannel,
                                boolean isUpdate) {
        this.jda = jda;
        this.messageId = messageId;
        this.responseNumber = responseNumber;
        this.guild = guild;
        this.member = member;
        this.isWebhookMessage = isWebhookMessage;
        this.message = message;
        this.author = author;
        this.textChannel = textChannel;
        this.isUpdate = isUpdate;
    }

    public static MessageEventWrapper fake() {
        return new MessageEventWrapper(null, 0, 0, null, null, false,
                null, null, null, false);
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
                event.getChannel(),
                false
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
                event.getChannel(),
                true
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
                null,
                false
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
                null,
                true
        );
    }

    public static MessageEventWrapper create(SlashCommandEvent event) {
        return new MessageEventWrapper(
                event.getJDA(),
                0,
                event.getResponseNumber(),
                null,
                null,
                false,
                null,
                event.getUser(),
                null,
                true
        );
    }

    public void registerLocalizer(ILocalizer localizer) {
        this.localizer = localizer;
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

    public ChannelLocation getChannelLocation() {
        return new ChannelLocation(guild, textChannel, author);
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public String localize(String message, Replacement... replacements) {
        return localizer.localize(message, guild, replacements);
    }

    @CheckReturnValue

    public MessageAction answer(String message) {
        return getChannel().sendMessage(message);
    }

    @CheckReturnValue
    public MessageAction answer(MessageEmbed embed) {
        return getChannel().sendMessage(embed);
    }

    @CheckReturnValue
    public MessageAction replyMention(String message) {
        return getMessage().reply(message);
    }

    @CheckReturnValue
    public MessageAction replyMention(MessageEmbed embed) {
        return getMessage().reply(embed);
    }

    @CheckReturnValue
    public MessageAction reply(String message) {
        return replyMention(message).allowedMentions(Collections.emptyList()).mentionRepliedUser(false);
    }

    @CheckReturnValue
    public MessageAction reply(MessageEmbed embed) {
        return replyMention(embed).allowedMentions(Collections.emptyList()).mentionRepliedUser(false);
    }

    @CheckReturnValue
    public void replyErrorAndDelete(MessageEmbed embed, int deleteDelay) {
        getMessage().delete().queueAfter(0, TimeUnit.SECONDS);
        reply(embed).queue(m -> m.delete().queueAfter(deleteDelay, TimeUnit.SECONDS));
    }

    @CheckReturnValue
    public void replyErrorAndDelete(String message, int deleteDelay) {
        getMessage().delete().queueAfter(0, TimeUnit.SECONDS);
        reply(message).queue(m -> m.delete().queueAfter(deleteDelay, TimeUnit.SECONDS));
    }

    public boolean hasLocalizer() {
        return localizer != null;
    }
}
