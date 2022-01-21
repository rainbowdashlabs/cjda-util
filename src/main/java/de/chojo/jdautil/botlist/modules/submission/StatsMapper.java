/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.chojo.jdautil.botlist.modules.submission;

import de.chojo.jdautil.botlist.BotListData;
import de.chojo.jdautil.botlist.modules.shared.RouteProvider;
import de.chojo.jdautil.util.MapBuilder;

import java.util.Map;
import java.util.function.BiConsumer;

public class StatsMapper extends RouteProvider implements ISubmissionService {
    private BiConsumer<BotListData, MapBuilder<String, Object>> mapper;

    private StatsMapper(String route, BiConsumer<BotListData, MapBuilder<String, Object>> mapper) {
        super(route);
    }

    public static StatsMapper of(String route, BiConsumer<BotListData, MapBuilder<String, Object>> mapper) {
        return new StatsMapper(route, mapper);
    }

    @Override
    public Map<String, Object> data(BotListData data) {
        var map = new MapBuilder<String, Object>();
        mapper.accept(data, map);
        return map.build();
    }
}
