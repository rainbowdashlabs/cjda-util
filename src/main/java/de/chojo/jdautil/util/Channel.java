/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.util;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.GuildMessageChannel;

import javax.annotation.Nullable;

public final class Channel {
    @Nullable
    public static Guild guildFromMessageChannel(MessageChannel channel) {
        return channel instanceof GuildMessageChannel ? ((GuildMessageChannel) channel).getGuild() : null;
    }
}
