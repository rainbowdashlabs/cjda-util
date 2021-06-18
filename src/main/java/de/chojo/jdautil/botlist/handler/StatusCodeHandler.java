package de.chojo.jdautil.botlist.handler;

import de.chojo.jdautil.botlist.BotList;
import org.slf4j.Logger;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static org.slf4j.LoggerFactory.getLogger;

public class StatusCodeHandler {
    private static final Logger log = getLogger(StatusCodeHandler.class);
    private static final Map<Integer, BiConsumer<BotList, HttpResponse<String>>> DEFAULT_HANDLER = new HashMap<>() {{
        put(200, (botList, response) -> success(botList));
        put(201, (botList, response) -> success(botList));
        put(202, (botList, response) -> success(botList));
        put(204, (botList, response) -> success(botList));
        put(401, (botList, response) -> failed(botList, response, "Invalid bot token."));
        put(403, (botList, response) -> failed(botList, response, "Can't access api."));
        put(404, (botList, response) -> failed(botList, response, "Bot is not registered or public."));
        put(418, (botList, response) -> failed(botList, response, "Botlist is a teapot."));
        put(429, (botList, response) -> failed(botList, response, "Ratelimited. Please lower refresh rate."));
        put(500, (botList, response) -> serviceFailed(botList, response, "Service encountered an error."));
        put(502, (botList, response) -> serviceFailed(botList, response, "Bad Gateway."));
        put(503, (botList, response) -> serviceFailed(botList, response, "Service is currently unavailable."));
    }};
    private static final BiConsumer<BotList, HttpResponse<String>> DEFAULT =
            (botList, response) -> log.warn("Unhandled status code for botlist {}\nStatus code: {}\n Body:\n{}",
                    botList.name(), response.statusCode(), response.body());
    private final Map<Integer, BiConsumer<BotList, HttpResponse<String>>> handler;

    private StatusCodeHandler(Map<Integer, BiConsumer<BotList, HttpResponse<String>>> defaultHandler) {
        handler = defaultHandler;
    }

    public void handle(BotList botList, HttpResponse<String> response) {
        handler.getOrDefault(response.statusCode(), DEFAULT).accept(botList, response);
    }

    public static StatusCodeHandler defaultHandler() {
        return new StatusCodeHandler(DEFAULT_HANDLER);
    }

    public static StatusCodeHandler create() {
        return new StatusCodeHandler(new HashMap<>());
    }

    private static void failed(BotList botList, HttpResponse<String> response, String message) {
        log.warn("Failed to send stats to {}\nStatus code: {}\n{}",
                botList.name(), response.statusCode(), message);
    }

    private static void serviceFailed(BotList botList, HttpResponse<String> response, String message) {
        log.debug("Failed to send stats to {}\nStatus code: {}\n{}",
                botList.name(), response.statusCode(), message);
    }

    private static void success(BotList botList) {
        log.trace("Stats send to {}!", botList.name());
    }
}
