---
title: Bot lists
sidebar_position: 8
---

# Bot lists

The `BotlistService` posts your server count to bot list sites and receives their vote webhooks.

## Supported lists

| List | Server count | Vote webhook route |
|---|---|---|
| top.gg | yes | `voting/topgg` |
| discordbotlist.com | yes | `voting/discordbotlistcom` |
| botlist.me | yes | `voting/botlistme` |
| discord.bots.gg | yes | none |

## Configure a list

Each list takes a `BotListConfig`. It reads well from JSON, so it fits into your bot's configuration file:

```json
{
  "name": "top.gg",
  "statsToken": "token to post stats",
  "voteToken": "secret the list sends with every vote",
  "guildId": 264445053596991498,
  "profileUrl": "https://top.gg/bot/your-bot-id",
  "voteUrl": "https://top.gg/bot/your-bot-id/vote"
}
```

Use empty strings for values you do not have. `profileUrl` and `voteUrl` must start with the list's address when they
are set.

## Start the service

```java
BotlistService.build(shardManager,
                () -> shardManager.getGuilds().size(),
                () -> shardManager.getUsers().size())
        .forTopGG(config.topGg())
        .forDiscordBotListCOM(config.discordBotlistCom())
        .forDiscordBotsGG(config.discordBotsGg())
        .forBotlistMe(config.botlistMe())
        .withVoteService(votes -> votes.withVoteWeebhooks(javalin)
                .onVote(vote -> rewards.give(vote.userId(), vote.listId()))
                .build())
        .build();
```

Always set a vote service when you add a list.

| Method | Default | What it does |
|---|---|---|
| `withSubmitInterval(interval, unit)` | 60 minutes | How often the counts are posted. The first post happens after one interval unit. |
| `withExecutorService(executor)` | a single thread | Where the posts run. |
| `withVoteService(...)` | | Receives votes. |

## Votes

`withVoteWeebhooks(javalin)` registers one POST route per list on your Javalin instance. `withVoteWeebhooks(host, port)`
starts a Javalin server of its own instead. Point each list's webhook at the route from the table above.

A vote request must carry the list's `voteToken` in the `Authorization` header, otherwise it is answered with 401.

Your `onVote` consumer gets a `VoteData`:

| Method | Content |
|---|---|
| `listId()` | The list, for example `top.gg`. |
| `userId()` | The user who voted. |
| `bonus()` | `true` for weekend votes on top.gg. |
| `guildId()` | For top.gg, the `guildId` query parameter of the webhook URL. `0` otherwise. |

## Add another list

`BotList.builder(name)` builds a list of your own:

```java
BotList myList = BotList.builder("mylist.example")
        .forClient(shardManager)
        .withBaseUrl("https://mylist.example/api")
        .withAuthentication(AuthHandler.of(statsToken))
        .withSubmission(StatsMapper.of("bots/{id}/stats", (data, map) -> map.add("servers", data.guilds())))
        .withVoteReceiver(VoteReceiver.create(MyVotePayload.class, AuthHandler.of(voteToken)))
        .build();
```

`{id}` is replaced with your application id. `MyVotePayload` implements `VoteDataAdapter` and turns the webhook body
into a `VoteData`. Override `injectGuild(long)` to receive the `guildId` query parameter. Register the list with `forBotlist(myList)`.
