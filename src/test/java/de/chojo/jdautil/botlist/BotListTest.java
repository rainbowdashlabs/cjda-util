/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist;

import de.chojo.jdautil.botlist.modules.shared.AuthHandler;
import de.chojo.jdautil.botlist.modules.submission.StatsMapper;
import de.chojo.jdautil.botlist.modules.voting.poll.BotlistMeVoteCheck;
import de.chojo.jdautil.botlist.modules.voting.poll.DiscordbotlistComUpvotes;
import de.chojo.jdautil.botlist.modules.voting.poll.TopGgVoteCheck;
import de.chojo.jdautil.botlist.modules.voting.poll.VoteChecker;
import de.chojo.jdautil.botlist.modules.voting.poll.VotePayload;
import de.chojo.jdautil.botlist.modules.voting.poll.VotingCheckingService;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BotListTest {
    private static final long CLIENT = 42L;
    private static final long USER = 7L;
    private static final BotListConfig CONFIG = new BotListConfig("list", "stats", "vote", 0L, "", "");

    @Test
    void topGgUsesTheV1Api() {
        var botList = BotListFactory.TOP_GG.build(shardManager(), CONFIG);

        assertEquals(
                "https://top.gg/api/v1/projects/@me/metrics",
                botList.getUrl(botList.statsMapper()).toString());
        assertEquals("PATCH", botList.statsMapper().method());
        assertFalse(botList.isShardStats());
        assertNotNull(botList.voteChecker());
        assertNotNull(botList.voteReceiver());
    }

    @Test
    void botlistMeUsesTheApiHost() {
        var botList = BotListFactory.BOTLIST_ME.build(shardManager(), CONFIG);

        assertEquals(
                "https://api.botlist.me/api/v1/bots/42/stats",
                botList.getUrl(botList.statsMapper()).toString());
        assertNotNull(botList.voteChecker());
    }

    @Test
    void discordBotListSendsShardStats() {
        var botList = BotListFactory.DISCORDBOTLIST_COM.build(shardManager(), CONFIG);

        assertTrue(botList.isShardStats());
        assertEquals(
                Map.of("guilds", 10L, "users", 20L, "shard_id", 1),
                botList.statsMapper().data(new BotListData(10, 20, 1, 2)));
        assertEquals(Map.of("guilds", 10L, "users", 20L), botList.statsMapper().data(BotListData.total(10, 20, 2)));
    }

    @Test
    void customListsSendTheTotalByDefault() {
        var botList = BotList.builder("test")
                .forClient(shardManager())
                .withBaseUrl("https://example.com")
                .withAuthentication(AuthHandler.of("token"))
                .build();

        assertFalse(botList.isShardStats());
    }

    @Test
    void voteCheckRoutesOfTheFactories() throws Exception {
        assertEquals(
                "https://top.gg/api/v1/projects/@me/votes/7",
                checkedUri("https://top.gg/api/v1", BotListFactory.TOP_GG));
        assertEquals(
                "https://api.botlist.me/api/v1/bots/42/voted?userId=7",
                checkedUri("https://api.botlist.me", BotListFactory.BOTLIST_ME));
        assertEquals(
                "https://discordbotlist.com/api/v1/bots/42/upvotes",
                checkedUri("https://discordbotlist.com", BotListFactory.DISCORDBOTLIST_COM));
    }

    @Test
    void failedChecksCountAsNotVotedAndAreRetried() throws Exception {
        var client = Mockito.mock(HttpClient.class);
        when(client.send(any(), any())).thenThrow(new IOException("down"));
        var botList = custom(client, false, new VotingCheckingService<>("voted", BotlistMeVoteCheck.class));

        assertFalse(botList.hasVoted(user()));
        assertFalse(botList.hasVoted(user()));
        verify(client, times(2)).send(any(), any());
    }

    @Test
    void errorResponsesAndBrokenBodiesCountAsNotVoted() throws Exception {
        assertFalse(checker(client(503, "{\"hasVoted\": true}"), "voted", BotlistMeVoteCheck.class));
        assertFalse(checker(client(200, "null"), "voted", BotlistMeVoteCheck.class));
        assertFalse(checker(client(200, "{\"expires_at\": \"tomorrow\"}"), "votes", TopGgVoteCheck.class));
        assertFalse(checker(client(200, "{\"upvotes\": null}"), "upvotes", DiscordbotlistComUpvotes.class));
    }

    @Test
    void shardStatsSendOneRequestPerShard() throws Exception {
        var client = client(200, "{}");
        var botList = custom(client, true);

        botList.report(
                BotListData.total(30, 60, 2), List.of(new BotListData(10, 20, 0, 2), new BotListData(20, 40, 1, 2)));

        var requests = ArgumentCaptor.forClass(HttpRequest.class);
        verify(client, times(2)).send(requests.capture(), any());
        assertEquals("PATCH", requests.getValue().method());
        assertEquals(
                "Bearer token",
                requests.getValue().headers().firstValue("Authorization").orElseThrow());
    }

    @Test
    void totalStatsSendOneRequest() throws Exception {
        var client = client(200, "{}");
        var botList = custom(client, false);

        botList.report(
                BotListData.total(30, 60, 2), List.of(new BotListData(10, 20, 0, 2), new BotListData(20, 40, 1, 2)));

        verify(client, times(1)).send(any(), any());
    }

    @Test
    void topGgVoteIsActiveUntilItExpires() throws Exception {
        var active = "{\"expires_at\": \"%s\"}".formatted(OffsetDateTime.now().plusHours(1));
        var expired = "{\"expires_at\": \"%s\"}".formatted(OffsetDateTime.now().minusHours(1));

        assertTrue(checker(client(200, active), "votes/{user}", TopGgVoteCheck.class));
        assertFalse(checker(client(200, expired), "votes/{user}", TopGgVoteCheck.class));
        assertFalse(checker(client(404, "{}"), "votes/{user}", TopGgVoteCheck.class));
    }

    @Test
    void botlistMeVoteCheckReadsTheFlag() throws Exception {
        assertTrue(checker(client(200, "{\"hasVoted\": true}"), "voted?userId={user}", BotlistMeVoteCheck.class));
        assertFalse(checker(client(200, "{\"hasVoted\": false}"), "voted?userId={user}", BotlistMeVoteCheck.class));
    }

    @Test
    void discordBotListVoteCheckSearchesTheUpvotes() throws Exception {
        var body = "{\"upvotes\": [{\"user_id\": \"%d\", \"username\": \"x\"}], \"total\": 1}".formatted(USER);

        assertTrue(checker(client(200, body), "upvotes", DiscordbotlistComUpvotes.class));
        assertFalse(checker(client(200, "{\"upvotes\": [], \"total\": 0}"), "upvotes", DiscordbotlistComUpvotes.class));
    }

    @Test
    void voteChecksAreCached() throws Exception {
        var client = client(200, "{\"hasVoted\": true}");
        var botList = custom(client, false, new VotingCheckingService<>("voted", BotlistMeVoteCheck.class));
        var user = user();

        botList.hasVoted(user);
        botList.hasVoted(user);

        verify(client, times(1)).send(any(), any());
    }

    @Test
    void serviceWorksWithoutVoteService() {
        var service = BotlistService.build(shardManager(), () -> 1, () -> 1)
                .forBotlist(BotListFactory.TOP_GG.build(shardManager(), CONFIG))
                .withExecutorService(Mockito.mock(ScheduledExecutorService.class))
                .build();

        assertEquals(1, service.botlists().size());
    }

    private static String checkedUri(String baseUrl, BotListFactory factory) throws Exception {
        var client = client(404, "{}");
        var checker = factory.build(shardManager(), CONFIG).voteChecker();
        var botList = BotList.builder("test")
                .forClient(shardManager())
                .withBaseUrl(baseUrl)
                .withAuthentication(AuthHandler.of("token"))
                .withHttpClient(client)
                .withVoteChecker(checker)
                .build();

        botList.hasVoted(user());

        var request = ArgumentCaptor.forClass(HttpRequest.class);
        verify(client).send(request.capture(), any());
        return request.getValue().uri().toString();
    }

    private static <T extends VotePayload> boolean checker(HttpClient client, String route, Class<T> payload) {
        return custom(client, false, new VotingCheckingService<>(route, payload))
                .hasVoted(user());
    }

    private static BotList custom(HttpClient client, boolean shardStats) {
        return custom(client, shardStats, null);
    }

    private static BotList custom(HttpClient client, boolean shardStats, VoteChecker checker) {
        var builder = BotList.builder("test")
                .forClient(shardManager())
                .withBaseUrl("https://example.com")
                .withAuthentication(AuthHandler.bearer("token"))
                .withSubmission(StatsMapper.of("PATCH", "stats", (data, map) -> map.add("guilds", data.guilds())))
                .withHttpClient(client)
                .useShardStats(shardStats);
        if (checker != null) builder.withVoteChecker(checker);
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private static HttpClient client(int status, String body) throws IOException, InterruptedException {
        var client = Mockito.mock(HttpClient.class);
        HttpResponse<Object> response = Mockito.mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(status);
        when(response.body()).thenReturn(body);
        when(client.send(any(), any())).thenReturn(response);
        return client;
    }

    private static ShardManager shardManager() {
        var shardManager = Mockito.mock(ShardManager.class, Mockito.RETURNS_DEEP_STUBS);
        when(shardManager.getShardById(0).getSelfUser().getApplicationIdLong()).thenReturn(CLIENT);
        return shardManager;
    }

    private static User user() {
        var user = Mockito.mock(User.class);
        when(user.getIdLong()).thenReturn(USER);
        when(user.getId()).thenReturn(String.valueOf(USER));
        return user;
    }
}
