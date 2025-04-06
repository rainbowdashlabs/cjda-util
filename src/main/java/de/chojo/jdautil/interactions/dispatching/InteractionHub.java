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
import de.chojo.jdautil.interactions.base.SKUConfiguration;
import de.chojo.jdautil.interactions.message.Message;
import de.chojo.jdautil.interactions.slash.Slash;
import de.chojo.jdautil.interactions.user.User;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.menus.MenuService;
import de.chojo.jdautil.modals.service.ModalService;
import de.chojo.jdautil.pagination.PageService;
import de.chojo.jdautil.util.Guilds;
import de.chojo.jdautil.util.Premium;
import de.chojo.jdautil.util.SlashCommandUtil;
import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
    private final boolean cleanGuildCommands;
    private final boolean testMode;
    private final String premiumErrorMessage;
    private final SKUConfiguration skuConfiguration;

    public InteractionHub(ShardManager shardManager,
                          Map<String, C> slash, Map<String, M> messages, Map<String, U> users,
                          ConversationService conversationService, ILocalizer localizer,
                          BiConsumer<InteractionContext, Throwable> commandErrorHandler,
                          MenuService buttons, PageService pages, ModalService modalService, Consumer<InteractionResult<C>> postCommandHook,
                          Function<InteractionMeta, List<Long>> guildCommandMapper, boolean cleanGuildCommands, boolean testMode, String premiumErrorMessage, SKUConfiguration skuConfiguration) {
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
        this.cleanGuildCommands = cleanGuildCommands;
        this.testMode = testMode;
        this.premiumErrorMessage = premiumErrorMessage;
        this.skuConfiguration = skuConfiguration;
    }


    public static <T extends Slash, M extends Message, U extends User> InteractionHubBuilder<T, M, U> builder(ShardManager shardManager) {
        return new InteractionHubBuilder<>(shardManager);
    }

    @SubscribeEvent
    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        var name = event.getName();
        var message = getMessage(name).get();
        var executionContext = new InteractionContext(message, SlashCommandUtil.commandAsString(event), event.getGuild(), event.getChannel());
        try {
            EventContext context = new EventContext(event, conversationService, localizer, buttons, pages, modalService, this);
            if (!skuConfiguration.isEntitled(event)) {
                Premium.replyPremium(event, context, skuConfiguration.messages(event.getFullCommandName()));
                return;
            }

            message.onMessage(event, context);
        } catch (Throwable t) {
            commandErrorHandler.accept(executionContext, t);
            return;
        }
        postCommandHook.accept(InteractionResult.success(event, executionContext));
    }


    @SubscribeEvent
    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        var name = event.getName();
        var user = getUser(name).get();
        var executionContext = new InteractionContext(user, SlashCommandUtil.commandAsString(event), event.getGuild(), event.getMessageChannel());
        try {
            EventContext context = new EventContext(event, conversationService, localizer, buttons, pages, modalService, this);
            if (!skuConfiguration.isEntitled(event)) {
                Premium.replyPremium(event, context, skuConfiguration.users(event.getFullCommandName()));
                return;
            }
            user.onUser(event, context);
        } catch (Throwable t) {
            commandErrorHandler.accept(executionContext, t);
            return;
        }
        postCommandHook.accept(InteractionResult.success(event, executionContext));
    }


    @SubscribeEvent
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        var name = event.getName();
        var command = getSlash(name).get();
        var executionContext = new InteractionContext(command, SlashCommandUtil.commandAsString(event), event.getGuild(), event.getChannel());
        try {
            EventContext context = new EventContext(event, conversationService, localizer, buttons, pages, modalService, this);
            if (!skuConfiguration.isEntitled(event)) {
                Premium.replyPremium(event, context, skuConfiguration.commands(event.getFullCommandName()));
                return;
            }
            command.onSlashCommand(event, context);
        } catch (Throwable t) {
            commandErrorHandler.accept(executionContext, t);
            return;
        }
        postCommandHook.accept(InteractionResult.success(event, executionContext));
    }

    @SubscribeEvent
    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        var name = event.getName();
        var command = getSlash(name).get();
        try {
            EventContext context = new EventContext(null, conversationService, localizer, buttons, pages, modalService, this);
            if (!skuConfiguration.isEntitled(event)) {
                event.replyChoices().queue();
                return;
            }
            command.onAutoComplete(event, context);
        } catch (Throwable t) {
            var executionContext = new InteractionContext(command, SlashCommandUtil.commandAsString(event), event.getGuild(), event.getMessageChannel());
            commandErrorHandler.accept(executionContext, t);
        }
    }

    @SubscribeEvent
    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        refreshGuildCommands(event.getGuild());
    }

    public String premiumErrorMessage() {
        return premiumErrorMessage;
    }

    private void buildGuildData(Collection<? extends CommandDataProvider> interactions, Map<Long, List<CommandData>> guildCommands) {
        for (var command : interactions) {
            try {
                if (command.meta().scope() == InteractionScope.PRIVATE) {
                    var data = command.toCommandData(localizer);
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
                if (command.meta().scope() == InteractionScope.GLOBAL) {
                    var data = command.toCommandData(localizer);
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

        // Wait for shards to be ready
        for (var shard : shardManager.getShards()) {
            try {
                shard.awaitReady();
                log.info("Shard {} is ready.", shard.getShardInfo().getShardId());
            } catch (InterruptedException e) {
                // ignore
            }
        }

        for (var command : global) {
            log.info("Registering command {}.", command.getName());
        }

        if (testMode) {
            // All commands with global scope will be published directly on the guilds
            // Commands which are on private scope will still only be published on the defined guilds.
            log.warn("Interactions hub is running in test mode. All interactions will be published on guilds and not globally");
            for (var guild : shardManager.getGuilds()) {
                var deploy = global;
                if (guildCommands.containsKey(guild.getIdLong())) {
                    log.debug("Adding {} private commands on {}", guildCommands.get(guild.getIdLong()).size(), Guilds.prettyName(guild));
                    deploy = new ArrayList<>(deploy);
                    deploy.addAll(guildCommands.get(guild.getIdLong()));
                }
                log.debug("Published {} commands on {}.", deploy.size(), Guilds.prettyName(guild));
                guild.updateCommands().addCommands(deploy).queue();
            }
            log.debug("Global slash commands were unregistered.");
            shardManager.getShards().get(0).updateCommands().queue();
            return;
        }

        var baseShard = shardManager.getShards().get(0);
        log.info("Updating {} global commands.", global.size());
        baseShard.updateCommands().addCommands(global).queue(RestAction.getDefaultSuccess(), err -> {
            if (err instanceof ErrorResponseException e && e.getErrorCode() == 50035) {
                for (var error : e.getSchemaErrors()) {
                    var loc = error.getLocation().split("\\.", 2);
                    var commandIndex = Integer.parseInt(loc[0]);
                    var command = global.get(commandIndex);
                    log.error("Invalid values in command {} at {}. Caused by:\n{}", command.getName(), loc[1], error.getErrors());
                }
                return;
            }
            log.error("Could not deploy commands", err);
        });

        if (cleanGuildCommands) {
            log.warn("Clean guild command deployment requested. Updating commands for all guilds.");
            for (var guild : shardManager.getGuilds()) {
                var cmds = guildCommands.getOrDefault(guild.getIdLong(), Collections.emptyList());
                log.debug("Cleaning and updating {} guild commands for {}.", cmds.size(), Guilds.prettyName(guild));
                guild.updateCommands().addCommands(cmds).queue();
            }
        } else {
            for (var entry : guildCommands.entrySet()) {
                var guild = shardManager.getGuildById(entry.getKey());
                if (guild == null) {
                    log.warn("Guild {} not found. Can't deploy commands.", entry.getKey());
                    continue;
                }
                log.debug("Updating {} guild commands for {}.", entry.getValue().size(), Guilds.prettyName(guild));
                guild.updateCommands().addCommands(entry.getValue()).queue();
            }
        }
    }

    public void refreshGuildCommands(Guild guild) {
        var guildData = getGuildData();
        if (!guildData.containsKey(guild.getIdLong())) {
            guild.updateCommands().queue();
            return;
        }
        guild.updateCommands().addCommands(guildData.get(guild.getIdLong())).queue();
    }

    @SubscribeEvent
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

    public Optional<U> getUser(String name) {
        return Optional.ofNullable(users.get(name.toLowerCase()));
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

    public MenuService buttonService() {
        return buttons;
    }

    public PageService pageServices() {
        return pages;
    }

    public ModalService modalService() {
        return modalService;
    }

    public ConversationService conversationService() {
        return conversationService;
    }
}
