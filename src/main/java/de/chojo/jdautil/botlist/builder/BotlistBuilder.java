/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist.builder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.chojo.jdautil.botlist.BotList;
import de.chojo.jdautil.botlist.builder.stage.AuthStage;
import de.chojo.jdautil.botlist.builder.stage.BaseUrlStage;
import de.chojo.jdautil.botlist.builder.stage.Buildable;
import de.chojo.jdautil.botlist.builder.stage.ClientStage;
import de.chojo.jdautil.botlist.builder.stage.ConfigurationStage;
import de.chojo.jdautil.botlist.modules.shared.AuthHandler;
import de.chojo.jdautil.botlist.modules.shared.StatusCodeHandler;
import de.chojo.jdautil.botlist.modules.submission.StatsMapper;
import de.chojo.jdautil.botlist.modules.voting.poll.VoteChecker;
import de.chojo.jdautil.botlist.modules.voting.post.VoteReceiver;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.net.http.HttpClient;

public class BotlistBuilder implements ClientStage, BaseUrlStage, AuthStage, ConfigurationStage, Buildable {
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper()
            .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    public static final HttpClient DEFAULT_CLIENT = HttpClient.newHttpClient();
    private ObjectMapper objectMapper = DEFAULT_MAPPER;
    private HttpClient httpClient = DEFAULT_CLIENT;
    private long clientId;
    private final String name;
    private String baseUrl;
    private StatusCodeHandler statusCodeHandler = StatusCodeHandler.defaultHandler();
    private AuthHandler authHandler;
    private StatsMapper statsMapper;
    private VoteChecker voteChecker;
    private VoteReceiver<?> voteReceiver;
    private boolean shardStats = true;


    private BotlistBuilder(String name) {
        this.name = name;
    }

    public static ClientStage builder(String name) {
        return new BotlistBuilder(name);
    }

    public BotlistBuilder useShardStats(boolean shardStats){
        this.shardStats = shardStats;
        return this;
    }

    @Override
    public BotlistBuilder forClient(ShardManager shardManager) {
        this.clientId = shardManager.getShardById(0).getSelfUser().getApplicationIdLong();
        return this;
    }

    @Override
    public BotlistBuilder withBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    @Override
    public BotlistBuilder withAuthentication(AuthHandler authHandler) {
        this.authHandler = authHandler;
        return this;
    }

    @Override
    public BotlistBuilder withSubmission(StatsMapper statsMapper) {
        this.statsMapper = statsMapper;
        return this;
    }

    @Override
    public BotlistBuilder withVoteChecker(VoteChecker voteChecker) {
        this.voteChecker = voteChecker;
        return this;
    }

    @Override
    public BotlistBuilder withHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    @Override
    public BotlistBuilder withObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        return this;
    }

    @Override
    public BotlistBuilder withStatusCodeHandler(StatusCodeHandler statusCodeHandler) {
        this.statusCodeHandler = statusCodeHandler;
        return this;
    }

    @Override
    public ConfigurationStage withVoteReceiver(VoteReceiver<?> voteReceiver) {
        this.voteReceiver = voteReceiver;
        return this;
    }

    @Override
    public BotList build() {
        return new BotList(objectMapper, httpClient, clientId, name, baseUrl, statusCodeHandler, authHandler, statsMapper, voteChecker, voteReceiver, shardStats);
    }
}
