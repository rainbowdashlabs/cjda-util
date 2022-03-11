/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist.modules.voting.post;

import de.chojo.jdautil.botlist.modules.shared.AuthHandler;
import de.chojo.jdautil.botlist.modules.voting.post.payload.VoteDataAdapter;
import io.javalin.http.Context;

public abstract class VoteReceiver<T> {
    private final Class<T> payload;
    private final AuthHandler auth;

    public VoteReceiver(Class<T> payload, AuthHandler auth) {
        this.payload = payload;
        this.auth = auth;
    }

    public boolean isAuthorized(Context key) {
        return auth.token().equals(key.header(auth.name()));
    }

    public AuthHandler auth() {
        return auth;
    }

    private T map(Object obj) {
        return (T) obj;
    }

    public final VoteData mapToVoteData(Object data) {
        return toVoteData(map(data));
    }

    public abstract VoteData toVoteData(T data);

    public Class<T> payload() {
        return payload;
    }

    public static <T extends VoteDataAdapter> VoteReceiver<T> create(Class<T> clazz, AuthHandler authHandler) {
        return new VoteReceiver<T>(clazz, authHandler) {
            @Override
            public VoteData toVoteData(T data) {
                return data.toVotaData();
            }
        };
    }
}
