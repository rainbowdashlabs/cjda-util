package de.chojo.jdautil.command.dispatching;

import de.chojo.jdautil.command.SimpleCommand;
import de.chojo.jdautil.conversation.ConversationService;
import de.chojo.jdautil.localization.ContextLocalizer;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.localization.Localizer;
import de.chojo.jdautil.localization.util.Language;
import de.chojo.jdautil.util.SlashCommandUtil;
import de.chojo.jdautil.wrapper.SlashCommandContext;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static org.slf4j.LoggerFactory.getLogger;

public class CommandHub<Command extends SimpleCommand> extends ListenerAdapter {
    private static final Logger log = getLogger(CommandHub.class);
    private final ShardManager shardManager;
    private final Map<String, Command> commands;
    private final BiFunction<SlashCommandEvent, Command, Boolean> permissionCheck;
    private final ConversationService conversationService;
    private final ILocalizer localizer;
    private final boolean useSlashGlobalCommands;
    private final BiConsumer<CommandExecutionContext<Command>, Throwable> commandErrorHandler;
    private final Map<Language, List<CommandData>> commandData = new HashMap<>();

    public CommandHub(ShardManager shardManager,
                      Map<String, Command> commands, BiFunction<SlashCommandEvent, Command, Boolean> permissionCheck,
                      ConversationService conversationService, ILocalizer localizer,
                      boolean useSlashGlobalCommands, BiConsumer<CommandExecutionContext<Command>, Throwable> commandErrorHandler) {
        this.shardManager = shardManager;
        this.commands = commands;
        this.permissionCheck = permissionCheck;
        this.conversationService = conversationService;
        this.localizer = localizer;
        this.useSlashGlobalCommands = useSlashGlobalCommands;
        this.commandErrorHandler = commandErrorHandler;
    }

    public static <T extends SimpleCommand> Builder<T> builder(ShardManager shardManager) {
        return new Builder<>(shardManager);
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
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

    @SafeVarargs
    public final void registerCommands(Command... commands) {
        for (var command : commands) {
            this.commands.put(command.command().toLowerCase(Locale.ROOT), command);
            for (var alias : command.alias()) {
                this.commands.put(alias.toLowerCase(Locale.ROOT), command);
            }
        }
    }

    private void updateCommands() {
        log.info("Updating slash commands.");
        for (var language : localizer.languages()) {
            log.info("Creating command data for {} language", language.getCode());
            List<CommandData> localizedCommandData = new ArrayList<>();
            for (var command : commands.values()) {
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
        log.info("Updating slash commands for guild {}({})", guild.getName(), guild.getId());
        var language = localizer.getGuildLocale(guild);
        guild.updateCommands().addCommands(commandData.get(language)).queue();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        if (conversationService != null) {
            conversationService.invoke(event);
        }
    }


    public boolean canExecute(SlashCommandEvent event, Command command) {
        return command.permission() == Permission.UNKNOWN || permissionCheck.apply(event, command);
    }

    public Optional<Command> getCommand(String name) {
        return Optional.ofNullable(commands.get(name.toLowerCase()));
    }

    public Set<Command> getCommands() {
        return Set.copyOf(commands.values());
    }


    public static class Builder<T extends SimpleCommand> {
        private final ShardManager shardManager;
        private final Map<String, T> commands = new HashMap<>();
        @NotNull
        private ILocalizer localizer = ILocalizer.DEFAULT;
        private BiFunction<SlashCommandEvent, T, Boolean> permissionCheck = (eventWrapper, command) -> {
            if (eventWrapper.isFromGuild()) {
                if (command.permission() == Permission.UNKNOWN) {
                    return true;
                }
                return eventWrapper.getMember().hasPermission(command.permission());
            }
            return true;
        };
        private boolean withConversations;
        private boolean useSlashGlobalCommands = true;
        private BiConsumer<CommandExecutionContext<T>, Throwable> commandErrorHandler =
                (context, err) -> log.error("An unhandled exception occured while executing command {}: {}", context.command(), context.args(), err);

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
                for (var alias : command.alias()) {
                    this.commands.put(alias.toLowerCase(Locale.ROOT), command);
                }
            }
            return this;
        }

        /**
         * Adds a permission check. This check determines if a user is allowed to execute a command.
         *
         * @param permissionCheck checks if a user can execute the command
         * @return builder instance
         */
        public Builder<T> withPermissionCheck(BiFunction<SlashCommandEvent, T, Boolean> permissionCheck) {
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
            var commandListener = new CommandHub<T>(shardManager, commands, permissionCheck, conversationService, localizer, useSlashGlobalCommands, commandErrorHandler);
            shardManager.addEventListener(commandListener);
            commandListener.updateCommands();
            return commandListener;
        }
    }
}
