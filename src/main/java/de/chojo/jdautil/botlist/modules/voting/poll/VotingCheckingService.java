/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist.modules.voting.poll;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.chojo.jdautil.botlist.BotList;
import de.chojo.jdautil.botlist.modules.shared.RouteProvider;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Checks votes by calling a vote check endpoint of the bot list.
 * <p>
 * Answers are cached per list and user for one minute, so a busy bot stays below the rate limits of the lists. Failed
 * requests and error responses count as "not voted" and are not cached.
 *
 * @param <T> type of the response
 */
public class VotingCheckingService<T extends VotePayload> extends RouteProvider implements VoteChecker {
    private static final Logger log = getLogger(VotingCheckingService.class);
    private final Class<T> response;
    private final Cache<String, Boolean> cache =
            CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();

    /**
     * Creates a new vote checker.
     *
     * @param route    route relative to the base url. {id} is replaced with the application id, {user} with the user id.
     * @param response type the response body is read into
     */
    public VotingCheckingService(String route, Class<T> response) {
        super(route);
        this.response = response;
    }

    @Override
    public boolean hasVoted(BotList botList, User user) {
        var key = botList.name() + ":" + user.getIdLong();
        var cached = cache.getIfPresent(key);
        if (cached != null) return cached;
        try {
            var result = request(botList, user);
            result.ifPresent(voted -> cache.put(key, voted));
            return result.orElse(false);
        } catch (JsonProcessingException | RuntimeException e) {
            log.warn("Could not check the vote of {} on {}.", user.getIdLong(), botList.name(), e);
            return false;
        }
    }

    private Optional<Boolean> request(BotList botList, User user) throws JsonProcessingException {
        var route = route(botList.clientId()).replace("{user}", user.getId());
        var optResponse = botList.get(route);
        if (optResponse.isEmpty()) return Optional.empty();
        var httpResponse = optResponse.get();
        if (httpResponse.statusCode() == 404) return Optional.of(false);
        if (httpResponse.statusCode() / 100 != 2) return Optional.empty();
        T payload = botList.mapper()
                .readerFor(response)
                .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue(httpResponse.body());
        return Optional.of(payload != null && payload.hasVoted(user));
    }
}
