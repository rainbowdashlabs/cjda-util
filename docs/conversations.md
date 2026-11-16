---
title: Conversations
sidebar_position: 6
---

# Conversations

A conversation walks a user through several steps in a channel. Each step sends a prompt and waits for a message or a
button click. The answer decides which step comes next.

Enable it on the hub with `withConversationSystem()`. Conversations read normal messages, so your bot needs the
`GUILD_MESSAGES` and `DIRECT_MESSAGES` intents and the privileged `MESSAGE_CONTENT` intent.

## Build a conversation

```java
Conversation conversation = ConversationBuilder.builder(
                Step.message("setup.ask.name", ctx -> {
                    String name = ctx.getContentRaw();
                    if (name.isBlank()) return Result.fail();
                    ctx.put("name", name);
                    return Result.proceed(1);
                }).build())
        .addStep(1, Step.button("setup.ask.confirm", buttons -> buttons
                        .add(Button.success("yes", "words.yes"), ctx -> Result.finish())
                        .add(Button.danger("no", "words.no"), ctx -> Result.fail()))
                .build())
        .onClose(conv -> log.info("Setup done"))
        .build();

context.startDialog(conversation);
```

The first step has no number, so no result can lead back to it. Every further step gets one with `addStep`.

| Result | What happens |
|---|---|
| `Result.proceed(step)` | Sends the prompt of that step. |
| `Result.finish()` | Ends the conversation. |
| `Result.fail()` | Sends the prompt of the current step again. |
| `Result.freeze()` | Waits for the next answer without sending anything. |

The context of a step gives you the message (`getContentRaw()`, `getAuthor()`, `getGuild()`), `reply(...)`,
`localize(...)` and a small map shared by all steps: `put`, `get` and `getOrDefault`.

## Locale keys

Conversations use these keys from your [locale files](./localization.md):

| Key | Used for |
|---|---|
| `conversation.start` | Sent when a conversation starts. |
| `conversation.inProgress` | Sent when the user already has a conversation in this channel. |
| `conversation.finished` | Sent when the conversation ends. |
| `conversation.canceled` | Sent when the user cancels. |
| `conversation.cancel`, `conversation.exit` | Words that cancel the conversation when the user sends them. |

A conversation belongs to one user in one channel. It runs until it finishes or the user cancels it.
