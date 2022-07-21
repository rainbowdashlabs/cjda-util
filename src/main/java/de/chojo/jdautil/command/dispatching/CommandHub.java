/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command.dispatching;

import de.chojo.jdautil.command.message.Message;
import de.chojo.jdautil.command.slash.structure.Command;
import de.chojo.jdautil.conversation.ConversationService;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.menus.MenuService;
import de.chojo.jdautil.modals.service.ModalService;
import de.chojo.jdautil.pagination.PageService;
import de.chojo.jdautil.util.Guilds;
import de.chojo.jdautil.util.SlashCommandUtil;
import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.slf4j.LoggerFactory.getLogger;

public class CommandHub<SlashImpl extends Command, MessageImpl extends Message> extends ListenerAdapter {
    private static final Logger log = getLogger(CommandHub.class);
    private final ShardManager shardManager;
    private final Map<String, SlashImpl> slash;
    private final Map<String, MessageImpl> messages;
    private final ConversationService conversationService;
    private final ILocalizer localizer;
    private final boolean useSlashGlobalCommands;
    @Deprecated
    private final BiConsumer<InteractionContext, Throwable> commandErrorHandler;
    private final MenuService buttons;
    private final PageService pages;
    private final ModalService modalService;
    private final Consumer<CommandResult<SlashImpl>> postCommandHook;

    public CommandHub(ShardManager shardManager,
                      Map<String, SlashImpl> slash, Map<String, MessageImpl> messages,
                      ConversationService conversationService, ILocalizer localizer,
                      boolean useSlashGlobalCommands, BiConsumer<InteractionContext, Throwable> commandErrorHandler,
                      MenuService buttons, PageService pages, ModalService modalService, Consumer<CommandResult<SlashImpl>> postCommandHook) {
        this.shardManager = shardManager;
        this.slash = slash;
        this.messages = messages;
        this.conversationService = conversationService;
        this.localizer = localizer;
        this.useSlashGlobalCommands = useSlashGlobalCommands;
        this.commandErrorHandler = commandErrorHandler;
        this.buttons = buttons;
        this.pages = pages;
        this.modalService = modalService;
        this.postCommandHook = postCommandHook;
    }


    public static <T extends Command, M extends Message> CommandHubBuilder<T, M> builder(ShardManager shardManager) {
        return new CommandHubBuilder<>(shardManager);
    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        var name = event.getName();
        var message = getMessage(name).get();
        var executionContext = new InteractionContext(message, SlashCommandUtil.commandAsString(event), event.getGuild(), event.getChannel());
        try {
            message.onMessage(event, new EventContext(event, conversationService, localizer.getContextLocalizer(event.getGuild()), buttons, pages, modalService, this));
        } catch (Throwable t) {
            commandErrorHandler.accept(executionContext, t);
            return;
        }
        postCommandHook.accept(CommandResult.success(event, executionContext));
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        var name = event.getName();
        var command = getSlash(name).get();
        var executionContext = new InteractionContext(command, SlashCommandUtil.commandAsString(event), event.getGuild(), event.getChannel());
        try {
            command.onSlashCommand(event, new EventContext(event, conversationService, localizer.getContextLocalizer(event.getGuild()), buttons, pages, modalService, this));
        } catch (Throwable t) {
            commandErrorHandler.accept(executionContext, t);
            return;
        }
        postCommandHook.accept(CommandResult.success(event, executionContext));
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        var name = event.getName();
        var command = getSlash(name).get();
        try {
            command.onAutoComplete(event, new EventContext(null, conversationService, localizer.getContextLocalizer(event.getGuild()), buttons, pages, modalService, this));
        } catch (Throwable t) {
            var executionContext = new InteractionContext(command, SlashCommandUtil.commandAsString(event), event.getGuild(), event.getMessageChannel());
            commandErrorHandler.accept(executionContext, t);
        }
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        if (!useSlashGlobalCommands) {
            refreshGuildCommands(event.getGuild());
        }
    }

    void updateCommands() {
        log.info("Updating slash commands.");
        List<CommandData> commandData = new ArrayList<>();
        for (var command : getSlash()) {
            try {
                commandData.add(command.toCommandData(localizer));
            } catch (Exception e) {
                throw new IllegalStateException("Bot command deployment failed in command " + command.meta().name(), e);
            }
        }
        for (var message : getMessage()) {
            try {
                commandData.add(message.toCommandData(localizer));
            } catch (Exception e) {
                throw new IllegalStateException("Bot command deployment failed in command " + message.meta().name(), e);
            }
        }
        for (var command : new HashSet<>(slash.values())) {
            log.info("Registering command {}.", command.meta().name());
        }

        for (JDA shard : shardManager.getShards()) {
            try {
                shard.awaitReady();
                log.info("Shard {} is ready.", shard.getShardInfo().getShardId());
            } catch (InterruptedException e) {
                // ignore
            }
        }

        if (useSlashGlobalCommands) {
            var baseShard = shardManager.getShards().get(0);
            log.info("Removing guild commands");
            for (JDA shard : shardManager.getShards()) {
                for (Guild guild : shard.getGuilds()) {
                    guild.updateCommands().complete();
                }
            }

            log.info("Updating global slash commands.");
            baseShard.updateCommands().addCommands(commandData).complete();
            return;
        }

        var baseShard = shardManager.getShards().get(0);
        log.info("Removing global slash commands and using guild commands.");
        baseShard.updateCommands().complete();

        for (var shard : shardManager.getShards()) {
            for (var guild : shard.getGuilds()) {
                refreshGuildCommands(guild);
            }
        }
    }

    @Deprecated(forRemoval = true)
    public void refreshGuildCommands(Guild guild) {
        // decomissioned in favour of command localization.
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        if (conversationService != null) {
            conversationService.invoke(event);
        }
    }

    public Optional<SlashImpl> getSlash(String name) {
        return Optional.ofNullable(slash.get(name.toLowerCase()));
    }

    public Optional<MessageImpl> getMessage(String name) {
        return Optional.ofNullable(messages.get(name.toLowerCase()));
    }

    public Set<SlashImpl> getSlash() {
        return Set.copyOf(slash.values());
    }

    public Set<MessageImpl> getMessage() {
        return Set.copyOf(messages.values());
    }
}
