/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.chojo.jdautil.wrapper;

import de.chojo.jdautil.conversation.Conversation;
import de.chojo.jdautil.conversation.ConversationService;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class SlashCommandContext {
    private final ConversationService conversationService;

    public SlashCommandContext(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    public ConversationService conversationService() {
        return conversationService;
    }

    public void startDialog(SlashCommandEvent event, Conversation conversation) {
        conversationService.startDialog(event.getUser(), event.getTextChannel(), conversation);
    }
}
