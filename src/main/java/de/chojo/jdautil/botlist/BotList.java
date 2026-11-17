/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.chojo.jdautil.botlist.builder.BotlistBuilder;
import de.chojo.jdautil.botlist.builder.stage.ClientStage;
import de.chojo.jdautil.botlist.modules.shared.AuthHandler;
import de.chojo.jdautil.botlist.modules.shared.RouteProvider;
import de.chojo.jdautil.botlist.modules.shared.StatusCodeHandler;
import de.chojo.jdautil.botlist.modules.submission.StatsMapper;
import de.chojo.jdautil.botlist.modules.voting.poll.VoteChecker;
import de.chojo.jdautil.botlist.modules.voting.post.VoteReceiver;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Class which represents a discord botlist entry.
 */
public class BotList {
    private static final Logger log = getLogger(BotList.class);
    private final ObjectMapper mapper;
    private final HttpClient httpClient;
    private final String baseUrl;
    private final String name;
    private final StatusCodeHandler statusCodeHandler;
    private final long clientId;
    private final AuthHandler authHandler;
    private final StatsMapper statsMapper;
    private final VoteChecker voteChecker;
    private final VoteReceiver<?> voteReceiver;

    private final boolean shardStats;

    public BotList(
            ObjectMapper objectMapper,
            HttpClient httpClient,
            long clientId,
            String name,
            String baseUrl,
            StatusCodeHandler statusCodeHandler,
            AuthHandler authHandler,
            StatsMapper statsMapper,
            VoteChecker voteChecker,
            VoteReceiver<?> voteReceiver,
            boolean shardStats) {
        this.mapper = objectMapper;
        this.httpClient = httpClient;
        this.clientId = clientId;
        this.name = name;
        this.baseUrl = baseUrl;
        this.statusCodeHandler = statusCodeHandler;
        this.authHandler = authHandler;
        this.statsMapper = statsMapper;
        this.voteChecker = voteChecker;
        this.voteReceiver = voteReceiver;
        this.shardStats = shardStats;
    }

    /**
     * Report statistics to the botlist. Lists with shard stats get one request per shard, all others the total.
     *
     * @param total  statistics of the whole bot
     * @param shards statistics per shard
     * @throws JsonProcessingException if the payload could not be parsed
     */
    public void report(BotListData total, List<BotListData> shards) throws JsonProcessingException {
        if (statsMapper == null) return;

        log.debug("Sending Server stats to {}.", name);

        if (shardStats && !shards.isEmpty()) {
            for (var shard : shards) {
                request(buildStatsRequest(statsMapper.data(shard)));
            }
            return;
        }
        request(buildStatsRequest(statsMapper.data(total)));
    }

    /**
     * Sends an authenticated GET request to a route of the bot list. A 404 response is returned without being logged,
     * since vote checks answer "no vote" with it.
     *
     * @param route route relative to the base url
     * @return the response, or empty when the request failed
     */
    public Optional<HttpResponse<String>> get(String route) {
        return send(buildGET(URI.create(baseUrl + route)).build()).map(response -> {
            if (response.statusCode() != 404) statusCodeHandler.handle(this, response);
            return response;
        });
    }

    public URI getUrl(RouteProvider provider) {
        return URI.create(baseUrl + provider.route(clientId));
    }

    public boolean hasVoted(User user) {
        if (voteChecker == null) return false;
        return voteChecker.hasVoted(this, user);
    }

    public long clientId() {
        return clientId;
    }

    public ObjectMapper mapper() {
        return mapper;
    }

    private Optional<HttpResponse<String>> request(HttpRequest request) {
        return send(request).map(response -> {
            statusCodeHandler.handle(this, response);
            return response;
        });
    }

    private Optional<HttpResponse<String>> send(HttpRequest request) {
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            log.warn("Request to {}!", name, e);
            return Optional.empty();
        }
        return Optional.of(response);
    }

    public String name() {
        return name;
    }

    public String baseUrl() {
        return baseUrl;
    }

    private HttpRequest buildStatsRequest(Map<String, Object> data) throws JsonProcessingException {
        var builder = HttpRequest.newBuilder()
                .method(statsMapper.method(), HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(data)))
                .uri(getUrl(statsMapper))
                .header("Content-Type", "application/json")
                .header("User-Agent", "cjda-util");
        return authHandler.auth(builder).build();
    }

    private HttpRequest.Builder buildGET(URI uri) {
        var builder = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("User-Agent", "cjda-util");
        return authHandler.auth(builder);
    }

    public static ClientStage builder(String name) {
        return BotlistBuilder.builder(name);
    }

    public VoteReceiver<?> voteReceiver() {
        return voteReceiver;
    }

    public StatsMapper statsMapper() {
        return statsMapper;
    }

    public VoteChecker voteChecker() {
        return voteChecker;
    }

    public boolean isShardStats() {
        return shardStats;
    }
}
