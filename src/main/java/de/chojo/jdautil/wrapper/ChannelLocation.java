package de.chojo.jdautil.wrapper;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.Objects;

public class ChannelLocation {
    private final long guild;
    private final long channel;
    private final long user;

    private ChannelLocation(Guild guild, MessageChannel channel, User user) {
        this.guild = guild.getIdLong();
        this.channel = channel.getIdLong();
        this.user = user.getIdLong();
    }

    public static ChannelLocation of(User user, TextChannel channel) {
        return new ChannelLocation(channel.getGuild(), channel, user);
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
