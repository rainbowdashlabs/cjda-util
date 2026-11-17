---
title: Premium
sidebar_position: 7
---

# Premium

Discord lets you sell SKUs. cjda-util can lock commands behind them and reply with buy buttons when a user lacks one.

## Tell the hub who owns what

The entitlements Discord sends with an interaction count automatically. For SKUs you track yourself, for example
subscriptions bought elsewhere, add an entitlement provider. It is called for every interaction, and its SKUs count
next to Discord's.

```java
.withEntitlementProvider((user, guild) -> guild == null
        ? List.of()
        : subscriptions.of(guild).stream().map(SKU::new).toList())
```

`new SKU(long)` takes a SKU id, `new SKU(Entitlement)` the SKU of an entitlement.

Your own SKUs can use small ids, up to 1,000,000. Discord SKU ids are snowflakes and always larger, so cjda-util knows
which SKUs Discord sells. Buy buttons are only shown for Discord SKUs.

## Lock a command

Add `skus(...)` to a slash command, a group, a sub command, a message command or a user command:

```java
Slash.of("stats", "command.stats.description")
        .skus(List.of(new SKU(1234567890123456789L)))
        .command(new Stats());
```

SKUs on a slash command or a group lock everything below them. A sub command runs only when the user owns one SKU of
each level that has SKUs: command, group and sub command. Autocomplete of a locked command answers with no choices.

The command runs when the user owns at least one of the SKUs. Otherwise cjda-util replies with the text of the key
`error.premium` and a button to buy each SKU. Change the key with `withPremiumErrorMessage(...)`.

## Check inside a handler

For features inside a command, check by hand:

```java
SkuMeta required = () -> List.of(new SKU(1234567890123456789L));

if (Premium.isNotEntitled(context, required)) {
    Premium.replyPremium(context, required);
    return;
}
```

`Premium.checkAndReplyPremium(context, required)` does both in one call and returns `true` when it replied.
`Premium.buildEntitlementButtons(...)` gives you the buy buttons for your own reply.

## Test everything

Set `cjda.premium.skipEntitledCheck=true` (system property) or `CJDA_PREMIUM_SKIP_ENTITLED_CHECK=true` (environment)
to treat everyone as entitled. Use it on your test bot only.
