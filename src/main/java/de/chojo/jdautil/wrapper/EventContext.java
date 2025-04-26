/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.wrapper;

import de.chojo.jdautil.conversation.Conversation;
import de.chojo.jdautil.conversation.ConversationService;
import de.chojo.jdautil.interactions.base.SkuMeta;
import de.chojo.jdautil.interactions.dispatching.InteractionHub;
import de.chojo.jdautil.interactions.dispatching.InteractionHubBuilder;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.localization.LocalizationContext;
import de.chojo.jdautil.localization.Localizer.Builder;
import de.chojo.jdautil.localization.util.LocaleProvider;
import de.chojo.jdautil.localization.util.Replacement;
import de.chojo.jdautil.menus.MenuAction;
import de.chojo.jdautil.menus.MenuService;
import de.chojo.jdautil.modals.handler.ModalHandler;
import de.chojo.jdautil.modals.service.ModalService;
import de.chojo.jdautil.pagination.PageService;
import de.chojo.jdautil.pagination.bag.IPageBag;
import net.dv8tion.jda.api.entities.Entitlement;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

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

    /**
     * Registers a new conversation.
     * <p>
     * Requires that a {@link ConversationService} is registered via {@link InteractionHubBuilder#withConversationSystem()}
     */
    public ConversationService conversationService() {
        return conversationService;
    }

    /**
     * Registers a new conversation.
     * <p>
     * Requires that a {@link ConversationService} is registered via {@link InteractionHubBuilder#withConversationSystem()}
     *
     * @param conversation conversation to start
     */
    public void startDialog(Conversation conversation) {
        conversationService.startDialog(event, conversation);
    }

    /**
     * Localizes a message with the guild locale if the event happened on a guild. Will use user locale if it happened in a private message.
     *
     * @param message      message
     * @param replacements replacements
     * @return localized message
     */
    public String localize(String message, Replacement... replacements) {
        if (event.isFromGuild()) {
            return guildLocale(message, replacements);
        }
        return userLocale(message, replacements);
    }

    /**
     * Localizes the key with the defined language of the guild or by the locale provided by the
     * {@link Builder#withLanguageProvider(Function)} when the language is the default value.
     *
     * @param message      message
     * @param replacements replacements
     * @return localized message
     */
    public String guildLocale(String message, Replacement... replacements) {
        return localizer.localize(message, event.getGuild(), replacements);
    }

    /**
     * Localizes the key with the defined language of the user.
     *
     * @param message      message
     * @param replacements replacements
     * @return localized message
     */
    public String userLocale(String message, Replacement... replacements) {
        return localizer.localize(message, LocaleProvider.user(event), replacements);
    }

    /**
     * Registers a new menu action.
     * <p>
     * Requires that a {@link MenuService} is registered via {@link InteractionHubBuilder#withMenuService(Consumer)} or {@link InteractionHubBuilder#withDefaultMenuService()}
     *
     * @param action action to register
     */
    public void registerMenu(MenuAction action) {
        if (event == null) {
            throw new UnsupportedOperationException("menus can be only used on interactions");
        }
        menus.register(action);
    }

    /**
     * Registers a new page.
     * <p>
     * Requires that a {@link PageService} is registered via {@link InteractionHubBuilder#withPagination(Consumer)} or {@link InteractionHubBuilder#withDefaultPagination()}
     *
     * @param page page to register
     */
    public void registerPage(IPageBag page) {
        if (event == null) {
            throw new UnsupportedOperationException("Pages can be only used on interactions");
        }
        pages.registerPage(event, page);
    }

    /**
     * Registers a new page.
     * <p>
     * Requires that a {@link PageService} is registered via {@link InteractionHubBuilder#withPagination(Consumer)} or {@link InteractionHubBuilder#withDefaultPagination()}
     *
     * @param page      page to register
     * @param ephemeral true so send the reply ephemeral
     */
    public void registerPage(IPageBag page, boolean ephemeral) {
        if (event == null) {
            throw new UnsupportedOperationException("Pages can be only used on interactions");
        }
        pages.registerPage(event, page, ephemeral);
    }

    /**
     * Registers a new modal
     * Requires that a {@link ModalService} is registered via {@link InteractionHubBuilder#withModalService(Consumer)} or {@link InteractionHubBuilder#withDefaultModalService()}
     *
     * @param modalHandler modal handler
     */
    public void registerModal(ModalHandler modalHandler) {
        if (event instanceof GenericCommandInteractionEvent event) {
            modalService.registerModal(event, modalHandler);
        } else {
            throw new UnsupportedOperationException("The interaction is not a command interaction");
        }
    }

    /**
     * Returns a guild localizer, which uses the {@link Guild#getLocale()}
     *
     * @return localizer with guild locale linked
     */
    public LocalizationContext guildLocalizer() {
        return new LocalizationContext(localizer, LocaleProvider.guild(event));
    }

    /**
     * @return guild localizer
     * @deprecated Removed in favor or {@link EventContext#guildLocalizer()}
     */
    @Deprecated(forRemoval = true)
    public LocalizationContext localizer() {
        return guildLocalizer();
    }

    /**
     * Returns a guild localizer, which uses the {@link Interaction#getUserLocale()}
     *
     * @return localizer with user locale linked
     */
    public LocalizationContext userLocalizer() {
        return new LocalizationContext(localizer, LocaleProvider.user(event));
    }

    /**
     * Returns the command hub
     *
     * @return command hub
     * @deprecated deprecated in favor of {@link #interactionHub()}
     */
    @Deprecated(forRemoval = true)
    public InteractionHub<?, ?, ?> commandHub() {
        return interactionHub;
    }

    /**
     * Returns the interaction hub, which dispatched the interaction
     *
     * @return interaction hub instance
     */
    public InteractionHub<?, ?, ?> interactionHub() {
        return interactionHub;
    }

    /**
     * Returns the list of entitlements for the current guild and user.
     * If this interaction is not from a guild, it will only contain entitlements of the user.
     * @return list of entitlements
     */
    public List<Entitlement> entitlements(){
        return event.getEntitlements();
    }
}
