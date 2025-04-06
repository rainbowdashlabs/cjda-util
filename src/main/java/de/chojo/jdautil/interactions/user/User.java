/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.user;

import de.chojo.jdautil.interactions.base.CommandDataProvider;
import de.chojo.jdautil.interactions.base.Interaction;
import de.chojo.jdautil.interactions.base.InteractionMeta;
import de.chojo.jdautil.interactions.user.builder.PartialUserBuilder;
import de.chojo.jdautil.interactions.user.builder.UserBuilder;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.Locale;

import static de.chojo.jdautil.util.Premium.isNotEntitled;
import static de.chojo.jdautil.util.Premium.replyPremium;

public class User implements Interaction, UserHandler, CommandDataProvider {
    private final InteractionMeta meta;
    private final UserHandler handler;

    public User(InteractionMeta meta, UserHandler handler) {
        this.meta = meta;
        this.handler = handler;
    }

    public static PartialUserBuilder of(String name) {
        return UserBuilder.of(name);
    }

    @Override
    public InteractionMeta meta() {
        return meta;
    }

    @Override
    public void onUser(UserContextInteractionEvent event, EventContext context) {
        if (isNotEntitled(event, meta)) {
            replyPremium(event, context, meta);
            return;
        }

        handler.onUser(event, context);
    }

    @Override
    public CommandData toCommandData(ILocalizer localizer) {
        var user = Commands.user(meta.name())
                           .setContexts(meta.context())
                           .setDefaultPermissions(meta.permission());
        if (meta.localized()) {
            user.setLocalizationFunction(localizer.prefixedLocalizer("user"));
        }
        return user;
    }

    private String localeKey() {
        return meta.name().toLowerCase(Locale.ROOT).replace(" ", "_") + ".name";
    }
}
