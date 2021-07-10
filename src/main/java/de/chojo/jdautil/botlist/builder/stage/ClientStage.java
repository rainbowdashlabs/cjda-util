package de.chojo.jdautil.botlist.builder.stage;

import de.chojo.jdautil.botlist.builder.BotlistBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

public interface ClientStage {
    BaseUrlStage forClient(ShardManager shardManager);
}
