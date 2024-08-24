/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.wrapper;

import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.User;

public class UserChannelKey {
    private final long channel;
    private final long user;

    private UserChannelKey(Channel channel, User user) {
        this.channel = channel.getIdLong();
        this.user = user.getIdLong();
    }

    public static UserChannelKey of(User user, Channel channel) {
        return new UserChannelKey(channel, user);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        var that = (UserChannelKey) o;

        if (channel != that.channel) return false;
        return user == that.user;
    }

    @Override
    public int hashCode() {
        var result = (int) (channel ^ (channel >>> 32));
        result = 31 * result + (int) (user ^ (user >>> 32));
        return result;
    }
}
