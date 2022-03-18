/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command.dispatching;

import de.chojo.jdautil.command.SimpleCommand;
import de.chojo.jdautil.conversation.ConversationService;
import de.chojo.jdautil.localization.ContextLocalizer;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.localization.Localizer;
import de.chojo.jdautil.localization.util.Language;
import de.chojo.jdautil.util.Guilds;
import de.chojo.jdautil.util.SlashCommandUtil;
import de.chojo.jdautil.wrapper.SlashCommandContext;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class CommandHub<Command extends SimpleCommand> extends ListenerAdapter {
    private static final Logger log = getLogger(CommandHub.class);
    private final ShardManager shardManager;
    private final Map<String, Command> commands;
    private final BiFunction<GenericInteractionCreateEvent, Command, Boolean> permissionCheck;
    private final ConversationService conversationService;
    private final ILocalizer localizer;
    private final boolean useSlashGlobalCommands;
    private final BiConsumer<CommandExecutionContext<Command>, Throwable> commandErrorHandler;
    private final Map<Language, List<CommandData>> commandData = new HashMap<>();
    private final Function<Guild, List<Long>> managerRoleSupplier;

    public CommandHub(ShardManager shardManager,
                      Map<String, Command> commands, BiFunction<GenericInteractionCreateEvent, Command, Boolean> permissionCheck,
                      ConversationService conversationService, ILocalizer localizer,
                      boolean useSlashGlobalCommands, BiConsumer<CommandExecutionContext<Command>, Throwable> commandErrorHandler,
                      Function<Guild, List<Long>> managerRoleSupplier) {
        this.shardManager = shardManager;
        this.commands = commands;
        this.permissionCheck = permissionCheck;
        this.conversationService = conversationService;
        this.localizer = localizer;
        this.useSlashGlobalCommands = useSlashGlobalCommands;
        this.commandErrorHandler = commandErrorHandler;
        this.managerRoleSupplier = managerRoleSupplier;
    }

    public static <T extends SimpleCommand> Builder<T> builder(ShardManager shardManager) {
        return new Builder<>(shardManager);
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
            command.onTabcomplete(event, new SlashCommandContext(conversationService));
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
            command.onSlashCommand(event, new SlashCommandContext(conversationService));
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

    private void updateCommands() {
        log.info("Updating slash commands.");
        for (var language : localizer.languages()) {
            log.info("Creating command data for {} language", language.getCode());
            List<CommandData> localizedCommandData = new ArrayList<>();
            for (var command : new HashSet<>(commands.values())) {
                localizedCommandData.add(command.getCommandData(localizer, language));
            }
            commandData.put(language, localizedCommandData);
        }
        for (var command : new HashSet<>(commands.values())) {
            if (command.subCommands() != null) {
                log.info("Registering command {} with {} subcommands", command.command(), command.subCommands().length);
            } else {
                log.info("Registering command {}.", command.command());
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
            buildGuildPriviledgesSilent(guild);
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
        return !command.needsPermission() ||  permissionCheck.apply(event, command);
    }

    public Optional<Command> getCommand(String name) {
        return Optional.ofNullable(commands.get(name.toLowerCase()));
    }

    public Set<Command> getCommands() {
        return Set.copyOf(commands.values());
    }

    /**
     * Refresh the guild command priviledges for all commands on all guilds
     */
    public void buildGuildPriviledges() {
        for (var shard : shardManager.getShards()) {
            log.debug("Refreshing command priviledges for {} guild on shard {}", shard.getGuilds().size(), shard.getShardInfo().getShardId());
            for (var guild : shard.getGuilds()) {
                buildGuildPriviledgesSilent(guild);
            }
        }
    }

    /**
     * Refresh the guild command priviledges for all commands on this guild.
     * Thius method catches any error.
     *
     * @param guild guild
     */
    public void buildGuildPriviledgesSilent(Guild guild) {
        try {
            buildGuildPriviledges(guild);
        } catch (ErrorResponseException e) {
            if (e.getErrorResponse() == ErrorResponse.MISSING_ACCESS) {
                log.debug("Missing access on slash commands for guild {}", Guilds.prettyName(guild));
                return;
            }
            log.error("Error on updating slash commands", e);
        } catch (Exception e) {
            log.error("Could not update guild priviledges for guild {}", Guilds.prettyName(guild), e);
        }
    }

    /**
     * Refresh the guild command priviledges for all commands on all guilds
     * Use {@link #buildGuildPriviledgesSilent(Guild)} if you dont care about errors.
     *
     * @param guild guild
     */
    public void buildGuildPriviledges(Guild guild) {
        var roles = managerRoleSupplier.apply(guild).stream()
                .map(guild::getRoleById)
                .filter(Objects::nonNull)
                .limit(5)
                .collect(Collectors.toList());
        log.debug("Refreshing command priviledges for guild {}", Guilds.prettyName(guild));
        if (roles.isEmpty()) {
            log.debug("No manager role defined on guild {}. Using admin roles.", guild.getIdLong());
            roles = guild.getRoles().stream()
                    .filter(r -> r.hasPermission(Permission.ADMINISTRATOR))
                    .limit(5)
                    .collect(Collectors.toList());
        } else {
            log.debug("Using manager roles on {}", Guilds.prettyName(guild));
        }

        List<CommandPrivilege> privileges = new ArrayList<>();

        for (var role : roles) {
            privileges.add(CommandPrivilege.enable(role));
        }

        guild.retrieveOwner().queue(owner -> {
            privileges.add(CommandPrivilege.enable(owner.getUser()));
            guild.retrieveCommands().queue(commands -> {
                var adminCommands = commands.stream().filter(c -> !c.isDefaultEnabled()).collect(Collectors.toList());
                Map<String, Collection<CommandPrivilege>> commandPrivileges = new HashMap<>();
                for (var adminCommand : adminCommands) {
                    commandPrivileges.put(adminCommand.getId(), privileges);
                }

                guild.updateCommandPrivileges(commandPrivileges).queue(succ -> {
                    log.debug("Update done. Set restricted commands to {} priviledges", privileges.size());
                }, err -> {
                    log.error("Could not update guild priviledges for guild {}", Guilds.prettyName(guild), err);
                });
            }, err -> ErrorResponseException.ignore(ErrorResponse.MISSING_ACCESS));
        }, err -> ErrorResponseException.ignore(ErrorResponse.UNKNOWN_USER));
    }

    public static class Builder<T extends SimpleCommand> {
        private final ShardManager shardManager;
        private final Map<String, T> commands = new HashMap<>();
        @NotNull
        private ILocalizer localizer = ILocalizer.DEFAULT;
        private BiFunction<GenericInteractionCreateEvent, T, Boolean> permissionCheck = (eventWrapper, command) -> {
            if (eventWrapper.isFromGuild()) {
                if (!command.needsPermission()) {
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
        private Function<Guild, List<Long>> managerRoleSupplier = guild -> Collections.emptyList();

        private Builder(ShardManager shardManager) {
            this.shardManager = shardManager;
        }

        /**
         * This will make slash commands only available on these guilds
         *
         * @return builder instance
         */
        public Builder<T> useGuildCommands() {
            useSlashGlobalCommands = false;
            return this;
        }

        /**
         * Adds a localizer to the command hub. This will allow to use {@link ContextLocalizer}.
         *
         * @param localizer localizer instance
         * @return builder instance
         */
        public Builder<T> withLocalizer(Localizer localizer) {
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
        public final Builder<T> withCommands(T... commands) {
            for (var command : commands) {
                this.commands.put(command.command().toLowerCase(Locale.ROOT), command);
            }
            return this;
        }

        /**
         * Adds a permission check. This check determines if a user is allowed to execute a command.
         *
         * @param permissionCheck checks if a user can execute the command
         * @return builder instance
         */
        public Builder<T> withPermissionCheck(BiFunction<GenericInteractionCreateEvent, T, Boolean> permissionCheck) {
            this.permissionCheck = permissionCheck;
            return this;
        }

        /**
         * Adds a conversation system to the command hub
         *
         * @return builder instance
         */
        public Builder<T> withConversationSystem() {
            this.withConversations = true;
            return this;
        }

        /**
         * Adds a command error handler to the hub, which handles uncatched exceptions. Used for loggin.
         *
         * @param commandErrorHandler handler for errors
         * @return builder instance
         */
        public Builder<T> withCommandErrorHandler(BiConsumer<CommandExecutionContext<T>, Throwable> commandErrorHandler) {
            this.commandErrorHandler = commandErrorHandler;
            return this;
        }

        /**
         * Adds a manager role supplier which provides the manager roles for a guild.
         *
         * @param managerRoleSupplier handler for errors
         * @return builder instance
         */
        public Builder<T> withManagerRole(Function<Guild, List<Long>> managerRoleSupplier) {
            this.managerRoleSupplier = managerRoleSupplier;
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
            var commandListener = new CommandHub<>(shardManager, commands, permissionCheck, conversationService, localizer, useSlashGlobalCommands, commandErrorHandler, managerRoleSupplier);
            shardManager.addEventListener(commandListener);
            commandListener.updateCommands();
            return commandListener;
        }
    }
}
