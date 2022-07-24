/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.wrapper;

import de.chojo.jdautil.conversation.Conversation;
import de.chojo.jdautil.conversation.ConversationService;
import de.chojo.jdautil.interactions.dispatching.InteractionHub;
import de.chojo.jdautil.localization.LocalizationContext;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.localization.util.LocaleProvider;
import de.chojo.jdautil.localization.util.Replacement;
import de.chojo.jdautil.menus.MenuAction;
import de.chojo.jdautil.menus.MenuService;
import de.chojo.jdautil.modals.handler.ModalHandler;
import de.chojo.jdautil.modals.service.ModalService;
import de.chojo.jdautil.pagination.PageService;
import de.chojo.jdautil.pagination.bag.IPageBag;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public class EventContext {
    private final IReplyCallback event;
    private final ConversationService conversationService;
    private final ILocalizer localizer;
    private final MenuService menus;
    private final PageService pages;
    private final ModalService modalService;
    private final InteractionHub<?, ?, ?> interactionHub;

    public EventContext(IReplyCallback event, ConversationService conversationService, ILocalizer localizer, MenuService menus, PageService pages, ModalService modalService, InteractionHub<?, ?, ?> interactionHub) {
        this.event = event;
        this.conversationService = conversationService;
        this.localizer = localizer;
        this.menus = menus;
        this.pages = pages;
        this.modalService = modalService;
        this.interactionHub = interactionHub;
    }

    public ConversationService conversationService() {
        return conversationService;
    }

    public void startDialog(Conversation conversation) {
        conversationService.startDialog(event, conversation);
    }

    public String localize(String message, Replacement... replacements) {
        return guildLocale(message, replacements);
    }

    public String guildLocale(String message, Replacement... replacements) {
        return localizer.localize(message, LocaleProvider.guild(event), replacements);
    }

    public String userLocale(String message, Replacement... replacements) {
        return localizer.localize(message, LocaleProvider.user(event), replacements);
    }

    public void registerMenu(MenuAction interaction) {
        if (event == null) {
            throw new UnsupportedOperationException("menus can be only used on interactions");
        }
        menus.register(interaction);
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

    public void registerModal(ModalHandler modalHandler) {
        if (event instanceof GenericCommandInteractionEvent event) {
            modalService.registerModal(event, modalHandler);
        } else {
            throw new UnsupportedOperationException("The interaction is not a command interaction");
        }
    }

    public LocalizationContext guildLocalizer() {
        return new LocalizationContext(localizer, LocaleProvider.guild(event));
    }

    /**
     *
     * @return
     * @deprecated Removed in favor or {@link EventContext#guildLocalizer()}
     */
    @Deprecated(forRemoval = true)
    public LocalizationContext localizer() {
        return guildLocalizer();
    }

    public LocalizationContext userLocalizer() {
        return new LocalizationContext(localizer, LocaleProvider.user(event));
    }

    public InteractionHub<?, ?, ?> commandHub() {
        return interactionHub;
    }
}
