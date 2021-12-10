package de.chojo.jdautil.botlist.modules.submission;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.chojo.jdautil.botlist.BotListData;
import de.chojo.jdautil.botlist.BotlistService;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class SubmissionService {
    private static final Logger log = getLogger(SubmissionService.class);
    private final BotlistService botlistService;
    private final ShardManager shardManager;

    public SubmissionService(BotlistService botlistService, ShardManager shardManager) {
        this.botlistService = botlistService;
        this.shardManager = shardManager;
    }

    public void submitData() {
        var data = BotListData.of(shardManager);
        for (var botlist : botlistService.botlists()) {
            try {
                botlist.report(data);
            } catch (JsonProcessingException e) {
                log.error("Could not build stats", e);
            }
        }
    }
}
