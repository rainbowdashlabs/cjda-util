package de.chojo.jdautil.botlist;

import de.chojo.jdautil.botlist.handler.StatusCodeHandler;
import de.chojo.jdautil.container.Pair;

import java.util.HashMap;

/**
 * Factory to provide botlist settings for most popular botlists.
 */
public interface BotListFactory {
    /**
     * Build a top.gg botlist
     */
    BotListFactory TOP_GG = key -> new BotList("top.gg", "https://top.gg/api/bots/{ID}/stats",
            Pair.of("Authorization", key),
            StatusCodeHandler.defaultHandler(),
            shardManager -> new HashMap<>() {{
                put("server_count", shardManager.getGuilds().size());
            }});

    /**
     * Build a discord.bots.gg botlist
     */
    BotListFactory DISCORD_BOTS_GG = key -> new BotList("discord.bots.gg", "https://discord.bots.gg/api/v1/bots/{ID}/stats",
            Pair.of("Authorization", key),
            StatusCodeHandler.defaultHandler(),
            shardManager -> new HashMap<>() {{
                put("guildCount", shardManager.getGuilds().size());
                put("shardCount", shardManager.getShards().size());
            }});

    /**
     * Build a discordbotlist.com botlist
     */
    BotListFactory DISCORD_BOT_LIST_COM = key -> new BotList("discordbotlist.com", "https://discordbotlist.com/api/v1/bots/{ID}/stats",
            Pair.of("Authorization", key),
            StatusCodeHandler.defaultHandler(),
            shardManager -> new HashMap<>() {{
                put("guilds", shardManager.getGuilds().size());
                put("users", shardManager.getUsers().size());
            }});

    /**
     * Build the botlist settings
     *
     * @param key auth key
     * @return new botlist instance
     */
    BotList build(String key);
}
