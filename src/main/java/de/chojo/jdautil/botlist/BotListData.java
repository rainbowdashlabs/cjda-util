package de.chojo.jdautil.botlist;

import net.dv8tion.jda.api.sharding.ShardManager;

public class BotListData {
    private final long guilds;
    private final long user;
    private final int shards;

    public BotListData(long guilds, long user, int shards) {
        this.guilds = guilds;
        this.user = user;
        this.shards = shards;
    }

    public static BotListData of(ShardManager shardManager) {
        var guilds = shardManager.getGuildCache().size();
        var user = shardManager.getUserCache().size();
        var shards = shardManager.getShards().size();
        return new BotListData(guilds, user, shards);
    }

    public long guilds() {
        return guilds;
    }

    public long user() {
        return user;
    }

    public int shards() {
        return shards;
    }
}
