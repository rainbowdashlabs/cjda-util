/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.conversation.elements;

import de.chojo.jdautil.conversation.Conversation;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.localization.LocalizationContext;
import de.chojo.jdautil.localization.util.LocaleProvider;
import de.chojo.jdautil.localization.util.Replacement;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Map;

public class InteractionContext extends Context {
    private final ComponentInteraction interaction;
    private final LocalizationContext localizer;

    public InteractionContext(Map<String, Object> data, Conversation conversation, ComponentInteraction interaction, ILocalizer localizer) {
        super(conversation, data);
        this.interaction = interaction;
        this.localizer = localizer.context(LocaleProvider.guild(interaction));
    }

    @Override
    public Message message() {
        return interaction.getMessage();
    }

    @NotNull
    @Override
    public User getAuthor() {
        return interaction.getUser();
    }

    @Nullable
    @Override
    public Member getMember() {
        return interaction.getMember();
    }

    @NotNull
    @Override
    public String getContentDisplay() {
        return interaction.getComponentId();
    }

    @NotNull
    @Override
    public String getContentRaw() {
        return interaction.getComponentId();
    }

    @NotNull
    @Override
    public String getContentStripped() {
        return interaction.getComponentId();
    }

    @Override
    public Guild getGuild() {
        return interaction.getGuild();
    }

    @NotNull
    @Override
    public MessageCreateAction reply(@NotNull String content) {
        return interaction.getChannel().sendMessage(MessageCreateData.fromContent(content));
    }

    @Override
    public @NotNull MessageCreateAction reply(@NotNull MessageEmbed content) {
        return interaction.getChannel().sendMessageEmbeds(content);
    }

    @Override
    public @NotNull MessageCreateAction reply(@NotNull Message content) {
        return interaction.getChannel().sendMessage(MessageCreateData.fromMessage(content));
    }

    @Nonnull
    public String getComponentId() {
        return interaction.getComponentId();
    }

    @Nonnull
    public Component.Type getComponentType() {
        return interaction.getComponentType();
    }

    @Override
    public String localize(String message, Replacement... replacements) {
        return localizer.localize(message, replacements);
    }

    @Override
    public LocalizationContext localizer() {
        return localizer;
    }
}
