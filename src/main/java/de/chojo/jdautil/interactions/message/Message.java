/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.message;

import de.chojo.jdautil.interactions.base.CommandDataProvider;
import de.chojo.jdautil.interactions.base.Interaction;
import de.chojo.jdautil.interactions.base.Meta;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class Message implements Interaction, MessageHandler, CommandDataProvider {
    private final MessageMeta meta;
    private final MessageHandler handler;

    public Message(MessageMeta meta, MessageHandler handler) {
        this.meta = meta;
        this.handler = handler;
    }

    @Override
    public Meta meta() {
        return meta;
    }

    @Override
    public void onMessage(MessageContextInteractionEvent event, EventContext context) {
        handler.onMessage(event, context);
    }

    @Override
    public CommandData toCommandData(ILocalizer localizer) {
        return Commands.message(meta.name())
                .setGuildOnly(meta.isGuildOnly())
                .setDefaultPermissions(meta.permission())
                .setLocalizationFunction(localizer::localizationMap);
    }
}
