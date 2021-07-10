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
     * @param voteChecker add a vote checker
     * @return builder instance
     * @deprecated Polling is not implemented yet. Also polling is quite bad. use the {@link #withVoteReceiver(VoteReceiver)} instead to receive a notification on votes.
     */
    @Deprecated
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
}
