---
title: Premium
sidebar_position: 7
---

# Premium

Discord lets you sell SKUs. cjda-util can lock commands behind them and reply with buy buttons when a user lacks one.

## Tell the hub who owns what

The entitlement provider returns the SKUs a user or guild owns. It is called for every interaction.

```java
.withEntitlementProvider((user, guild) -> guild == null
        ? List.of()
        : subscriptions.of(guild).stream().map(SKU::new).toList())
```

`new SKU(long)` takes a SKU id, `new SKU(Entitlement)` the SKU of an entitlement.

## Lock a command

Add `skus(...)` to a slash command with its own handler, a sub command, a message command or a user command:

```java
Slash.of("stats", "command.stats.description")
        .skus(List.of(new SKU(1234567890123456789L)))
        .command(new Stats());
```

On a slash command with sub commands, `skus(...)` on the command itself is not checked. Put it on each sub command
instead.

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
