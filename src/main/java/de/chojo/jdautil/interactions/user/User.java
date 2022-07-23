/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.user;

import de.chojo.jdautil.interactions.base.CommandDataProvider;
import de.chojo.jdautil.interactions.base.Interaction;
import de.chojo.jdautil.interactions.base.InteractionMeta;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class User implements Interaction, UserHandler, CommandDataProvider {
    private final InteractionMeta meta;
    private final UserHandler handler;

    public User(InteractionMeta meta, UserHandler handler) {
        this.meta = meta;
        this.handler = handler;
    }

    @Override
    public InteractionMeta meta() {
        return meta;
    }

    @Override
    public void onUser(UserContextInteractionEvent event, EventContext context) {
        handler.onUser(event, context);
    }

    @Override
    public CommandData toCommandData(ILocalizer localizer) {
        return Commands.user(meta.name())
                .setGuildOnly(meta.isGuildOnly())
                .setDefaultPermissions(meta.permission())
                .setLocalizationFunction(localizer.prefixedLocalizer("user"));
    }
}
