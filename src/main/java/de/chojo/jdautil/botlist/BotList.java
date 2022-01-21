/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
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

    public BotList(ObjectMapper objectMapper, HttpClient httpClient, long clientId, String name, String baseUrl,
                   StatusCodeHandler statusCodeHandler, AuthHandler authHandler, StatsMapper statsMapper,
                   VoteChecker voteChecker, VoteReceiver<?> voteReceiver) {
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
    }

    /**
     * Report statistics to the botlist
     *
     * @param shardManager shardmanager for statistics
     * @throws JsonProcessingException if the payload could not be parsed
     */
    public void report(BotListData shardManager) throws JsonProcessingException {
        if (statsMapper == null) return;

        log.debug("Sending Server stats to {}.", name);

        var request = buildPOST(getUrl(statsMapper), statsMapper.data(shardManager));

        request(request);
    }

    public URI getUrl(RouteProvider provider) {
        return URI.create(baseUrl + provider.route(clientId));
    }

    public boolean hasVoted(User user) {
        if (voteChecker == null) return false;
        return voteChecker.hasVoted(user);
    }

    private Optional<HttpResponse<String>> request(HttpRequest request) {
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            log.warn("Request to {}!", name, e);
            return Optional.empty();
        }
        statusCodeHandler.handle(this, response);
        return Optional.of(response);
    }

    public String name() {
        return name;
    }

    public String baseUrl() {
        return baseUrl;
    }

    private HttpRequest buildPOST(URI uri, Map<String, Object> data) throws JsonProcessingException {
        var builder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(data)))
                .uri(uri)
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

    public VoteChecker voteChecker() {
        return voteChecker;
    }
}
