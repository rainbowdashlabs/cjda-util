/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.dispatching;

import de.chojo.jdautil.conversation.ConversationService;
import de.chojo.jdautil.interactions.base.CommandDataProvider;
import de.chojo.jdautil.interactions.base.InteractionMeta;
import de.chojo.jdautil.interactions.message.Message;
import de.chojo.jdautil.interactions.premium.SKU;
import de.chojo.jdautil.interactions.premium.SKUConfiguration;
import de.chojo.jdautil.interactions.slash.Slash;
import de.chojo.jdautil.interactions.user.User;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.menus.MenuService;
import de.chojo.jdautil.modals.service.ModalService;
import de.chojo.jdautil.pagination.PageService;
import de.chojo.jdautil.util.Premium;
import de.chojo.jdautil.util.SlashCommandUtil;
import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static de.chojo.jdautil.interactions.dispatching.InteractionResult.failed;
import static de.chojo.jdautil.interactions.dispatching.InteractionResult.success;
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
    private final Consumer<InteractionResult> postCommandHook;
    private final String premiumErrorMessage;
    private final SKUConfiguration skuConfiguration;
    private final BiFunction<net.dv8tion.jda.api.entities.User, Guild, List<SKU>> entitlementProvider;
    private final CommandDeployer deployer;

    public InteractionHub(ShardManager shardManager,
                          Map<String, C> slash, Map<String, M> messages, Map<String, U> users,
                          ConversationService conversationService, ILocalizer localizer,
                          BiConsumer<InteractionContext, Throwable> commandErrorHandler,
                          MenuService buttons, PageService pages, ModalService modalService, Consumer<InteractionResult> postCommandHook,
                          Function<InteractionMeta, List<Long>> guildCommandMapper,
                          Function<InteractionMeta, Map<Long, String>> aliasMapper,
                          boolean cleanGuildCommands, boolean testMode, String premiumErrorMessage,
                          SKUConfiguration skuConfiguration,
                          BiFunction<net.dv8tion.jda.api.entities.User, Guild, List<SKU>> entitlementProvider) {
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
        this.premiumErrorMessage = premiumErrorMessage;
        this.skuConfiguration = skuConfiguration;
        this.entitlementProvider = entitlementProvider;
        var contextInteractions = new ArrayList<CommandDataProvider>(messages.values());
        contextInteractions.addAll(users.values());
        this.deployer = new CommandDeployer(shardManager, localizer, Set.copyOf(slash.values()), Set.copyOf(contextInteractions),
                guildCommandMapper, aliasMapper, cleanGuildCommands, testMode);
    }


    public static <T extends Slash, M extends Message, U extends User> InteractionHubBuilder<T, M, U> builder(ShardManager shardManager) {
        return new InteractionHubBuilder<>(shardManager);
    }

    private EventContext buildContext(GenericInteractionCreateEvent event) {
        if (event instanceof IReplyCallback callback) {
            return new EventContext(callback, conversationService, localizer, buttons, pages, modalService, this, entitlementProvider.apply(event.getUser(), event.getGuild()));
        }
        return new EventContext(null, conversationService, localizer, buttons, pages, modalService, this, entitlementProvider.apply(event.getUser(), event.getGuild()));
    }

    @SubscribeEvent
    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        var name = event.getName();
        var message = getMessage(name).orElseThrow(() -> {
            event.reply("Unknown Interaction.").setEphemeral(true).complete();
            return new UnknownInteractionException(event.getGuild(), event.getUser(), event.getChannel(), SlashCommandUtil.commandAsString(event));
        });

        var executionContext = new InteractionContext(message, SlashCommandUtil.commandAsString(event), event.getGuild(), event.getChannel(), event.getUser());
        try {
            EventContext context = buildContext(event);
            if (!skuConfiguration.isEntitled(event)) {
                Premium.replyPremium(context, skuConfiguration.messages(event.getFullCommandName()));
                return;
            }

            message.onMessage(event, context);
        } catch (Throwable t) {
            postCommandHook.accept(failed(event, executionContext, t));
            commandErrorHandler.accept(executionContext, t);
            return;
        }
        postCommandHook.accept(success(event, executionContext));
    }


    @SubscribeEvent
    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        var name = event.getName();
        var user = getUser(name).orElseThrow(() -> {
            event.reply("Unknown Interaction.").setEphemeral(true).complete();
            return new UnknownInteractionException(event.getGuild(), event.getUser(), event.getChannel(), SlashCommandUtil.commandAsString(event));
        });
        var executionContext = new InteractionContext(user, SlashCommandUtil.commandAsString(event), event.getGuild(), event.getMessageChannel(), event.getUser());
        try {
            EventContext context = buildContext(event);
            if (!skuConfiguration.isEntitled(event)) {
                Premium.replyPremium(context, skuConfiguration.users(event.getFullCommandName()));
                return;
            }
            user.onUser(event, context);
        } catch (Throwable t) {
            commandErrorHandler.accept(executionContext, t);
            postCommandHook.accept(failed(event, executionContext, t));
            return;
        }
        postCommandHook.accept(success(event, executionContext));
    }


    @SubscribeEvent
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        var command = resolveSlash(event).orElseThrow(() -> {
            event.reply("Unknown Interaction.").setEphemeral(true).complete();
            return new UnknownInteractionException(event.getGuild(), event.getUser(), event.getChannel(), SlashCommandUtil.commandAsString(event));
        });
        var executionContext = new InteractionContext(command, SlashCommandUtil.commandAsString(event), event.getGuild(), event.getChannel(), event.getUser());
        try {
            EventContext context = buildContext(event);
            var fullCommandName = canonicalFullCommandName(event, command);
            if (!skuConfiguration.isCommandEntitled(event, fullCommandName)) {
                Premium.replyPremium(context, skuConfiguration.commands(fullCommandName));
                return;
            }
            command.onSlashCommand(event, context);
        } catch (Throwable t) {
            commandErrorHandler.accept(executionContext, t);
            postCommandHook.accept(failed(event, executionContext, t));
            return;
        }
        postCommandHook.accept(success(event, executionContext));
    }

    @SubscribeEvent
    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        var optCommand = resolveSlash(event);
        if (optCommand.isEmpty()) {
            throw new UnknownInteractionException(event.getGuild(), event.getUser(), event.getChannel(), SlashCommandUtil.commandAsString(event));
        }
        var command = optCommand.get();
        try {
            EventContext context = buildContext(event);
            if (!skuConfiguration.isCommandEntitled(event, canonicalFullCommandName(event, command))) {
                event.replyChoices().complete();
                return;
            }
            command.onAutoComplete(event, context);
        } catch (Throwable t) {
            var executionContext = new InteractionContext(command, SlashCommandUtil.commandAsString(event), event.getGuild(), event.getMessageChannel(), event.getUser());
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

    /**
     * Deploys all commands on a separate thread. Interactions are dispatched while the deployment is running.
     *
     * @return future completing once every command is deployed
     */
    CompletableFuture<Void> updateCommands() {
        return deployer.start();
    }

    /**
     * Future of the command deployment started when the hub was built.
     *
     * @return future completing once every command is deployed
     */
    public CompletableFuture<Void> deployment() {
        return deployer.deployment();
    }

    /**
     * Rebuilds and deploys the guild commands of a guild, including its aliases.
     * <p>
     * Blocks until discord accepted the commands. Only waits for a deployment running on the same guild.
     *
     * @param guild guild to refresh
     */
    public void refreshGuildCommands(Guild guild) {
        deployer.refresh(guild);
    }

    @SubscribeEvent
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        if (conversationService != null) {
            conversationService.invoke(event);
        }
    }

    /**
     * Resolves the command of an interaction, either by its own name or by an alias of the guild it was used on.
     */
    private Optional<C> resolveSlash(CommandInteractionPayload event) {
        var command = getSlash(event.getName());
        if (command.isPresent() || event.getGuild() == null) return command;
        return deployer.canonicalName(event.getGuild().getIdLong(), event.getName()).flatMap(this::getSlash);
    }

    /**
     * The full command name with the alias, if one was used, replaced by the name of the command.
     */
    private static String canonicalFullCommandName(CommandInteractionPayload event, Slash command) {
        return command.meta().name() + event.getFullCommandName().substring(event.getName().length());
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
