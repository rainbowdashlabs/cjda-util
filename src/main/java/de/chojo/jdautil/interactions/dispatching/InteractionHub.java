/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.dispatching;

import de.chojo.jdautil.conversation.ConversationService;
import de.chojo.jdautil.interactions.base.CommandDataProvider;
import de.chojo.jdautil.interactions.base.InteractionMeta;
import de.chojo.jdautil.interactions.base.InteractionScope;
import de.chojo.jdautil.interactions.message.Message;
import de.chojo.jdautil.interactions.slash.structure.Slash;
import de.chojo.jdautil.interactions.user.User;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.menus.MenuService;
import de.chojo.jdautil.modals.service.ModalService;
import de.chojo.jdautil.pagination.PageService;
import de.chojo.jdautil.util.SlashCommandUtil;
import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.slf4j.LoggerFactory.getLogger;

public class InteractionHub<C extends Slash, M extends Message, U extends User> extends ListenerAdapter {
    private static final Logger log = getLogger(InteractionHub.class);
    private final ShardManager shardManager;
    private final Map<String, C> slash;
    private final Map<String, M> messages;
    private final Map<String, U> users;
    private final ConversationService conversationService;
    private final ILocalizer localizer;
    @Deprecated
    private final BiConsumer<InteractionContext, Throwable> commandErrorHandler;
    private final MenuService buttons;
    private final PageService pages;
    private final ModalService modalService;
    private final Consumer<InteractionResult<C>> postCommandHook;
    private final Function<InteractionMeta, List<Long>> guildCommandMapper;

    public InteractionHub(ShardManager shardManager,
                          Map<String, C> slash, Map<String, M> messages, Map<String, U> users,
                          ConversationService conversationService, ILocalizer localizer,
                          BiConsumer<InteractionContext, Throwable> commandErrorHandler,
                          MenuService buttons, PageService pages, ModalService modalService, Consumer<InteractionResult<C>> postCommandHook, Function<InteractionMeta, List<Long>> guildCommandMapper) {
        this.shardManager = shardManager;
        this.slash = slash;
        this.messages = messages;
        this.users = users;
        this.conversationService = conversationService;
        this.localizer = localizer;
        this.commandErrorHandler = commandErrorHandler;
        this.buttons = buttons;
        this.pages = pages;
        this.modalService = modalService;
        this.postCommandHook = postCommandHook;
        this.guildCommandMapper = guildCommandMapper;
    }


    public static <T extends Slash, M extends Message, U extends User> InteractionHubBuilder<T, M, U> builder(ShardManager shardManager) {
        return new InteractionHubBuilder<>(shardManager);
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
        postCommandHook.accept(InteractionResult.success(event, executionContext));
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
        postCommandHook.accept(InteractionResult.success(event, executionContext));
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
        refreshGuildCommands(event.getGuild());
    }

    private void buildGuildData(Collection<? extends CommandDataProvider> interactions, Map<Long, List<CommandData>> guildCommands) {
        for (var command : interactions) {
            try {
                var data = command.toCommandData(localizer);
                if (command.meta().scope() == InteractionScope.PRIVATE) {
                    for (var guildId : guildCommandMapper.apply(command.meta())) {
                        guildCommands.computeIfAbsent(guildId, k -> new ArrayList<>()).add(data);
                    }
                }
            } catch (Exception e) {
                throw new IllegalStateException("Bot command deployment failed in command " + command.meta().name(), e);
            }
        }
    }

    private void buildGlobalData(Collection<? extends CommandDataProvider> interactions, List<CommandData> global) {
        for (var command : interactions) {
            try {
                var data = command.toCommandData(localizer);
                if (command.meta().scope() == InteractionScope.GLOBAL) {
                    global.add(data);
                }
            } catch (Exception e) {
                throw new IllegalStateException("Bot command deployment failed in command " + command.meta().name(), e);
            }
        }
    }

    private List<CommandData> getGlobalData() {
        List<CommandData> global = new ArrayList<>();
        buildGlobalData(getSlash(), global);
        buildGlobalData(getMessage(), global);
        buildGlobalData(getUser(), global);
        return global;
    }

    private Map<Long, List<CommandData>> getGuildData() {
        Map<Long, List<CommandData>> guildCommands = new HashMap<>();
        buildGuildData(getSlash(), guildCommands);
        buildGuildData(getMessage(), guildCommands);
        buildGuildData(getUser(), guildCommands);
        return guildCommands;
    }


    void updateCommands() {
        log.info("Updating slash commands.");
        var guildCommands = getGuildData();
        var global = getGlobalData();

        for (var command : global) {
            log.info("Registering command {}.", command.getName());
        }

        if (!global.isEmpty()) {
            var baseShard = shardManager.getShards().get(0);
            try {
                baseShard.awaitReady();
            } catch (InterruptedException e) {
                // ignore
            }

            log.info("Updating global slash commands.");
            baseShard.updateCommands().addCommands(global).complete();
            return;
        }

        for (JDA shard : shardManager.getShards()) {
            try {
                shard.awaitReady();
                log.info("Shard {} is ready.", shard.getShardInfo().getShardId());
            } catch (InterruptedException e) {
                // ignore
            }
        }

        for (var entry : guildCommands.entrySet()) {
            var guild = shardManager.getGuildById(entry.getKey());
            if (guild == null) {
                log.warn("Guild {} not found. Can't deploy commands.", entry.getKey());
                continue;
            }
            guild.updateCommands().addCommands(entry.getValue()).queue();
        }
    }

    @Deprecated(forRemoval = true)
    public void refreshGuildCommands(Guild guild) {
        var guildData = getGuildData();
        if (!guildData.containsKey(guild.getIdLong())) return;
        guild.updateCommands().addCommands(guildData.get(guild.getIdLong())).queue();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        if (conversationService != null) {
            conversationService.invoke(event);
        }
    }

    public Optional<C> getSlash(String name) {
        return Optional.ofNullable(slash.get(name.toLowerCase()));
    }

    public Optional<M> getMessage(String name) {
        return Optional.ofNullable(messages.get(name.toLowerCase()));
    }

    public Collection<C> getSlash() {
        return Set.copyOf(slash.values());
    }

    public Collection<U> getUser() {
        return Set.copyOf(users.values());
    }

    public Collection<M> getMessage() {
        return Set.copyOf(messages.values());
    }
}
