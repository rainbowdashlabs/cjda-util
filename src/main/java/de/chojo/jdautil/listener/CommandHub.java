package de.chojo.jdautil.listener;

import de.chojo.jdautil.command.SimpleCommand;
import de.chojo.jdautil.dialog.ConversationHandler;
import de.chojo.jdautil.localization.ContextLocalizer;
import de.chojo.jdautil.localization.Localizer;
import de.chojo.jdautil.wrapper.CommandContext;
import de.chojo.jdautil.wrapper.MessageEventWrapper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommandHub<Command extends SimpleCommand> extends ListenerAdapter {
    private final ShardManager shardManager;
    private final boolean guildMessages;
    private final boolean guildMessagesUpdates;
    private final boolean privateMessages;
    private final boolean privateMessagesUpdates;
    private final int maxUpdateAge;
    private final String defaultPrefix;
    private final Function<Guild, Optional<String>> prefixResolver;
    private final Map<String, Command> commands;
    private final BiFunction<MessageEventWrapper, Command, Boolean> permissionCheck;
    private final ConversationHandler conversationHandler;
    private final BiFunction<ContextLocalizer, Command, MessageEmbed> invalidArgumentProvider;
    private final Localizer localizer;

    public CommandHub(ShardManager shardManager, boolean guildMessages, boolean guildMessagesUpdates,
                      boolean privateMessages, boolean privateMessagesUpdates, int maxUpdateAge,
                      @NotNull String defaultPrefix, Function<Guild, Optional<String>> prefixResolver,
                      Map<String, Command> commands, BiFunction<MessageEventWrapper, Command, Boolean> permissionCheck,
                      ConversationHandler conversationHandler,
                      BiFunction<ContextLocalizer, Command, MessageEmbed> invalidArgumentProvider, Localizer localizer) {
        this.shardManager = shardManager;
        this.guildMessages = guildMessages;
        this.guildMessagesUpdates = guildMessagesUpdates;
        this.privateMessages = privateMessages;
        this.privateMessagesUpdates = privateMessagesUpdates;
        this.maxUpdateAge = maxUpdateAge;
        this.defaultPrefix = defaultPrefix;
        this.prefixResolver = prefixResolver;
        this.commands = commands;
        this.permissionCheck = permissionCheck;
        this.conversationHandler = conversationHandler;
        this.invalidArgumentProvider = invalidArgumentProvider;
        this.localizer = localizer;
    }

    public static <T extends SimpleCommand> Builder<T> builder(ShardManager shardManager, String defaultPrefix) {
        return new Builder<>(shardManager, defaultPrefix);
    }

    @SafeVarargs
    public final void registerCommands(Command... commands) {
        for (var command : commands) {
            this.commands.put(command.getCommand().toLowerCase(Locale.ROOT), command);
            for (var alias : command.getAlias()) {
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


    private void onMessageReceived(MessageEventWrapper eventWrapper) {
        if (eventWrapper.getAuthor().isBot()) return;

        eventWrapper.registerLocalizer(localizer);

        if (conversationHandler != null) {
            if (conversationHandler.invoke(eventWrapper)) {
                return;
            }
        }

        var contentRaw = eventWrapper.getMessage().getContentRaw();
        var prefix = prefixResolver.apply(eventWrapper.getGuild()).orElse(defaultPrefix);

        if (!contentRaw.startsWith(prefix)) return;

        var stripped = contentRaw.substring(prefix.length()).split("\\s");
        var label = stripped[0];
        var args = Arrays.copyOfRange(stripped, 1, stripped.length);

        var optCommand = getCommand(label);
        if (optCommand.isEmpty()) return;

        var command = optCommand.get();

        if (command.getPermission() != Permission.UNKNOWN && !permissionCheck.apply(eventWrapper, command)) return;

        if (!command.onCommand(eventWrapper, new CommandContext(label, args, conversationHandler))) {
            if (invalidArgumentProvider != null) {
                eventWrapper.replyErrorAndDelete(invalidArgumentProvider.apply(localizer.getContextLocalizer(eventWrapper),
                        command), 10);
                return;
            }
            eventWrapper.getMessage().delete().queueAfter(10, TimeUnit.SECONDS);
            var s = "Invalid arguments: " + command.getCommand() + " " + (command.getArgs() == null ? "" : command.getArgs());
            s += "\n" + Arrays.stream(command.getSubCommands())
                    .map(c -> command.getCommand() + " " + c.getName() + (c.getArgs() == null ? "" : c.getArgs()))
                    .collect(Collectors.joining("\n"));
            eventWrapper.getChannel().sendMessage(s)
                    .queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
        }
    }

    public boolean canExecute(MessageEventWrapper eventWrapper, Command command) {
        return permissionCheck.apply(eventWrapper, command);
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
        private boolean guildMessages = false;
        private boolean guildMessagesUpdates = false;
        private boolean privateMessages = false;
        private boolean privateMessagesUpdates = false;
        private int maxUpdateAge = 5;
        @NotNull
        private String defaultPrefix;
        private Function<Guild, Optional<String>> prefixResolver = (g) -> Optional.of(defaultPrefix);
        private BiFunction<ContextLocalizer, T, MessageEmbed> invalidArgumentProvider;
        private Localizer localizer;
        private BiFunction<MessageEventWrapper, T, Boolean> permissionCheck = (eventWrapper, command) -> {
            if (eventWrapper.isGuild()) {

                if (command.getPermission() == Permission.UNKNOWN) {
                    return true;
                }
                return eventWrapper.getMember().hasPermission(command.getPermission());
            }
            return true;
        };
        private boolean withConversations = false;

        private Builder(ShardManager shardManager, String defaultPrefix) {
            this.shardManager = shardManager;
            this.defaultPrefix = defaultPrefix;
        }

        public Builder<T> receiveGuildMessage() {
            this.guildMessages = true;
            return this;
        }

        public Builder<T> receiveGuildMessagesUpdates() {
            this.guildMessagesUpdates = true;
            return this;
        }

        public Builder<T> receivePrivateMessage() {
            this.privateMessages = true;
            return this;
        }

        public Builder<T> receivePrivateMessagesUpdates() {
            this.privateMessagesUpdates = true;
            return this;
        }

        public Builder<T> withMaxMessageUpdateAge(int age) {
            this.maxUpdateAge = age;
            return this;
        }

        public Builder<T> withPrefixResolver(Function<Guild, Optional<String>> prefixResolver) {
            this.prefixResolver = guild -> guild == null ? Optional.empty() : prefixResolver.apply(guild);
            return this;
        }

        public Builder<T> withInvalidArgumentProvider(BiFunction<ContextLocalizer, T, MessageEmbed> invalidArgumentProvider) {
            this.invalidArgumentProvider = invalidArgumentProvider;
            return this;
        }

        public Builder<T> withLocalizer(Localizer localizer) {
            this.localizer = localizer;
            return this;
        }

        @SafeVarargs
        public final Builder<T> withCommands(T... commands) {
            for (var command : commands) {
                this.commands.put(command.getCommand().toLowerCase(Locale.ROOT), command);
                for (var alias : command.getAlias()) {
                    this.commands.put(alias.toLowerCase(Locale.ROOT), command);
                }
            }
            return this;
        }

        public Builder<T> withPermissionCheck(BiFunction<MessageEventWrapper, T, Boolean> permissionCheck) {
            this.permissionCheck = permissionCheck;
            return this;
        }

        public Builder<T> withConversationSystem() {
            this.withConversations = true;
            return this;
        }

        public CommandHub<T> build() {
            ConversationHandler conversationHandler = null;
            if (withConversations) {
                conversationHandler = new ConversationHandler();

            }
            var commandListener = new CommandHub<>(shardManager, guildMessages, guildMessagesUpdates,
                    privateMessages, privateMessagesUpdates, maxUpdateAge, defaultPrefix, prefixResolver, commands,
                    permissionCheck, conversationHandler, invalidArgumentProvider, localizer);
            shardManager.addEventListener(commandListener);
            return commandListener;
        }
    }
}
