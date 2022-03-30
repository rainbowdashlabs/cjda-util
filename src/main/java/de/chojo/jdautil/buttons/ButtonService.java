/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.buttons;

import com.google.common.cache.Cache;
import de.chojo.jdautil.localization.ILocalizer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ButtonService extends ListenerAdapter {
    private final Cache<Long, ButtonContainer> cache;
    private final ILocalizer localizer;

    public ButtonService(Cache<Long, ButtonContainer> cache, ILocalizer localizer) {
        this.cache = cache;
        this.localizer = localizer;
    }

    public static ButtonServiceBuilder builder(ShardManager shardManager) {
        return new ButtonServiceBuilder(shardManager);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        var buttonContainer = cache.getIfPresent(event.getMessageIdLong());
        if (buttonContainer == null || !buttonContainer.canInteract(event.getUser())) return;
        buttonContainer.invoke(event);
    }

    public void register(MessageEmbed embed, IReplyCallback event, @Nullable User user, ButtonEntry... entries) {
        var buttons = Arrays.stream(entries)
                .map(ButtonEntry::button)
                .map(button -> button.withLabel(localizer.localize(button.getLabel(), event.getGuild())))
                .collect(Collectors.toList());
        var rows = ActionRow.partitionOf(buttons);
        event.replyEmbeds(embed)
                .setEphemeral(false)
                .addActionRows(rows)
                .flatMap(InteractionHook::retrieveOriginal)
                .queue(mess -> cache.put(mess.getIdLong(), new ButtonContainer(List.of(entries), user)));
    }

    public void register(MessageEmbed embed, Guild guild, MessageChannel messageChannel, @Nullable User user, ButtonEntry... entries) {
        var buttons = Arrays.stream(entries)
                .map(ButtonEntry::button)
                .map(button -> button.withLabel(localizer.localize(button.getLabel(), guild)))
                .collect(Collectors.toList());
        var rows = ActionRow.partitionOf(buttons);
        messageChannel.sendMessageEmbeds(embed)
                .setActionRows(rows)
                .queue(mess -> cache.put(mess.getIdLong(), new ButtonContainer(List.of(entries), user)));
    }
}
