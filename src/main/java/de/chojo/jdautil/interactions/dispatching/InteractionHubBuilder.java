/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.dispatching;

import de.chojo.jdautil.conversation.ConversationService;
import de.chojo.jdautil.interactions.base.InteractionMeta;
import de.chojo.jdautil.interactions.premium.SKU;
import de.chojo.jdautil.interactions.premium.SKUConfiguration;
import de.chojo.jdautil.interactions.message.Message;
import de.chojo.jdautil.interactions.message.provider.MessageProvider;
import de.chojo.jdautil.interactions.slash.Slash;
import de.chojo.jdautil.interactions.slash.provider.SlashProvider;
import de.chojo.jdautil.interactions.user.User;
import de.chojo.jdautil.interactions.user.provider.UserProvider;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.localization.LocalizationContext;
import de.chojo.jdautil.menus.MenuService;
import de.chojo.jdautil.menus.MenuServiceBuilder;
import de.chojo.jdautil.menus.MenuServiceModifier;
import de.chojo.jdautil.modals.service.ModalService;
import de.chojo.jdautil.modals.service.ModalServiceBuilder;
import de.chojo.jdautil.modals.service.ModalServiceModifier;
import de.chojo.jdautil.pagination.PageService;
import de.chojo.jdautil.pagination.PageServiceBuilder;
import de.chojo.jdautil.pagination.PageServiceModifier;
import de.chojo.jdautil.util.SysVar;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.slf4j.LoggerFactory.getLogger;

public class InteractionHubBuilder<T extends Slash, M extends Message, U extends User> {
    private static final Logger log = getLogger(InteractionHubBuilder.class);
    private final ShardManager shardManager;
    private final Map<String, T> commands = new HashMap<>();
    private final Map<String, M> messages = new HashMap<>();
    private final Map<String, U> users = new HashMap<>();
    @NotNull
    private ILocalizer localizer = ILocalizer.DEFAULT;

    private Consumer<InteractionResult<T>> postCommandHook = r -> {
    };
    private boolean withConversations;
    @Deprecated
    private BiConsumer<InteractionContext, Throwable> commandErrorHandler =
            (context, err) -> log.error("An unhandled exception occurred while executing command {}: {}", context.interaction(), context.args(), err);
    private PageServiceBuilder pagination;
    private MenuServiceBuilder menuService;
    private ModalServiceBuilder modalService;
    private Function<InteractionMeta, List<Long>> guildCommandMapper = meta -> Collections.emptyList();
    private boolean cleanGuildCommands = Boolean.parseBoolean(SysVar.envOrProp("CJDA_INTERACTIONS_CLEANGUILDCOMMANDS", "cjda.interactions.cleanguildcommands", "false"));
    ;
    private boolean testMode = Boolean.parseBoolean(SysVar.envOrProp("CJDA_INTERACTIONS_TESTMODE", "cjda.interactions.testmode", "false"));
    private String premiumErrorMessage = "error.premium";
    private SKUConfiguration skuConfiguration = new SKUConfiguration();
private BiFunction<net.dv8tion.jda.api.entities.User, Guild, List<SKU>> entitlementProvider = (user, guild) -> Collections.emptyList();
    InteractionHubBuilder(ShardManager shardManager) {
        this.shardManager = shardManager;
    }

    public InteractionHubBuilder<T, M, U> withGuildCommandMapper(Function<InteractionMeta, List<Long>> commandMapper) {
        this.guildCommandMapper = commandMapper;
        return this;
    }

    /**
     * Clean all guild commands by redeploying commands to all guilds.
     *
     * @return builder instance
     */
    public InteractionHubBuilder<T, M, U> cleanGuildCommands() {
        return cleanGuildCommands(true);
    }

    /**
     * Clean all guild commands by redeploying commands to all guilds.
     *
     * @param cleanGuildCommands true to clean
     * @return builder instance
     */
    public InteractionHubBuilder<T, M, U> cleanGuildCommands(boolean cleanGuildCommands) {
        this.cleanGuildCommands = cleanGuildCommands;
        return this;
    }

    /**
     * Setting the hub to test mode.
     * This will cause all global interactions to be deployed on a guild level instead and removing global interactions.
     *
     * @param testMode true to activate
     * @return builder instance
     */
    public InteractionHubBuilder<T, M, U> testMode(boolean testMode) {
        this.testMode = testMode;
        return this;
    }

    /**
     * Setting the hub to test mode.
     * This will cause all global interactions to be deployed on a guild level instead and removing global interactions.
     *
     * @return builder instance
     */
    public InteractionHubBuilder<T, M, U> testMode() {
        return testMode(true);
    }

    /**
     * Adds a localizer to the hub. This will allow to use {@link LocalizationContext}.
     *
     * @param localizer localizer instance
     * @return builder instance
     */
    public InteractionHubBuilder<T, M, U> withLocalizer(ILocalizer localizer) {
        this.localizer = localizer;
        return this;
    }

    /**
     * Register slash interactions
     *
     * @param commands commands to register
     * @return builder instance
     */
    @SafeVarargs
    public final InteractionHubBuilder<T, M, U> withCommands(T... commands) {
        for (var command : commands) {
            this.commands.put(command.meta().name().toLowerCase(Locale.ROOT), command);
        }
        return this;
    }

