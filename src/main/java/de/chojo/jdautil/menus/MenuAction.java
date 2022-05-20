/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.menus;

import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.menus.entries.MenuEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class MenuAction {
    private final MessageEmbed embed;
    @Nullable
    private final IReplyCallback callback;
    @Nullable
    private final MessageChannel channel;
    private final boolean ephemeral;
    @Nullable
    private final User user;
    private final List<de.chojo.jdautil.menus.entries.MenuEntry<?,?>> components;
    @Nullable
    private final Guild guild;

    public MenuAction(MessageEmbed embed, IReplyCallback callback, MessageChannel channel, Guild guild, boolean ephemeral, User user, List<de.chojo.jdautil.menus.entries.MenuEntry<?,?>> components) {
        this.embed = embed;
        this.callback = callback;
        this.channel = channel;
        this.ephemeral = ephemeral;
        this.user = user;
        this.components = components;
        this.guild = guild;
    }

    public void send(ILocalizer localizer, long id) {
        var buttons = this.components.stream()
                .filter(MenuEntry::visible)
                .map(e -> e.component(id, localizer.getContextLocalizer(guild)))
                .collect(Collectors.toList());

        var rows = ActionRow.partitionOf(buttons);

        if (channel != null) {
            channel.sendMessageEmbeds(embed)
                    .setActionRows(rows)
                    .queue();
        }

        if (callback != null) {
            callback.replyEmbeds(embed)
                    .setEphemeral(ephemeral)
                    .addActionRows(rows)
                    .queue();
        }
    }

    public User user() {
        return user;
    }

    public List<MenuEntry<?,?>> components() {
        return components;
    }

    public static MenuActionBuilder forChannel(MessageEmbed embed, MessageChannel channel){
        return new MenuActionBuilder(embed, channel);
    }

    public static MenuActionBuilder forCallback(MessageEmbed embed, IReplyCallback callback){
        return new MenuActionBuilder(embed, callback);
    }

    public Guild guild() {
        return guild;
    }
}
