package de.chojo.jdautil.listener;

import de.chojo.jdautil.command.SimpleCommand;
import de.chojo.jdautil.dialog.ConversationHandler;
import de.chojo.jdautil.wrapper.CommandContext;
import de.chojo.jdautil.wrapper.MessageEventWrapper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
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
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CommandListener<Command extends SimpleCommand> extends ListenerAdapter {
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

    public CommandListener(ShardManager shardManager, boolean guildMessages, boolean guildMessagesUpdates,
                           boolean privateMessages, boolean privateMessagesUpdates, int maxUpdateAge,
                           @NotNull String defaultPrefix, Function<Guild, Optional<String>> prefixResolver,
                           Map<String, Command> commands, BiFunction<MessageEventWrapper, Command, Boolean> permissionCheck,
                           ConversationHandler conversationHandler) {
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

        var command = commands.get(label.toLowerCase());
        if (command == null) return;

        if (command.getPermission() != Permission.UNKNOWN && !permissionCheck.apply(eventWrapper, command)) return;

        if (!command.onCommand(eventWrapper, new CommandContext(label, args, conversationHandler))) {
            eventWrapper.getMessage().delete().queueAfter(10, TimeUnit.SECONDS);
            eventWrapper.getChannel().sendMessage(command.getUsage())
                    .queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
        }
    }


    public static class Builder<T extends SimpleCommand> {
        private final ShardManager shardManager;
        private boolean guildMessages = false;
        private boolean guildMessagesUpdates = false;
        private boolean privateMessages = false;
        private boolean privateMessagesUpdates = false;
        private int maxUpdateAge = 5;
        @NotNull
        private String defaultPrefix;
        private Function<Guild, Optional<String>> prefixResolver = (g) -> Optional.of(defaultPrefix);
        private final Map<String, T> commands = new HashMap<>();
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

        public CommandListener<T> build() {
            ConversationHandler conversationHandler = null;
            if (withConversations) {
                conversationHandler = new ConversationHandler();

            }
            var commandListener = new CommandListener<>(shardManager, guildMessages, guildMessagesUpdates,
                    privateMessages, privateMessagesUpdates, maxUpdateAge, defaultPrefix, prefixResolver, commands,
                    permissionCheck, conversationHandler);
            shardManager.addEventListener(commandListener);
            return commandListener;
        }
    }
}
