package de.chojo.jdautil.command.dispatching;

import de.chojo.jdautil.command.SimpleCommand;
import de.chojo.jdautil.dialog.ConversationHandler;
import de.chojo.jdautil.localization.ContextLocalizer;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.localization.Localizer;
import de.chojo.jdautil.parsing.ArgumentUtil;
import de.chojo.jdautil.parsing.DiscordResolver;
import de.chojo.jdautil.parsing.Verifier;
import de.chojo.jdautil.util.SlashCommandUtil;
import de.chojo.jdautil.wrapper.CommandContext;
import de.chojo.jdautil.wrapper.MessageEventWrapper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class CommandHub<Command extends SimpleCommand> extends ListenerAdapter {
    private static final Logger log = getLogger(CommandHub.class);
    private final ShardManager shardManager;
    private final boolean guildMessages;
    private final boolean guildMessagesUpdates;
    private final boolean privateMessages;
    private final boolean privateMessagesUpdates;
    private final boolean slashCommands;
    private final boolean textCommands;
    private final int maxUpdateAge;
    private final String defaultPrefix;
    private final Function<Guild, Optional<String>> prefixResolver;
    private final Map<String, Command> commands;
    private final BiFunction<MessageEventWrapper, Command, Boolean> permissionCheck;
    private final ConversationHandler conversationHandler;
    private final BiFunction<ContextLocalizer, Command, MessageEmbed> invalidArgumentProvider;
    private final ILocalizer localizer;
    private final boolean useSlashGlobalCommands;
    private final long[] guildIds;
    private final BiConsumer<CommandExecutionContext<Command>, Throwable> commandErrorHandler;

    public CommandHub(ShardManager shardManager, boolean guildMessages, boolean guildMessagesUpdates,
                      boolean privateMessages, boolean privateMessagesUpdates, boolean slashCommands, boolean textCommands, int maxUpdateAge,
                      @NotNull String defaultPrefix, Function<Guild, Optional<String>> prefixResolver,
                      Map<String, Command> commands, BiFunction<MessageEventWrapper, Command, Boolean> permissionCheck,
                      ConversationHandler conversationHandler,
                      BiFunction<ContextLocalizer, Command, MessageEmbed> invalidArgumentProvider, ILocalizer localizer,
                      boolean useSlashGlobalCommands, long[] guildIds, BiConsumer<CommandExecutionContext<Command>, Throwable> commandErrorHandler) {
        this.shardManager = shardManager;
        this.guildMessages = guildMessages;
        this.guildMessagesUpdates = guildMessagesUpdates;
        this.privateMessages = privateMessages;
        this.privateMessagesUpdates = privateMessagesUpdates;
        this.slashCommands = slashCommands;
        this.textCommands = textCommands;
        this.maxUpdateAge = maxUpdateAge;
        this.defaultPrefix = defaultPrefix;
        this.prefixResolver = prefixResolver;
        this.commands = commands;
        this.permissionCheck = permissionCheck;
        this.conversationHandler = conversationHandler;
        this.invalidArgumentProvider = invalidArgumentProvider;
        this.localizer = localizer;
        this.useSlashGlobalCommands = useSlashGlobalCommands;
        this.guildIds = guildIds;
        this.commandErrorHandler = commandErrorHandler;
    }

    public static <T extends SimpleCommand> Builder<T> builder(ShardManager shardManager, String defaultPrefix) {
        return new Builder<>(shardManager, defaultPrefix);
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (!slashCommands) return;
        if (event.getGuild() == null && !privateMessages) {
            event.deferReply(true).queue();
            event.reply("🚫").queue();
            return;
        }
        if (event.getGuild() != null && !guildMessages) {
            event.deferReply(true).queue();
            event.reply("🚫").queue();
            return;
        }
        var name = event.getName();
        var command = getCommand(name).get();
        if (!canExecute(MessageEventWrapper.create(event), command)) {
            event.deferReply(true).queue();
            event.reply("🚫").queue();
            return;
        }
        try {
            command.onSlashCommand(event);
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

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (!guildMessages) return;
        onMessageReceived(MessageEventWrapper.create(event));
    }

    @Override
    public void onGuildMessageUpdate(@NotNull GuildMessageUpdateEvent event) {
        if (!guildMessagesUpdates) return;

        if (freshMessage(event.getMessage())) {
            onMessageReceived(MessageEventWrapper.create(event));
        }
    }

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        if (!privateMessages) return;
        onMessageReceived(MessageEventWrapper.create(event));
    }

    @Override
    public void onPrivateMessageUpdate(@NotNull PrivateMessageUpdateEvent event) {
        if (!privateMessagesUpdates) return;
        if (freshMessage(event.getMessage())) {
            onMessageReceived(MessageEventWrapper.create(event));
        }
    }

    private boolean freshMessage(Message message) {
        return message.getTimeCreated().toInstant().until(Instant.now(), ChronoUnit.MINUTES) <= maxUpdateAge;
    }

    private void updateCommands() {
        log.info("Updating slash commands.");
        List<CommandData> commandData = new ArrayList<>();
        for (var command : new HashSet<>(commands.values())) {
            if (command.subCommands() != null) {
                log.info("Registering command {} with {} subcommands", command.command(), command.subCommands().length);
            } else {
                log.info("Registering command {}.", command.command());
            }
            commandData.add(command.getCommandData(localizer));
        }
        if (useSlashGlobalCommands) {
            var baseShard = shardManager.getShardById(0);
            try {
                baseShard.awaitReady();
            } catch (InterruptedException e) {
            }
            log.info("Updating global slash commands");
            baseShard.updateCommands().addCommands(commandData).queue();
        } else {
            for (var shard : shardManager.getShards()) {
                try {
                    shard.awaitReady();
                } catch (InterruptedException e) {
                }
            }
            for (var guildId : guildIds) {
                var guild = shardManager.getGuildById(guildId);
                log.info("Updating slash commands for guild {}({})", guild.getName(), guildId);
                guild.updateCommands().addCommands(commandData).queue();
            }
        }
    }


    private void onMessageReceived(MessageEventWrapper eventWrapper) {
        if (!textCommands) return;
        if (eventWrapper.getAuthor().isBot()) return;

        eventWrapper.registerLocalizer(localizer);

        if (conversationHandler != null) {
            if (conversationHandler.invoke(eventWrapper)) {
                return;
            }
        }

        var contentRaw = eventWrapper.getMessage().getContentRaw();
        var prefix = prefixResolver.apply(eventWrapper.getGuild()).orElse(defaultPrefix);

        String[] stripped;
        var splitted = contentRaw.split(" ");
        var user = DiscordResolver.getUser(shardManager, splitted[0]);

        if (user.isEmpty() || !Verifier.equalSnowflake(user.get(), eventWrapper.getJda().getSelfUser())) {
            if (prefix.startsWith("re:")) {
                var pattern = Pattern.compile(prefix.substring(3));
                if (!pattern.matcher(contentRaw).find()) return;
                // command via regex prefix
                stripped = pattern.matcher(contentRaw).replaceAll("").split("\\s+");
            } else {
                if (!contentRaw.startsWith(prefix)) return;
                // command via prefix
                stripped = contentRaw.substring(prefix.length()).split("\\s+");
            }
        } else {
            // Bot is mentioned
            stripped = ArgumentUtil.getRangeAsList(splitted, 1).toArray(new String[0]);
        }

        if (stripped.length == 0) {
            return;
        }

        var label = stripped[0];
        var args = new String[0];
        if (stripped.length > 1) {
            args = Arrays.copyOfRange(stripped, 1, stripped.length);
        }

        var optCommand = getCommand(label);
        if (optCommand.isEmpty()) return;

        var command = optCommand.get();

        if (!canExecute(eventWrapper, command)) return;

        var success = false;
        try {
            success = command.onCommand(eventWrapper, new CommandContext(label, args, conversationHandler));
        } catch (Throwable t) {
            var executionContext = new CommandExecutionContext<>(command, String.join(" ", args), eventWrapper.getGuild(), eventWrapper.getChannel());
            commandErrorHandler.accept(executionContext, t);
            return;
        }

        if (!success) {
            if (invalidArgumentProvider != null) {
                eventWrapper.replyErrorAndDelete(invalidArgumentProvider.apply(localizer.getContextLocalizer(eventWrapper),
                        command), 10);
                return;
            }
            eventWrapper.getMessage().delete().queueAfter(10, TimeUnit.SECONDS);
            // Todo implement new args
            var s = "Invalid arguments: " + command.command() + " " + (command.args() == null ? "" : command.args());
            s += "\n" + Arrays.stream(command.getSubCommands())
                    .map(c -> command.command() + " " + c.name() + (c.args() == null ? "" : c.args()))
                    .collect(Collectors.joining("\n"));
            eventWrapper.getChannel().sendMessage(s)
                    .queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
        }
    }

    public boolean canExecute(MessageEventWrapper eventWrapper, Command command) {
        return command.permission() == Permission.UNKNOWN || permissionCheck.apply(eventWrapper, command);
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
        private boolean guildMessages;
        private boolean guildMessagesUpdates;
        private boolean privateMessages;
        private boolean privateMessagesUpdates;
        private boolean slashCommands;
        private boolean textCommands;
        private int maxUpdateAge = 5;
        @NotNull
        private String defaultPrefix;
        private Function<Guild, Optional<String>> prefixResolver = (g) -> Optional.of(defaultPrefix);
        private BiFunction<ContextLocalizer, T, MessageEmbed> invalidArgumentProvider;
        private ILocalizer localizer = ILocalizer.DEFAULT;
        private BiFunction<MessageEventWrapper, T, Boolean> permissionCheck = (eventWrapper, command) -> {
            if (eventWrapper.isGuild()) {

                if (command.permission() == Permission.UNKNOWN) {
                    return true;
                }
                return eventWrapper.getMember().hasPermission(command.permission());
            }
            return true;
        };
        private boolean withConversations;
        private boolean useSlashGlobalCommands = true;
        private long[] guildIds;
        private BiConsumer<CommandExecutionContext<T>, Throwable> commandErrorHandler =
                (context, err) -> log.error("An unhandled exception occured while executing command {}: {}", context.command(), context.args(), err);

        private Builder(ShardManager shardManager, String defaultPrefix) {
            this.shardManager = shardManager;
            this.defaultPrefix = defaultPrefix;
        }

        /**
         * This will make slash commands only available on these guilds
         *
         * @param ids all guild ids which should have slash commands
         * @return builder instance
         */
        public Builder<T> onlyGuildCommands(long... ids) {
            useSlashGlobalCommands = false;
            this.guildIds = ids;
            return this;
        }

        /**
         * Accept slash commands
         *
         * @return builder instance
         */
        public Builder<T> withSlashCommands() {
            slashCommands = true;
            return this;
        }

        /**
         * Accept text commands
         *
         * @return builder instance
         */
        public Builder<T> withTextCommands() {
            textCommands = true;
            return this;
        }

        /**
         * Listen to guild commands on guilds
         *
         * @return builder instance
         */
        public Builder<T> receiveGuildCommands() {
            textCommands = true;
            this.guildMessages = true;
            return this;
        }

        /**
         * Listen to message updates on guild for commands
         *
         * @return builder instance
         */
        public Builder<T> receiveGuildMessagesUpdates() {
            textCommands = true;
            this.guildMessagesUpdates = true;
            return this;
        }

        /**
         * Listen to commands in private channels
         *
         * @return builder instance
         */
        public Builder<T> receivePrivateMessage() {
            textCommands = true;
            this.privateMessages = true;
            return this;
        }

        /**
         * Listen to message updates in private channels for commands
         *
         * @return builder instance
         */
        public Builder<T> receivePrivateMessagesUpdates() {
            textCommands = true;
            this.privateMessagesUpdates = true;
            return this;
        }

        /**
         * Set the mas age for message updates
         *
         * @param age age of message
         * @return builder instance
         */
        public Builder<T> withMaxMessageUpdateAge(int age) {
            this.maxUpdateAge = age;
            return this;
        }

        /**
         * Register a resolver to retrieve the guild prefix on runtime
         *
         * @param prefixResolver prefix resolver which maps a guild to an optional prefix. Prefix may be empty to use the default prefix
         * @return builder instance
         */
        public Builder<T> withPrefixResolver(Function<Guild, Optional<String>> prefixResolver) {
            this.prefixResolver = guild -> guild == null ? Optional.empty() : prefixResolver.apply(guild);
            return this;
        }

        /**
         * Returns a message embed which provides help for the command when an invalid argument is entered.
         * <p>
         * Arguments are invalid if {@link SimpleCommand#onCommand(MessageEventWrapper, CommandContext)} returns {@code false}
         *
         * @param invalidArgumentProvider returns a message embed for the command
         * @return builder instance
         */
        public Builder<T> withInvalidArgumentProvider(BiFunction<ContextLocalizer, T, MessageEmbed> invalidArgumentProvider) {
            this.invalidArgumentProvider = invalidArgumentProvider;
            return this;
        }

        /**
         * Adds a localizer to the command hub. This will allow to use {@link ContextLocalizer} in the {@link MessageEventWrapper}.
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
                log.info("Registering command {}", command.command());
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
        public Builder<T> withPermissionCheck(BiFunction<MessageEventWrapper, T, Boolean> permissionCheck) {
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
            ConversationHandler conversationHandler = null;
            if (withConversations) {
                conversationHandler = new ConversationHandler();

            }
            var commandListener = new CommandHub<T>(shardManager, guildMessages, guildMessagesUpdates,
                    privateMessages, privateMessagesUpdates, slashCommands, textCommands, maxUpdateAge, defaultPrefix, prefixResolver, commands,
                    permissionCheck, conversationHandler, invalidArgumentProvider, localizer, useSlashGlobalCommands, guildIds, commandErrorHandler);
            shardManager.addEventListener(commandListener);
            if (slashCommands) {
                commandListener.updateCommands();
            }
            return commandListener;
        }
    }
}
