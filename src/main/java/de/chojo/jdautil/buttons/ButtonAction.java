/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.buttons;

import de.chojo.jdautil.localization.ILocalizer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class ButtonAction {
    private final MessageEmbed embed;
    @Nullable
    private final IReplyCallback callback;
    @Nullable
    private final MessageChannel channel;
    private final boolean ephemeral;
    @Nullable
    private final User user;
    private final List<ButtonEntry> buttons;
    @Nullable
    private final Guild guild;

    public ButtonAction(MessageEmbed embed, IReplyCallback callback, MessageChannel channel, Guild guild, boolean ephemeral, User user, List<ButtonEntry> buttons) {
        this.embed = embed;
        this.callback = callback;
        this.channel = channel;
        this.ephemeral = ephemeral;
        this.user = user;
        this.buttons = buttons;
        this.guild = guild;
    }

    public void send(ILocalizer localizer, long id) {
        var buttons = this.buttons.stream()
                .map(ButtonEntry::button)
                .map(button -> button.withLabel(localizer.localize(button.getLabel(), guild)))
                .map(button -> button.withId(String.format("%s:%s", id, button.getId())))
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

    public List<ButtonEntry> buttons() {
        return buttons;
    }

    public static ButtonActionBuilder forChannel(MessageEmbed embed, MessageChannel channel){
        return new ButtonActionBuilder(embed, channel);
    }

    public static ButtonActionBuilder forCallback(MessageEmbed embed, IReplyCallback callback){
        return new ButtonActionBuilder(embed, callback);
    }
}
