---
title: Interactions
sidebar_position: 2
---

# Interactions

The `InteractionHub` holds all your commands. It deploys them to Discord, receives the interactions and calls your
handlers.

## Create the hub

```java
InteractionHub<Slash, Message, User> hub = InteractionHub.builder(shardManager)
        .withLocalizer(localizer)
        .withCommands(new Ping(), new Settings())
        .withMessages(new ReportMessage())
        .withUsers(new UserInfo())
        .withDefaultPagination()
        .withDefaultMenuService()
        .withDefaultModalService()
        .build();
```

`build()` checks every command, then deploys them in the background and registers the hub as a JDA listener. A
command that cannot be built fails `build()` with an `IllegalStateException` that names the command.

| Method | What it does |
|---|---|
| `withLocalizer(ILocalizer)` | Localizes command names, descriptions and replies. See [localization](./localization.md). |
| `withCommands(...)` | Adds slash commands. Takes `Slash` objects or `SlashProvider`s. |
| `withMessages(...)` | Adds message context commands. Takes `Message` objects or `MessageProvider`s. |
| `withUsers(UserProvider...)` | Adds user context commands. |
| `withDefaultPagination()`, `withPagination(...)` | Enables [pagination](./pagination.md). |
| `withDefaultMenuService()`, `withMenuService(...)` | Enables [menus](./menus-and-modals.md#menus). |
| `withDefaultModalService()`, `withModalService(...)` | Enables [modals](./menus-and-modals.md#modals). |
| `withConversationSystem()` | Enables [conversations](./conversations.md). |
| `withGuildCommandMapper(...)` | Decides on which guilds a private command is deployed. |
| `withCommandAliases(...)` | Gives a slash command another name on single guilds. |
| `withCommandErrorHandler(...)` | Called when a handler throws. The default logs the error. |
| `withPostCommandHook(...)` | Called after every command, failed or not. |
| `withEntitlementProvider(...)`, `withSKUConfiguration(...)`, `withPremiumErrorMessage(...)` | [Premium](./premium.md) checks. |
| `testMode()`, `cleanGuildCommands()` | Deployment switches, see [below](#deployment). |

## Slash commands

A slash command starts with `Slash.of(name, description)`. The builder asks for the settings first, then either one
handler or a set of sub commands.

```java
public class Ping extends SlashCommand {
    public Ping() {
        super(Slash.of("ping", "command.ping.description")
                .guildOnly()
                .command(new PingHandler()));
    }
}
```

With sub commands and arguments:

```java
public class Log extends SlashCommand {
    public Log() {
        super(Slash.of("log", "command.log.description")
                .guildOnly()
                .withPermission(Permission.MESSAGE_MANAGE)
                .subCommand(SubCommand.of("received", "command.log.received.description")
                        .handler(new Received())
                        .argument(Argument.user("user", "command.log.received.options.user.description")
                                .asRequired()))
                .subCommand(SubCommand.of("donated", "command.log.donated.description")
                        .handler(new Donated())));
    }
}
```

Groups bundle sub commands one level deeper: `.group(Group.of("name", "description").subCommand(...))`.

### Settings

| Method | Effect |
|---|---|
| `guildOnly()` | Only usable in guilds. By default a command also works in DMs with the bot. |
| `withPermission(Permission, Permission...)` | The default member permissions a user needs to see the command. |
| `adminCommand()` | Hidden for everyone except administrators by default. |
| `publicCommand()` | Visible for everyone. This is the default. |
| `privateCommand()` | Only deployed on the guilds the guild command mapper returns. |
| `globalCommand()` | Deployed globally. This is the default. |
| `skus(...)` | Requires one of these SKUs, see [premium](./premium.md). |
| `unlocalized()` | Uses the name and description as written, without looking them up. |

Permissions only set Discord's default member permissions. Server admins can change them, and cjda-util does not check
them again when the command runs.

### Arguments

| Factory | Option type | Extra methods |
|---|---|---|
| `Argument.text(name, description)` | String | `minLength`, `maxLength`, `withAutoComplete` |
| `Argument.integer(name, description)` | Integer | `min`, `max`, `withAutoComplete` |
| `Argument.number(name, description)` | Number | `min`, `max`, `withAutoComplete` |
| `Argument.bool(name, description)` | Boolean | |
| `Argument.user`, `role`, `channel`, `mentionable`, `attachment` | as named | |

Every argument has `asRequired()`.

### Handlers

A handler implements `SlashHandler`:

```java
public class Received implements SlashHandler {
    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event, EventContext context) {
        Member member = event.getOption("user").getAsMember();
        event.reply(context.localize("command.log.received.message", Replacement.createMention(member)))
                .setEphemeral(true)
                .queue();
    }

    @Override
    public void onAutoComplete(CommandAutoCompleteInteractionEvent event, EventContext context) {
        event.replyChoices(Completion.complete(event.getFocusedOption().getValue(), "one", "two")).queue();
    }
}
```

`onAutoComplete` is optional. It is called for arguments marked with `withAutoComplete()`.

The `EventContext` gives you the services of the hub:

| Method | Returns |
|---|---|
| `localize(key, Replacement...)` | The text in the guild's language, or the user's language in DMs. |
| `guildLocalizer()`, `userLocalizer()` | A `LocalizationContext` for the guild or the user. |
| `registerPage(...)` | Sends a [paginated message](./pagination.md). |
| `registerMenu(...)` | Sends a [menu](./menus-and-modals.md#menus). |
| `registerModal(...)` | Opens a [modal](./menus-and-modals.md#modals). |
| `startDialog(...)` | Starts a [conversation](./conversations.md). |
| `entitlements()` | The SKUs of the user or guild, see [premium](./premium.md). |
| `interactionHub()` | The hub itself. |

During autocomplete there is no reply callback. Only reply with choices there.

## Message and user commands

Context commands appear when someone right-clicks a message or a user.

```java
public class ReportMessage implements MessageProvider<Message> {
    @Override
    public Message message() {
        return Message.of("Report Message")
                .handler((event, context) -> event.reply("Reported.").setEphemeral(true).queue())
                .setContext(Set.of(InteractionContextType.GUILD))
                .withPermission(Permission.MESSAGE_MANAGE)
                .build();
    }
}
```

`User.of(name)` works the same way with a `UserHandler` and `UserProvider<User>`.

## Localized names

By default every name and description is a locale key. cjda-util looks up:

| Command | Keys |
|---|---|
| Slash command | `command.<command>.name`, `command.<command>.description` |
| Sub command | `command.<command>.<sub>.name`, `command.<command>.<sub>.description` |
| Group | `command.<command>.<group>.name`, `command.<command>.<group>.description` |
| Sub command in a group | `command.<command>.<group>.<sub>.name`, `command.<command>.<group>.<sub>.description` |
| Argument of a command | `command.<command>.options.<argument>.name` and `.description` |
| Argument of a sub command | `command.<command>.<sub>.options.<argument>.name` and `.description` |
| Argument in a group | `command.<command>.<group>.<sub>.options.<argument>.name` and `.description` |
| Message command | `message.<name in snake case>.name` |
| User command | `user.<name in snake case>.name` |

Missing or invalid keys are logged as warnings when the hub starts. Names must be lower snake case and at most 32
characters long.

## Deployment

Commands are deployed on a background thread once all shards are ready. `hub.deployment()` returns a future that
completes when the deployment is done.

- Global commands are deployed globally.
- Private commands are deployed on the guilds the guild command mapper returns for them.
- Aliases from `withCommandAliases` deploy a slash command under another name on a guild. The command still runs as the
  original command.
- When the bot joins a guild, its guild commands are deployed. Call `hub.refreshGuildCommands(guild)` after you change
  what the mapper or the alias function returns.

For development there are two switches. Both can be set in the builder, as a system property or as an environment
variable:

| Builder | Property | Environment | Effect |
|---|---|---|---|
| `testMode()` | `cjda.interactions.testmode` | `CJDA_INTERACTIONS_TESTMODE` | Deploys global commands as guild commands on every guild, so changes show up at once, and removes the global commands. |
| `cleanGuildCommands()` | `cjda.interactions.cleanguildcommands` | `CJDA_INTERACTIONS_CLEANGUILDCOMMANDS` | Redeploys the guild commands on every guild the bot is in, which removes stale ones. |

The environment variable wins over the system property, and a builder call wins over both.

## Errors

When a handler throws, the hub calls the command error handler and the post command hook with an `InteractionResult`.
Use the hook for metrics and for error replies:

```java
.withPostCommandHook(result -> {
    metrics.log(result.context().interaction().meta().name());
    if (result.failed()) {
        log.error("Command {} failed", result.commandAsString(), result.exception());
    }
})
```

To catch exceptions from your other JDA listeners as well, use the `InterceptingEventManager`:

```java
DefaultShardManagerBuilder.createDefault(token)
        .setEventManagerProvider(id -> new InterceptingEventManager((event, error) -> log.error("Listener failed", error)));
```
