/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command.dispatching;

import de.chojo.jdautil.menus.MenuService;
import de.chojo.jdautil.menus.MenuServiceBuilder;
import de.chojo.jdautil.menus.MenuServiceModifier;
import de.chojo.jdautil.command.CommandMeta;
import de.chojo.jdautil.command.SimpleCommand;
import de.chojo.jdautil.conversation.ConversationService;
import de.chojo.jdautil.localization.ContextLocalizer;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.modals.service.ModalService;
import de.chojo.jdautil.modals.service.ModalServiceBuilder;
import de.chojo.jdautil.modals.service.ModalServiceModifier;
import de.chojo.jdautil.pagination.PageService;
import de.chojo.jdautil.pagination.PageServiceBuilder;
import de.chojo.jdautil.pagination.PageServiceModifier;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.slf4j.LoggerFactory.getLogger;

public class CommandHubBuilder<T extends SimpleCommand> {
    private static final Logger log = getLogger(CommandHubBuilder.class);
    private final ShardManager shardManager;
    private final Map<String, T> commands = new HashMap<>();
    @NotNull
    private ILocalizer localizer = ILocalizer.DEFAULT;
    private PermissionCheck<CommandMeta> permissionCheck = (eventWrapper, command) -> {
        if (eventWrapper.isFromGuild()) {
            if (command.defaultEnabled()) {
                return true;
            }
            return eventWrapper.getMember().hasPermission(Permission.ADMINISTRATOR);
        }
        return true;
    };

    private Consumer<CommandResult<T>> postCommandHook = r -> {
    };
    private boolean withConversations;
    private boolean useSlashGlobalCommands = true;
    @Deprecated
    private BiConsumer<CommandExecutionContext<T>, Throwable> commandErrorHandler =
            (context, err) -> log.error("An unhandled exception occured while executing command {}: {}", context.command(), context.args(), err);
    private PageServiceBuilder pagination;
    private MenuServiceBuilder menuService;
    private ModalServiceBuilder modalService;

    CommandHubBuilder(ShardManager shardManager) {
        this.shardManager = shardManager;
    }

    /**
     * This will make slash commands only available on these guilds
     *
     * @return builder instance
     */
    public CommandHubBuilder<T> useGuildCommands() {
        useSlashGlobalCommands = false;
        return this;
    }

    /**
     * Adds a localizer to the command hub. This will allow to use {@link ContextLocalizer}.
     *
     * @param localizer localizer instance
     * @return builder instance
     */
    public CommandHubBuilder<T> withLocalizer(ILocalizer localizer) {
        this.localizer = localizer;
        return this;
    }

    /**
     * Register commands
     *
     * @param commands commands to register
     * @return builder instance
     */
    @SafeVarargs
    public final CommandHubBuilder<T> withCommands(T... commands) {
        for (var command : commands) {
            this.commands.put(command.meta().name().toLowerCase(Locale.ROOT), command);
        }
        return this;
    }

    /**
     * Adds a permission check. This check determines if a user is allowed to execute a command.
     *
     * @param permissionCheck checks if a user can execute the command
     * @return builder instance
     */
    public CommandHubBuilder<T> withPermissionCheck(PermissionCheck<CommandMeta> permissionCheck) {
        this.permissionCheck = permissionCheck;
        return this;
    }

    /**
     * Adds a conversation system to the command hub
     *
     * @return builder instance
     */
    public CommandHubBuilder<T> withConversationSystem() {
        this.withConversations = true;
        return this;
    }

    /**
     * Adds a command error handler to the hub, which handles uncatched exceptions. Used for loggin.
     *
     * @param commandErrorHandler handler for errors
     * @return builder instance
     */
    public CommandHubBuilder<T> withCommandErrorHandler(BiConsumer<CommandExecutionContext<T>, Throwable> commandErrorHandler) {
        this.commandErrorHandler = commandErrorHandler;
        return this;
    }

    /**
     * Adds a post command hook to the hub, which gets executed after every successful or unsuccessful command execution.
     *
     * @param postCommandHook handler
     * @return builder instance
     */
    public CommandHubBuilder<T> withPostCommandHook(Consumer<CommandResult<T>> postCommandHook) {
        this.postCommandHook = postCommandHook;
        return this;
    }

    public CommandHubBuilder<T> withPagination(Consumer<PageServiceModifier> builder) {
        pagination = PageService.builder(shardManager);
        builder.accept(pagination);
        return this;
    }

    public CommandHubBuilder<T> withMenuService(Consumer<MenuServiceModifier> builder) {
        menuService = MenuService.builder(shardManager);
        builder.accept(menuService);
        return this;
    }

    public CommandHubBuilder<T> withModalService(Consumer<ModalServiceModifier> builder) {
        modalService = ModalService.builder(shardManager);
        builder.accept(modalService);
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
    public CommandHub<T> build() {
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
        var commandListener = new CommandHub<>(shardManager, commands, permissionCheck, conversationService, localizer,
                useSlashGlobalCommands, commandErrorHandler, buttons, pages, modals, postCommandHook);
        shardManager.addEventListener(commandListener);
        commandListener.updateCommands();
        return commandListener;
    }
}
