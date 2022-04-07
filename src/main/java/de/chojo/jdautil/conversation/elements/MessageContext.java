/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.conversation.elements;

import de.chojo.jdautil.conversation.Conversation;
import de.chojo.jdautil.localization.ContextLocalizer;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.localization.util.Replacement;
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
    ContextLocalizer localizer;

    public MessageContext(Conversation conversation, Map<String, Object> data, Message message, ILocalizer localizer) {
        super(conversation, data);
        this.message = message;
        this.localizer = localizer.getContextLocalizer(message.isFromGuild() ? message.getGuild() : null);
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

    @Override
    public String localize(String message, Replacement... replacements) {
        return localizer.localize(message, replacements);
    }

    @Override
    public ContextLocalizer localizer() {
        return localizer;
    }
}
