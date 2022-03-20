/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command.dispatching;

import de.chojo.jdautil.buttons.ButtonService;
import de.chojo.jdautil.buttons.ButtonServiceBuilder;
import de.chojo.jdautil.command.CommandMeta;
import de.chojo.jdautil.command.SimpleCommand;
import de.chojo.jdautil.conversation.ConversationService;
import de.chojo.jdautil.localization.ContextLocalizer;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.pagination.PageService;
import de.chojo.jdautil.pagination.PageServiceBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Collections;
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
    private boolean withConversations;
    private boolean useSlashGlobalCommands = true;
    private BiConsumer<CommandExecutionContext<T>, Throwable> commandErrorHandler =
            (context, err) -> log.error("An unhandled exception occured while executing command {}: {}", context.command(), context.args(), err);
    private ManagerRoles managerRoles = guild -> Collections.emptyList();
    private PageServiceBuilder pagination;
    private ButtonServiceBuilder buttonService;

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
     * Adds a manager role supplier which provides the manager roles for a guild.
     *
     * @param managerRoles handler for errors
     * @return builder instance
     */
    public CommandHubBuilder<T> withManagerRole(ManagerRoles managerRoles) {
        this.managerRoles = managerRoles;
        return this;
    }

    public CommandHubBuilder<T> withPagination(Consumer<PageServiceBuilder> builder) {
        pagination = PageService.builder(shardManager);
        builder.accept(pagination);
        return this;
    }

    public CommandHubBuilder<T> withButtonService(Consumer<ButtonServiceBuilder> builder) {
        buttonService = ButtonService.builder();
        builder.accept(buttonService);
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
        ButtonService buttons = null;
        if (buttonService != null) {
            buttons = buttonService.setLocalizer(localizer).build();
            shardManager.addEventListener(buttons);
        }
        PageService pages = null;
        if (pagination != null) {
            pages = pagination.localizer(localizer).build();
            shardManager.addEventListener(pages);
        }
        var commandListener = new CommandHub<>(shardManager, commands, permissionCheck, conversationService, localizer,
                useSlashGlobalCommands, commandErrorHandler, managerRoles, buttons, pages);
        shardManager.addEventListener(commandListener);
        commandListener.updateCommands();
        return commandListener;
    }
}
