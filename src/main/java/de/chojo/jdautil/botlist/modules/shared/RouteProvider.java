/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist.modules.shared;

public abstract class RouteProvider {
    private String route;

    public RouteProvider() {
    }

    public RouteProvider(String route) {
        this.route = transformRoute(route);
    }

    private String transformRoute(String route) {
        if (route.startsWith("/")) {
            return route;
        }
        return "/" + route;
    }

    public String route(long id) {
        return route.replace("{id}", String.valueOf(id));
    }
}
