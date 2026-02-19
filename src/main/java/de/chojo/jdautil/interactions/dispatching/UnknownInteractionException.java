/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.dispatching;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;

public class UnknownInteractionException extends RuntimeException {
    public UnknownInteractionException(Guild guild, User user, Channel channel, String interaction) {
        super("Unknown interaction used on %s by %s in %s. Could not find any match for: %s".formatted(guild, user, channel, interaction));
    }
}
