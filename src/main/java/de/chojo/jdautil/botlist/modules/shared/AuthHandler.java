/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist.modules.shared;

import java.net.http.HttpRequest;

public class AuthHandler {
    private String name;
    private String token;

    public AuthHandler() {
    }

    private AuthHandler(String name, String token) {
        this.name = name;
        this.token = token;
    }

    public static AuthHandler of(String token) {
        return new AuthHandler("Authorization", token);
    }

    public static AuthHandler of(String name, String token) {
        return new AuthHandler(name, token);
    }

    public String name() {
        return name;
    }

    public String token() {
        return token;
    }

    public HttpRequest.Builder auth(HttpRequest.Builder builder) {
        builder.header(name, token);
        return builder;
    }
}
