/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.menus;

import de.chojo.jdautil.menus.entries.MenuEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MenuActionBuilder {
    private final MessageCreateData message;
    @Nullable
    private IReplyCallback callback;
    @Nullable
    private MessageChannel channel;
    private boolean ephemeral;
    @Nullable
    private User user;
    private final List<MenuEntry<?, ?>> components = new ArrayList<>();
    @Nullable
    private Guild guild;

    MenuActionBuilder(MessageCreateData message, IReplyCallback callback) {
        this.message = message;
        this.callback = callback;
        this.guild = callback.getGuild();
    }

    MenuActionBuilder(MessageCreateData message, MessageChannel channel) {
        this.message = message;
        this.channel = channel;
        if (channel instanceof TextChannel textChannel) {
            guild = textChannel.getGuild();
        }
    }

    public MenuActionBuilder asEphemeral() {
        this.ephemeral = true;
        return this;
    }

    public MenuActionBuilder forUser(User user) {
        this.user = user;
        return this;
    }

    public MenuActionBuilder addComponent(de.chojo.jdautil.menus.entries.MenuEntry<?, ?> entry) {
        components.add(entry);
        return this;
    }

    public MenuAction build() {
        return new MenuAction(message, callback, channel, guild, ephemeral, user, components);
    }
}
