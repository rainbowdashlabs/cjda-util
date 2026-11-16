---
title: Utilities
sidebar_position: 9
---

# Utilities

Smaller helpers that most bots end up needing.

## Configuration files

`Configuration` loads a JSON file into a class of yours and writes missing fields back with their defaults.

```java
Configuration<BotConfig> configuration = Configuration.create(new BotConfig());
String token = configuration.config().token();
```

- The path comes from the environment variable `BOT_CONFIG` or the system property `bot.config`. It is resolved against
  the working directory.
- On the first start the file is created with the defaults, and `create` throws a `ConfigurationException` asking you to
  fill it in.
- Fields are read and written directly. Getters are ignored, unknown fields in the file too.
- `configuration.reload()` reads the file again.

Extend `BaseConfiguration` to use your own `ObjectMapper`.

## Environment and properties

`SysVar` reads a value from an environment variable or a system property:

```java
String host = SysVar.envOrProp("BOT_DB_HOST", "bot.db.host", "localhost");
Optional<String> token = SysVar.envOrProp("BOT_TOKEN", "bot.token");
```

`envOrProp` checks the environment first, `propOrEnv` the property first.

## Parsing user input

| Helper | What it does |
|---|---|
| `Verifier.isValidId(text)` | Checks for an id or a mention. |
| `Verifier.getIdRaw(text)` | Takes the id out of a mention. |
| `Verifier.equalSnowflake(a, b)` | Compares two Discord entities by id. |
| `DiscordResolver.getGuildMember(guild, text)` | Finds a member by id, mention, tag or name. |
| `DiscordResolver.getRole`, `getTextChannel`, `getGuildChannel` | The same for roles and channels. |
| `DiscordResolver.fuzzyGuildUserSearch(guild, text)` | Members ranked by how well their name matches. |
| `ValueParser.parseInt`, `parseLong`, `parseDouble`, `parseBoolean` | Parsing that returns an `Optional` instead of throwing. |
| `ArgumentUtil.parseQuotedArgs(text, strip)` | Splits text into words and keeps quoted parts together. |

## Discord helpers

| Helper | What it does |
|---|---|
| `MentionUtil.user(id)`, `role(id)`, `channel(id)` | Mention strings from ids. |
| `Completion.complete(input, values...)` | Autocomplete choices that start with the input. Also works with enums. |
| `Choice.toStringChoice(list)` | Command choices from values. |
| `Guilds.prettyName(guild)` | `Name (id)` for logs. |
| `SlashCommandUtil.commandAsString(event)` | The full command with its options, for logs. |
| `PermissionErrorHandler.assertPermissions(channel, permissions...)` | Throws `InsufficientPermissionException` when the bot lacks a permission. |
| `Colors.Pastel.*`, `Colors.Strong.*` | Embed colours. |

## Everything else

| Helper | What it does |
|---|---|
| `SnowflakeCreator.builder().build().nextLong()` | Unique snowflake ids. |
| `ThreadFactories.builder()` | Named thread factories with an exception handler. |
| `TextFormatting.getTableBuilder(...)` | A text table for code blocks. |
| `TextFormatting.cropText(...)` | Shortens text to a length. |
| `Futures.whenComplete(onValue, onError)` | A handler for `CompletableFuture.whenComplete`. |
| `Pair.of(a, b)`, `Triple.of(a, b, c)` | Small value holders. |
| `TriFunction`, `QuadFunction`, `TriConsumer`, `QuadConsumer` | Functional interfaces with more arguments. |
