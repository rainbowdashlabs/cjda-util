/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.buttons;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ButtonActionBuilder {
    private MessageEmbed embed;
    @Nullable
    private IReplyCallback callback;
    @Nullable
    private MessageChannel channel;
    private boolean ephemeral;
    @Nullable
    private User user;
    private List<ButtonEntry> buttons = new ArrayList<>();
    @Nullable
    private Guild guild;

    ButtonActionBuilder(MessageEmbed embed, IReplyCallback callback) {
        this.embed = embed;
        this.callback = callback;
        this.guild = callback.getGuild();
    }

    ButtonActionBuilder(MessageEmbed embed, MessageChannel channel) {
        this.embed = embed;
        this.channel = channel;
        if (channel instanceof TextChannel textChannel) {
            guild = textChannel.getGuild();
        }
    }

    public ButtonActionBuilder asEphemeral() {
        this.ephemeral = true;
        return this;
    }

    public ButtonActionBuilder forUser(User user) {
        this.user = user;
        return this;
    }

    public ButtonActionBuilder addButton(ButtonEntry entry) {
        buttons.add(entry);
        return this;
    }

    public ButtonAction build() {
        return new ButtonAction(embed, callback, channel, guild, ephemeral, user, buttons);
    }
}
