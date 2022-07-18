/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command.dispatching;

import de.chojo.jdautil.command.base.Interaction;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;

public class InteractionContext {
    private Interaction interaction;
    private String args;
    private Guild guild;
    private MessageChannel channel;

    public InteractionContext(Interaction interaction, String args, Guild guild, MessageChannel channel) {
        this.interaction = interaction;
        this.args = args;
        this.guild = guild;
        this.channel = channel;
    }

    public Interaction interaction() {
        return interaction;
    }

    public String args() {
        return args;
    }

    public Guild guild() {
        return guild;
    }

    public MessageChannel channel() {
        return channel;
    }
}
