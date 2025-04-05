/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.message;

import de.chojo.jdautil.interactions.base.CommandDataProvider;
import de.chojo.jdautil.interactions.base.Interaction;
import de.chojo.jdautil.interactions.base.InteractionMeta;
import de.chojo.jdautil.interactions.message.builder.MessageBuilder;
import de.chojo.jdautil.interactions.message.builder.PartialMessageBuilder;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.Locale;

import static de.chojo.jdautil.util.Premium.isNotEntitled;
import static de.chojo.jdautil.util.Premium.replyPremium;

public class Message implements Interaction, MessageHandler, CommandDataProvider {
    private final InteractionMeta meta;
    private final MessageHandler handler;

    public Message(InteractionMeta meta, MessageHandler handler) {
        this.meta = meta;
        this.handler = handler;
    }

    public static PartialMessageBuilder of(String name) {
        return MessageBuilder.of(name);
    }

    @Override
    public InteractionMeta meta() {
        return meta;
    }

    @Override
    public void onMessage(MessageContextInteractionEvent event, EventContext context) {
        if (isNotEntitled(event, meta)) {
            replyPremium(event, context, meta);
            return;
        }

        handler.onMessage(event, context);
    }

    @Override
    public CommandData toCommandData(ILocalizer localizer) {
        var message = Commands.message(meta.name())
                              .setContexts(meta.contextTypes())
                              .setDefaultPermissions(meta.permission());
        if (meta.localized()) {
            message.setLocalizationFunction(localizer.prefixedLocalizer("message"));
        }
        return message;
    }

    private String localeKey() {
        return meta.name().toLowerCase(Locale.ROOT).replace(" ", "_") + ".name";
    }
}
