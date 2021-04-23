package de.chojo.jdautil.wrapper;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.Objects;

public class ChannelLocation {
    private final long guild;
    private final long channel;
    private final long user;

    public ChannelLocation(Guild guild, MessageChannel channel, User user) {
        this.guild = guild.getIdLong();
        this.channel = channel.getIdLong();
        this.user = user.getIdLong();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (ChannelLocation) o;
        return guild == that.guild && channel == that.channel && user == that.user;
    }

    @Override
    public int hashCode() {
        return Objects.hash(guild, channel, user);
    }
}
