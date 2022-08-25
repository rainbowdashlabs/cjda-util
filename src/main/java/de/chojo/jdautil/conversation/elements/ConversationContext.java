/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.conversation.elements;

import de.chojo.jdautil.conversation.Conversation;
import de.chojo.jdautil.localization.LocalizationContext;
import de.chojo.jdautil.localization.util.Replacement;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.jetbrains.annotations.NotNull;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

public interface ConversationContext {
    Message message();

    @Nonnull
    User getAuthor();

    Member getMember();

    @Nonnull
    String getContentDisplay();

    @Nonnull
    String getContentRaw();

    @Nonnull
    String getContentStripped();

    Guild getGuild();

    @CheckReturnValue
    @Nonnull
    MessageCreateAction reply(@NotNull String content);

    @CheckReturnValue
    @Nonnull
    MessageCreateAction reply(@NotNull Message content);

    @CheckReturnValue
    @Nonnull
    MessageCreateAction reply(@NotNull MessageEmbed content);

    Conversation conversation();

    Object put(String key, Object value);

    Object putIfAbsent(String key, Object value);

    @SuppressWarnings("unchecked")
    <T> T get(String key);

    @SuppressWarnings("unchecked")
    <T> T getOrDefault(String key, T defaultValue);

    String localize(String message, Replacement... replacements);

    LocalizationContext localizer();
}
