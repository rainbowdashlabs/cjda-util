package de.chojo.jdautil.botlist;

public class BotListConfig {
    private String name;
    private String baseUrl;
    private String statsToken;
    private String voteToken;
    private long guildId;
    private String profile;

    public String name() {
        return name;
    }

    public String baseUrl() {
        return baseUrl;
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

    public String profile() {
        return profile;
    }
}
