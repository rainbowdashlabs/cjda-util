/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.menus;

import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.localization.util.LocaleProvider;
import de.chojo.jdautil.menus.entries.MenuEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class MenuAction {
    private final MessageCreateData message;
    @Nullable
    private final IReplyCallback callback;
    @Nullable
    private final MessageChannel channel;
    private final boolean ephemeral;
    @Nullable
    private final User user;
    private final List<de.chojo.jdautil.menus.entries.MenuEntry<?, ?>> components;
    @Nullable
    private final Guild guild;

    public MenuAction(MessageCreateData message, @Nullable IReplyCallback callback, @Nullable MessageChannel channel, Guild guild, boolean ephemeral, User user, List<de.chojo.jdautil.menus.entries.MenuEntry<?, ?>> components) {
        this.message = message;
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
                .map(e -> e.component(id, localizer.context(LocaleProvider.guild(guild))))
                .collect(Collectors.toList());

        var rows = ActionRow.partitionOf(buttons);

        if (channel != null) {
            channel.sendMessage(message)
                    .addComponents(rows)
                    .queue();
        }

        if (callback != null) {
            if (!callback.isAcknowledged()) {
                callback.deferReply(ephemeral).complete();
            }
            callback.getHook()
                    .editOriginal(MessageEditData.fromCreateData(message))
                    .setComponents(rows)
                    .queue();
        }
    }

    public User user() {
        return user;
    }

    public List<MenuEntry<?, ?>> components() {
        return components;
    }

    /**
     * Create a new action for a callback
     *
     * @param embed   message to send
     * @param channel the channel to send the message to
     * @return new action builder
     */
    public static MenuActionBuilder forChannel(MessageEmbed embed, MessageChannel channel) {
        return new MenuActionBuilder(MessageCreateData.fromEmbeds(embed), channel);
    }

    /**
     * Create a new action for a callback
     *
     * @param message message to send
     * @param channel the channel to send the message to
     * @return new action builder
     */
    public static MenuActionBuilder forChannel(String message, MessageChannel channel) {
        return new MenuActionBuilder(MessageCreateData.fromContent(message), channel);
    }

    /**
     * Create a new action for a callback
     *
     * @param embed    message to send
     * @param callback the callback to respond to
     * @return new action builder
     */
    public static MenuActionBuilder forCallback(MessageEmbed embed, IReplyCallback callback) {
        return new MenuActionBuilder(MessageCreateData.fromEmbeds(embed), callback);
    }

    /**
     * Create a new action for a callback
     *
     * @param message  message to send
     * @param callback the callback to respond to
     * @return new action builder
     */
    public static MenuActionBuilder forCallback(String message, IReplyCallback callback) {
        return new MenuActionBuilder(MessageCreateData.fromContent(message), callback);
    }
    /**
     * Create a new action for a callback
     *
     * @param message  message to send
     * @param callback the callback to respond to
     * @return new action builder
     */
    public static MenuActionBuilder forCallback(MessageCreateData message, IReplyCallback callback) {
        return new MenuActionBuilder(message, callback);
    }

    public Guild guild() {
        return guild;
    }
}