    /**
     * Register slash interactions
     *
     * @param commands commands to register
     * @return builder instance
     */
    @SafeVarargs
    public final InteractionHubBuilder<T, M, U> withCommands(SlashProvider<T>... commands) {
        for (var command : commands) {
            this.commands.put(command.slash().meta().name().toLowerCase(Locale.ROOT), command.slash());
        }
        return this;
    }

    /**
     * Register message interactions
     *
     * @param messages commands to register
     * @return builder instance
     */
    @SafeVarargs
    public final InteractionHubBuilder<T, M, U> withMessages(M... messages) {
        for (var message : messages) {
            this.messages.put(message.meta().name().toLowerCase(Locale.ROOT), message);
        }
        return this;
    }

    /**
     * Register message interactions
     *
     * @param messages commands to register
     * @return builder instance
     */
    @SafeVarargs
    public final InteractionHubBuilder<T, M, U> withMessages(MessageProvider<M>... messages) {
        for (var message : messages) {
            this.messages.put(message.message().meta().name().toLowerCase(Locale.ROOT), message.message());
        }
        return this;
    }

    /**
     * Register message interactions
     *
     * @param messages commands to register
     * @return builder instance
     */
    @SafeVarargs
    public final InteractionHubBuilder<T, M, U> withUsers(M... messages) {
        for (var message : messages) {
            this.messages.put(message.meta().name().toLowerCase(Locale.ROOT), message);
        }
        return this;
    }

    /**
     * Register message interactions
     *
     * @param users commands to register
     * @return builder instance
     */
    @SafeVarargs
    public final InteractionHubBuilder<T, M, U> withUsers(UserProvider<U>... users) {
        for (var user : users) {
            this.users.put(user.user().meta().name().toLowerCase(Locale.ROOT), user.user());
        }
        return this;
    }

    /**
     * Adds a conversation system to the command hub
     *
     * @return builder instance
     */
    public InteractionHubBuilder<T, M, U> withConversationSystem() {
        this.withConversations = true;
        return this;
    }

    /**
     * Adds a command error handler to the hub, which handles uncatched exceptions. Used for loggin.
     *
     * @param commandErrorHandler handler for errors
     * @return builder instance
     */
    public InteractionHubBuilder<T, M, U> withCommandErrorHandler(BiConsumer<InteractionContext, Throwable> commandErrorHandler) {
        this.commandErrorHandler = commandErrorHandler;
        return this;
    }

    /**
     * Adds a post command hook to the hub, which gets executed after every successful or unsuccessful command execution.
     *
     * @param postCommandHook handler
     * @return builder instance
     */
    public InteractionHubBuilder<T, M, U> withPostCommandHook(Consumer<InteractionResult<T>> postCommandHook) {
        this.postCommandHook = postCommandHook;
        return this;
    }

    public InteractionHubBuilder<T, M, U> withPagination(Consumer<PageServiceModifier> builder) {
        pagination = PageService.builder(shardManager);
        builder.accept(pagination);
        return this;
    }

    public InteractionHubBuilder<T, M, U> withDefaultPagination() {
        pagination = PageService.builder(shardManager);
        return this;
    }

    public InteractionHubBuilder<T, M, U> withMenuService(Consumer<MenuServiceModifier> builder) {
        menuService = MenuService.builder(shardManager);
        builder.accept(menuService);
        return this;
    }

    public InteractionHubBuilder<T, M, U> withDefaultMenuService() {
        menuService = MenuService.builder(shardManager);
        return this;
    }

    public InteractionHubBuilder<T, M, U> withModalService(Consumer<ModalServiceModifier> builder) {
        modalService = ModalService.builder(shardManager);
        builder.accept(modalService);
        return this;
    }

    public InteractionHubBuilder<T, M, U> withDefaultModalService() {
        modalService = ModalService.builder(shardManager);
        return this;
    }

    public InteractionHubBuilder<T, M, U> withPremiumErrorMessage(String errorMessage) {
        this.premiumErrorMessage = errorMessage;
        return this;
    }

    public InteractionHubBuilder<T, M, U> withSKUConfiguration(SKUConfiguration skuConfiguration) {
        this.skuConfiguration = skuConfiguration;
        return this;
    }

    public InteractionHubBuilder<T, M, U> withEntitlementProvider(BiFunction<net.dv8tion.jda.api.entities.User, Guild, List<SKU>> entitlementProvider) {
        this.entitlementProvider = entitlementProvider;
        return this;
    }

    /**
     * Build the command hub.
     * <p>
     * This will register the command hub as a listener.
     * <p>
     * This will also register slash commands, if slash commands are active.
     *
     * @return command hub instance
     */
    public InteractionHub<T, M, U> build() {
        ConversationService conversationService = null;
        if (withConversations) {
            conversationService = new ConversationService(localizer);
            shardManager.addEventListener(conversationService);
        }
        MenuService buttons = null;
        if (menuService != null) {
            menuService.withLocalizer(localizer);
            buttons = menuService.build();
        }
        PageService pages = null;
        if (pagination != null) {
            pagination.withLocalizer(localizer);
            pages = pagination.build();
        }
        ModalService modals = null;
        if (modalService != null) {
            modalService.withLocalizer(localizer);
            modals = modalService.build();
        }
        var commandListener = new InteractionHub<>(shardManager, commands, messages, users, conversationService, localizer,
                commandErrorHandler, buttons, pages, modals, postCommandHook, guildCommandMapper, cleanGuildCommands,
                testMode, premiumErrorMessage, skuConfiguration, entitlementProvider);
        shardManager.addEventListener(commandListener);
        commandListener.updateCommands();
        return commandListener;
    }
}
