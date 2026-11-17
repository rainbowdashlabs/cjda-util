/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist.builder.stage;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.chojo.jdautil.botlist.modules.shared.StatusCodeHandler;
import de.chojo.jdautil.botlist.modules.submission.StatsMapper;
import de.chojo.jdautil.botlist.modules.voting.poll.VoteChecker;
import de.chojo.jdautil.botlist.modules.voting.post.VoteReceiver;

import java.net.http.HttpClient;

public interface ConfigurationStage extends Buildable {

    /**
     * Set the submission service
     *
     * @param statsMapper submission service
     * @return builder instance
     */
    ConfigurationStage withSubmission(StatsMapper statsMapper);

    /**
     * Sets the vote checker that asks the bot list whether a user voted. Prefer {@link #withVoteReceiver(VoteReceiver)}
     * to be notified of votes; the checker is for looking up a single user.
     *
     * @param voteChecker vote checker
     * @return builder instance
     */
    ConfigurationStage withVoteChecker(VoteChecker voteChecker);

    /**
     * Set the http client
     *
     * @param httpClient http client
     * @return builder instance
     */
    ConfigurationStage withHttpClient(HttpClient httpClient);

    /**
     * Set the object mapper
     *
     * @param objectMapper object mapper
     * @return builder instance
     */
    ConfigurationStage withObjectMapper(ObjectMapper objectMapper);

    /**
     * Set the status handlet
     *
     * @param statusCodeHandler status handler instance
     * @return builder instance
     */
    ConfigurationStage withStatusCodeHandler(StatusCodeHandler statusCodeHandler);

    /**
     * Set the vote receiver
     *
     * @param voteReceiver vote receiver instance
     * @return builder instance
     */
    ConfigurationStage withVoteReceiver(VoteReceiver<?> voteReceiver);

    /**
     * Report the stats per shard or a total for all connected shards. Only enable it when the list accepts per shard
     * stats and the stats mapper writes the shard id.
     *
     * @param shardStats true if stats should be reported per shard. Default {@code false}
     * @return builder instance
     */
    ConfigurationStage useShardStats(boolean shardStats);
}
