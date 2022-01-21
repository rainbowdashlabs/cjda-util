package de.chojo.jdautil.wrapper;

import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
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

        UserChannelKey that = (UserChannelKey) o;

        if (channel != that.channel) return false;
        return user == that.user;
    }

    @Override
    public int hashCode() {
        int result = (int) (channel ^ (channel >>> 32));
        result = 31 * result + (int) (user ^ (user >>> 32));
        return result;
    }
}
