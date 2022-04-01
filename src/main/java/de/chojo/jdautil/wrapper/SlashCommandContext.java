/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.wrapper;

import de.chojo.jdautil.buttons.ButtonAction;
import de.chojo.jdautil.buttons.ButtonService;
import de.chojo.jdautil.command.dispatching.CommandHub;
import de.chojo.jdautil.conversation.Conversation;
import de.chojo.jdautil.conversation.ConversationService;
import de.chojo.jdautil.localization.ContextLocalizer;
import de.chojo.jdautil.localization.util.Replacement;
import de.chojo.jdautil.pagination.PageService;
import de.chojo.jdautil.pagination.bag.IPageBag;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public class SlashCommandContext {
    private final IReplyCallback event;
    private final ConversationService conversationService;
    private final ContextLocalizer contextLocalizer;
    private final ButtonService buttons;
    private final PageService pages;
    private final CommandHub<?> commandHub;

    public SlashCommandContext(IReplyCallback event, ConversationService conversationService, ContextLocalizer contextLocalizer, ButtonService buttons, PageService pages, CommandHub<?> commandHub) {
        this.event = event;
        this.conversationService = conversationService;
        this.contextLocalizer = contextLocalizer;
        this.buttons = buttons;
        this.pages = pages;
        this.commandHub = commandHub;
    }

    public ConversationService conversationService() {
        return conversationService;
    }

    public void startDialog(Conversation conversation) {
        conversationService.startDialog(event, conversation);
    }

    public String localize(String message, Replacement... replacements) {
        return contextLocalizer.localize(message, replacements);
    }

    public void registerButtons(ButtonAction interaction) {
        if (event == null) {
            throw new UnsupportedOperationException("buttons can be only used on interactions");
        }
        buttons.register(interaction);
    }

    public void registerPage(IPageBag page) {
        if (event == null) {
            throw new UnsupportedOperationException("Pages can be only used on interactions");
        }
        pages.registerPage(event, page);
    }
    public void registerPage(IPageBag page, boolean ephemeral) {
        if (event == null) {
            throw new UnsupportedOperationException("Pages can be only used on interactions");
        }
        pages.registerPage(event, page, ephemeral);
    }

    public ContextLocalizer localizer() {
        return contextLocalizer;
    }

    public CommandHub<?> commandHub() {
        return commandHub;
    }
}
