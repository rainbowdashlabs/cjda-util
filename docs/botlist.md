---
title: Bot lists
sidebar_position: 8
---

# Bot lists

The `BotlistService` posts your server count to bot list sites, receives their vote webhooks and asks them whether a
user voted.

## Supported lists

| List | Server count | Vote webhook route | Vote check |
|---|---|---|---|
| top.gg | total and shard count | `voting/topgg` | yes |
| discordbotlist.com | per shard | `voting/discordbotlistcom` | the last 500 votes |
| botlist.me | total and shard count | `voting/botlistme` | yes |
| discord.bots.gg | total | none | no |

top.gg uses its API v1. The `statsToken` for top.gg is a v1 project token from your bot's settings on top.gg.

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

| Method | Default | What it does |
|---|---|---|
| `withSubmitInterval(interval, unit)` | 60 minutes | How often the counts are posted. The first post happens after one interval unit. |
| `withExecutorService(executor)` | a single thread | Where the posts run. |
| `withVoteService(...)` | none | Receives vote webhooks. Without it, votes are not received. |

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

## Check a vote

`hasVoted(user)` asks every list with a vote check and returns the lists the user has an active vote on:

```java
List<BotList> voted = botlistService.hasVoted(user);
```

Answers are kept for a minute per list and user. A list that cannot be reached counts as "not voted". Prefer the
webhooks to react to votes; the check is for looking up a single user.

## Add another list

`BotList.builder(name)` builds a list of your own:

```java
BotList myList = BotList.builder("mylist.example")
        .forClient(shardManager)
        .withBaseUrl("https://mylist.example/api")
        .withAuthentication(AuthHandler.of(statsToken))
        .withSubmission(StatsMapper.of("bots/{id}/stats", (data, map) -> map.add("servers", data.guilds())))
        .withVoteReceiver(VoteReceiver.create(MyVotePayload.class, AuthHandler.of(voteToken)))
        .withVoteChecker(new VotingCheckingService<>("bots/{id}/votes/{user}", MyVoteCheck.class))
        .build();
```

`{id}` is replaced with your application id and `{user}` with the id of the user to check. Register the list with
`forBotlist(myList)`.

| Part | What it does |
|---|---|
| `AuthHandler.of(token)` | Sends the token in the `Authorization` header. `AuthHandler.bearer(token)` adds `Bearer `. |
| `StatsMapper.of(route, mapper)` | Posts the stats. `StatsMapper.of("PATCH", route, mapper)` uses another HTTP method. |
| `useShardStats(true)` | Sends one request per shard. Write `data.shardId()` into the body when you use it. The default is the total. |
| `VoteReceiver.create(payload, auth)` | Receives webhooks. The payload implements `VoteDataAdapter` and turns the body into a `VoteData`. Override `injectGuild(long)` to receive the `guildId` query parameter. |
| `VotingCheckingService` | Calls the vote check route. The response class implements `VotePayload`. |
