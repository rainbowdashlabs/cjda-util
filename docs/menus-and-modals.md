---
title: Menus and modals
sidebar_position: 4
---

# Menus and modals

Menus send a message with buttons and select menus and call a lambda per component. Modals open a form and call a
lambda per input and one for the whole form. Both localize their labels for you.

Enable them on the hub with `withDefaultMenuService()` and `withDefaultModalService()`.

## Menus

```java
context.registerMenu(MenuAction.forCallback(context.localize("command.cleanup.message.confirm"), event)
        .forUser(event.getUser())
        .addComponent(MenuEntry.of(Button.danger("yes", "words.yes"), ctx -> {
            cleanup();
            ctx.event().editMessage("Done.").setComponents().queue();
        }))
        .addComponent(MenuEntry.of(Button.secondary("no", "words.no"),
                ctx -> ctx.event().editComponents().queue()))
        .build());
```

| Method | What it does |
|---|---|
| `MenuAction.forCallback(text or embed, event)` | Replies to an interaction. |
| `MenuAction.forChannel(text or embed, channel)` | Sends a new message into a channel. |
| `asEphemeral()` | Makes the reply ephemeral. |
| `forUser(user)` | Only this user can use the components. |
| `addComponent(MenuEntry.of(...))` | Adds a button, string select or entity select with its lambda. |

The lambda gets an `EntryContext` with the `event()`, the `entry()` and the `container()` of all entries. Hide or show
entries with `entry.hidden()` or `entry.visible(true)`, then call `ctx.refresh()` to redraw the components.

Button labels, select options and placeholders are localized. The message text is sent as it is, so localize it
yourself as in the example.

Your lambda acknowledges the interaction. cjda-util does not do it for you.

A menu lives for 10 minutes after its last use. Clicks after that, or by other users when the menu is bound to one
user, are ignored. Change the time with `withMenuService(m -> m.withCache(c -> c.expireAfterAccess(30, TimeUnit.MINUTES)))`.

## Modals

```java
ModalHandler modal = ModalHandler.builder("command.feedback.modal.title")
        .addInput(TextInputHandler.builder("text", "command.feedback.modal.text", TextInputStyle.PARAGRAPH)
                .withRequiredRange(10, 1000)
                .withPlaceholder("command.feedback.modal.placeholder"))
        .withHandler(event -> {
            String text = event.getValue("text").getAsString();
            event.reply("Thanks!").setEphemeral(true).queue();
        })
        .build();

context.registerModal(modal);
```

`registerModal` only works in command handlers.

| `TextInputHandler` builder | What it does |
|---|---|
| `asOptional()` | The input may stay empty. |
| `withMinLength`, `withMaxLength`, `withRequiredRange` | Length limits. |
| `withValue(String)` | A prefilled value. |
| `withPlaceholder(String)` | A localized placeholder. |
| `withHandler(Consumer<ModalMapping>)` | Called with the value of this input on submit. |

On submit, every input handler runs first, then the modal handler. The modal handler acknowledges the interaction.

A modal can be submitted for 30 minutes.
