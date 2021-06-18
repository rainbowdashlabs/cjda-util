package de.chojo.jdautil.botlist;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.chojo.jdautil.botlist.handler.StatusCodeHandler;
import de.chojo.jdautil.container.Pair;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Class which represents a discord botlist entry.
 */
public class BotList {
    private static final Logger log = getLogger(BotList.class);
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private final String name;
    private final String submitUrl;
    private final Pair<String, String> auth;
    private final StatusCodeHandler statusCodeHandler;
    private final Function<ShardManager, Map<String, Object>> dataMapper;

    BotList(String name, String submitUrl, Pair<String, String> auth, StatusCodeHandler statusCodeHandler, Function<ShardManager, Map<String, Object>> dataMapper) {
        this.name = name;
        this.submitUrl = submitUrl;
        this.auth = auth;
        this.statusCodeHandler = statusCodeHandler;
        this.dataMapper = dataMapper;
    }

    public String getUrl(long id) {
        return submitUrl.replace("{ID}", String.valueOf(id));
    }

    public String payload(ShardManager shardManager) throws JsonProcessingException {
        return MAPPER.writeValueAsString(dataMapper.apply(shardManager));
    }

    /**
     * Report statistics to the botlist
     *
     * @param shardManager shardmanager for statistics
     * @throws JsonProcessingException if the payload could not be parsed
     */
    public void report(ShardManager shardManager) throws JsonProcessingException {
        log.debug("Sending Server stats to {}.", name);

        var request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(payload(shardManager)))
                .uri(URI.create(getUrl(shardManager.getShards().get(0).getSelfUser().getIdLong())))
                .header(auth.first, auth.second)
                .header("Content-Type", "application/json")
                .header("User-Agent", "cjda-util")
                .build();

        HttpResponse<String> response;
        try {
            response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            log.warn("Failed to send stats to {}!", name, e);
            return;
        }
        statusCodeHandler.handle(this, response);
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        var botList = (BotList) o;

        if (!Objects.equals(name, botList.name)) return false;
        return Objects.equals(submitUrl, botList.submitUrl);
    }

    @Override
    public int hashCode() {
        var result = name != null ? name.hashCode() : 0;
        result = 31 * result + (submitUrl != null ? submitUrl.hashCode() : 0);
        return result;
    }


}