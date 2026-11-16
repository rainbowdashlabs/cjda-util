---
title: Localization
sidebar_position: 3
---

# Localization

The `Localizer` reads your texts from `.properties` files, one per language. The hub uses it for command names and
descriptions, and you use it for every reply.

## Create the localizer

```java
Localizer localizer = Localizer.builder(DiscordLocale.ENGLISH_US)
        .addLanguage(DiscordLocale.GERMAN, DiscordLocale.FRENCH)
        .withLanguageProvider(guild -> settings.language(guild))
        .withGuildLocaleCodeProvider((guild, key) -> Optional.empty())
        .build();
```

| Method | Default | What it does |
|---|---|---|
| `addLanguage(DiscordLocale...)` | only the default language | The languages your bot ships. |
| `withBundlePath(String)` | `locale` | Base name of the resource bundles. |
| `withLanguageProvider(...)` | the default language | Returns an `Optional<DiscordLocale>` with the language a guild chose, for example from your database. |
| `withGuildLocaleCodeProvider(...)` | none | Lets a guild override single keys. Return `Optional.empty()` to use the files. |

Set a guild locale code provider whenever you localize for a guild. Without one, localizing with a guild fails.

Pass the localizer to the hub with `withLocalizer(localizer)`. Pages, menus, modals and conversations use it then too.

## Locale files

The files live in your resources and follow the `ResourceBundle` naming: the bundle path, an underscore and the
language with an underscore instead of a dash.

```text
src/main/resources/
├── locale.properties
├── locale_en_US.properties
├── locale_de.properties
└── locale_fr.properties
```

`locale.properties` is the base bundle. Keys missing in a language are taken from it, so it is a good place for every
key. When the localizer starts, it warns about keys that are missing in one of the languages.

```properties
command.invite.message.title=Invite me
command.invite.message.click=[Click here](%URL%)
command.dashboard.message.topUser=Highest $words.reputation$
words.reputation=Reputation
```

## Keys and replacements

There are two ways to write a text:

- A plain key such as `command.invite.message.title` is looked up as a whole. A key has at least one dot and uses
  letters, dots and underscores.
- Any other text is kept as it is, and every `$key$` inside it is looked up. This lets you mix keys and text:
  `"$command.vote.field.streak$: 5"`.

Values are looked up again after the replacements, so a value can contain `$other.key$`.

Replacements fill `%NAME%` placeholders:

```java
context.localize("command.invite.message.click", Replacement.create("URL", inviteLink));
```

| Factory | Placeholder | Value |
|---|---|---|
| `Replacement.create("KEY", value)` | `%KEY%` | `String.valueOf(value)` |
| `Replacement.create("KEY", value, Format.BOLD)` | `%KEY%` | the value in bold |
| `Replacement.createMention(member)` | `%USER%` | the mention |
| `Replacement.createMention(role)` | `%ROLE%` | the mention |
| `Replacement.createMention(channel)` | `%CHANNEL%` | the mention |
| `Replacement.createMention("KEY", mentionable)` | `%KEY%` | the mention |

Placeholders match without regard to case. `Format` also has `ITALIC`, `UNDERLINE`, `CODE` and `CODE_BLOCK`.

## Which language is used

| You call | Language |
|---|---|
| `context.localize(...)` in a handler | The guild's language in a guild, the user's language in DMs. |
| `localizer.localize(key, guild, ...)` | The guild's language. |
| `context.guildLocalizer().localize(...)` | The guild's Discord locale. Your language provider is only asked when that locale is English (US). |
| `context.userLocalizer().localize(...)` | The user's client language. |
| `localizer.localize(key, locale, guild, ...)` | The given language. |

The guild's language is the one your language provider returns, else the guild's Discord locale. A language you did
not add falls back to the default language. A key missing everywhere comes back as the key itself, with a warning.

`LocalizedEmbedBuilder` with `guildLocalizer()`, menu labels and modal labels follow the `guildLocalizer()` rule.

## Embeds

`LocalizedEmbedBuilder` is an `EmbedBuilder` that localizes everything you put in:

```java
MessageEmbed embed = new LocalizedEmbedBuilder(context.guildLocalizer())
        .setTitle("command.invite.message.title")
        .setDescription("command.invite.message.click", Replacement.create("URL", inviteLink))
        .build();
```

## Quieter logs

Command names often have no translation on purpose. Set `cjda.localisation.error.name=false` (system property) or
`CJDA_LOCALISATION_NAME_ERROR=false` (environment) to stop warnings about missing `.name` keys.
