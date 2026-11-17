/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist.modules.submission;

import de.chojo.jdautil.botlist.BotListData;
import de.chojo.jdautil.botlist.modules.shared.RouteProvider;
import de.chojo.jdautil.util.MapBuilder;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Maps statistics to the request body of a bot list and names the route and HTTP method to send them with.
 */
public class StatsMapper extends RouteProvider implements ISubmissionService {
    private final String method;
    private final BiConsumer<BotListData, MapBuilder<String, Object>> mapper;

    private StatsMapper(String method, String route, BiConsumer<BotListData, MapBuilder<String, Object>> mapper) {
        super(route);
        this.method = method;
        this.mapper = mapper;
    }

    /**
     * Sends the statistics with a POST request.
     *
     * @param route  route relative to the base url. {id} is replaced with the application id.
     * @param mapper writes the statistics into the request body
     * @return stats mapper
     */
    public static StatsMapper of(String route, BiConsumer<BotListData, MapBuilder<String, Object>> mapper) {
        return of("POST", route, mapper);
    }

    /**
     * Sends the statistics with the given HTTP method.
     *
     * @param method HTTP method, for example POST or PATCH
     * @param route  route relative to the base url. {id} is replaced with the application id.
     * @param mapper writes the statistics into the request body
     * @return stats mapper
     */
    public static StatsMapper of(
            String method, String route, BiConsumer<BotListData, MapBuilder<String, Object>> mapper) {
        return new StatsMapper(method, route, mapper);
    }

    @Override
    public Map<String, Object> data(BotListData data) {
        var map = new MapBuilder<String, Object>();
        mapper.accept(data, map);
        return map.build();
    }

    public String method() {
        return method;
    }
}
