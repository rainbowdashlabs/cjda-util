/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command.dispatching;

import de.chojo.jdautil.buttons.ButtonService;
import de.chojo.jdautil.command.CommandMeta;
import de.chojo.jdautil.command.SimpleCommand;
import de.chojo.jdautil.conversation.ConversationService;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.localization.util.Language;
import de.chojo.jdautil.pagination.PageService;
import de.chojo.jdautil.util.SlashCommandUtil;
import de.chojo.jdautil.wrapper.SlashCommandContext;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

import static org.slf4j.LoggerFactory.getLogger;

public class CommandHub<Command extends SimpleCommand> extends ListenerAdapter {
    private static final Logger log = getLogger(CommandHub.class);
    private final ShardManager shardManager;
    private final Map<String, Command> commands;
    private final PermissionCheck<CommandMeta> permissionCheck;
    private final ConversationService conversationService;
    private final ILocalizer localizer;
    private final boolean useSlashGlobalCommands;
    private final BiConsumer<CommandExecutionContext<Command>, Throwable> commandErrorHandler;
    private final Map<Language, List<CommandData>> commandData = new HashMap<>();
    private final ButtonService buttons;
    private final PageService pages;

    public CommandHub(ShardManager shardManager,
                      Map<String, Command> commands, PermissionCheck<CommandMeta> permissionCheck,
                      ConversationService conversationService, ILocalizer localizer,
                      boolean useSlashGlobalCommands, BiConsumer<CommandExecutionContext<Command>, Throwable> commandErrorHandler,
                      ButtonService buttons, PageService pages) {
        this.shardManager = shardManager;
        this.commands = commands;
        this.permissionCheck = permissionCheck;
        this.conversationService = conversationService;
        this.localizer = localizer;
        this.useSlashGlobalCommands = useSlashGlobalCommands;
        this.commandErrorHandler = commandErrorHandler;
        this.buttons = buttons;
        this.pages = pages;
    }

    public static <T extends SimpleCommand> CommandHubBuilder<T> builder(ShardManager shardManager) {
        return new CommandHubBuilder<>(shardManager);
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        var name = event.getName();
        var command = getCommand(name).get();
        if (!canExecute(event, command)) {
            event.replyChoices().queue();
            return;
        }
        try {
            command.onAutoComplete(event, new SlashCommandContext(null, conversationService, localizer.getContextLocalizer(event.getGuild()), buttons, pages, this));
        } catch (Throwable t) {
            var executionContext = new CommandExecutionContext<>(command, SlashCommandUtil.commandAsString(event), event.getGuild(), event.getMessageChannel());
            commandErrorHandler.accept(executionContext, t);
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        var name = event.getName();
        var command = getCommand(name).get();
        if (!canExecute(event, command)) {
            event.reply("🚫").setEphemeral(true).queue();
            return;
        }
        try {
            command.onSlashCommand(event, new SlashCommandContext(event, conversationService, localizer.getContextLocalizer(event.getGuild()), buttons, pages, this));
        } catch (Throwable t) {
            var executionContext = new CommandExecutionContext<>(command, SlashCommandUtil.commandAsString(event), event.getGuild(), event.getChannel());
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
        for (var language : localizer.languages()) {
            log.info("Creating command data for {} language", language.getCode());
            List<CommandData> localizedCommandData = new ArrayList<>();
            for (var command : new HashSet<>(commands.values())) {
                try {
                    localizedCommandData.add(command.getCommandData(localizer, language));
                } catch (Exception e) {
                    throw new IllegalStateException("Bot command deployment failed in command " + command.meta().name() + " for language " + language.getCode(), e);
                }
            }
            commandData.put(language, localizedCommandData);
        }
        for (var command : new HashSet<>(commands.values())) {
            if (command.meta().subCommands() != null) {
                log.info("Registering command {} with {} subcommands", command.meta().name(), command.meta().subCommands().length);
            } else {
                log.info("Registering command {}.", command.meta().name());
            }
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
            baseShard.updateCommands().addCommands(commandData.get(localizer.defaultLanguage())).complete();
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

    public void refreshGuildCommands(Guild guild) {
        var language = localizer.getGuildLocale(guild);
        guild.updateCommands().addCommands(commandData.get(language)).queue(suc -> {
            log.info("Updated {} slash commands for guild {}({})", suc.size(), guild.getName(), guild.getId());
        }, err -> {
            if (err instanceof ErrorResponseException) {
                var response = (ErrorResponseException) err;
                if (response.getErrorResponse() == ErrorResponse.MISSING_ACCESS) {
                    log.debug("Missing slash command access on guild {}({})", guild.getName(), guild.getId());
                    return;
                }
            }
            log.error("Could not update guild commands", err);
        });
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        if (conversationService != null) {
            conversationService.invoke(event);
        }
    }


    public boolean canExecute(GenericInteractionCreateEvent event, Command command) {
        return command.meta().defaultEnabled() || command.meta().hasPermission(event, permissionCheck);
    }

    public Optional<Command> getCommand(String name) {
        return Optional.ofNullable(commands.get(name.toLowerCase()));
    }

    public Set<Command> getCommands() {
        return Set.copyOf(commands.values());
    }
}
