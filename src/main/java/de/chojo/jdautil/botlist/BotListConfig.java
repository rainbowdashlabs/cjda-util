package de.chojo.jdautil.botlist;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BotListConfig {

    /**
     * Name of the botlist
     */
    private final String name;

    /**
     * The token which is required to send stats to the botlist.
     */
    private final String statsToken;
    /**
     * The token which will be provided by the botlist when votes are beeing send
     */
    private final String voteToken;
    /**
     * The id of the guild of the botlist.
     */
    private final long guildId;

    /**
     * The link to the botlist profile of the bot
     */
    private final String profileUrl;

    /**
     * The link to the voting profile of the bot
     */
    private final String voteUrl;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public BotListConfig(@JsonProperty("name") String name, @JsonProperty("statsToken") String statsToken,
                         @JsonProperty("voteToken") String voteToken, @JsonProperty("guildId") long guildId,
                         @JsonProperty("profileUrl") String profileUrl, @JsonProperty("voteUrl") String voteUrl) {
        this.name = name;
        this.statsToken = statsToken;
        this.voteToken = voteToken;
        this.guildId = guildId;
        this.profileUrl = profileUrl;
        this.voteUrl = voteUrl;
    }

    public String name() {
        return name;
    }

    public String statsToken() {
        return statsToken;
    }

    public String voteToken() {
        return voteToken;
    }

    public long guildId() {
        return guildId;
    }

    public String profileUrl() {
        return profileUrl;
    }

    public String voteUrl() {
        return voteUrl;
    }
}
