/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.wrapper;

import de.chojo.jdautil.conversation.Conversation;
import de.chojo.jdautil.conversation.ConversationService;
import de.chojo.jdautil.localization.ContextLocalizer;
import de.chojo.jdautil.localization.util.Replacement;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

public class SlashCommandContext {
    private final ConversationService conversationService;
    private final ContextLocalizer contextLocalizer;

    public SlashCommandContext(ConversationService conversationService, ContextLocalizer contextLocalizer) {
        this.conversationService = conversationService;
        this.contextLocalizer = contextLocalizer;
    }

    public ConversationService conversationService() {
        return conversationService;
    }

    public void startDialog(CommandInteraction interaction, Conversation conversation) {
        conversationService.startDialog(interaction, conversation);
    }

    public String localize(String message, Replacement... replacements) {
        return contextLocalizer.localize(message, replacements);
    }
}
