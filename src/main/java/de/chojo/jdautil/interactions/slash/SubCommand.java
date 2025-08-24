/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash;

import de.chojo.jdautil.interactions.locale.LocaleChecks;
import de.chojo.jdautil.interactions.locale.LocaleKey;
import de.chojo.jdautil.interactions.slash.structure.Route;
import de.chojo.jdautil.interactions.slash.structure.builder.SubCommandBuilder;
import de.chojo.jdautil.interactions.slash.structure.builder.components.PartialSubCommandBuilder;
import de.chojo.jdautil.interactions.slash.structure.handler.SlashHandler;
import de.chojo.jdautil.interactions.slash.structure.meta.RouteMeta;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.localization.util.LocaleProvider;
import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;

import static de.chojo.jdautil.util.Premium.checkAndReplyPremium;
import static de.chojo.jdautil.util.Premium.isNotEntitled;
import static de.chojo.jdautil.util.Premium.replyPremium;

public class SubCommand implements Route<RouteMeta> {
    private final RouteMeta meta;
    private final SlashHandler handler;
    private final List<Argument> arguments;

    public SubCommand(RouteMeta meta, SlashHandler handler, List<Argument> arguments) {
        this.meta = meta;
        this.handler = handler;
        this.arguments = arguments;
    }

    public static SubCommandBuilder of(String name, String description, SlashHandler handler) {
        return SubCommandBuilder.full(name, description, handler);
    }

    public static PartialSubCommandBuilder of(String name, String description) {
        return SubCommandBuilder.partial(name, description);
    }

    public static SubCommandBuilder sub(String name, String description, SlashHandler handler) {
        return of(name, description, handler);
    }

    public static PartialSubCommandBuilder sub(String name, String description) {
        return of(name, description);
    }

    public RouteMeta meta() {
        return meta;
    }

    @Override
    public void onAutoComplete(CommandAutoCompleteInteractionEvent event, EventContext context) {
        if (checkAndReplyPremium(context, meta())) {
            event.replyChoices().queue();
            return;
        }

        handler.onAutoComplete(event, context);
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event, EventContext context) {
        if (checkAndReplyPremium(context, meta())) {
            return;
        }

        handler.onSlashCommand(event, context);
    }

    public SubcommandData data(Slash slash, Group group, ILocalizer localizer) {
        LocaleChecks.checkCommandName(localizer, "command", LocaleKey.name(slash.meta().name(), group.meta().name(), meta.name()));
        LocaleChecks.checkCommandDescription(localizer, "command", LocaleKey.description(slash.meta().name(), group.meta().name(), meta.name()));
        return new SubcommandData(meta.name(), localizer.localize(meta.description(), LocaleProvider.empty()))
                .addOptions(arguments.stream().map(a -> a.data(slash, group, this, localizer)).toList());
    }

    public SubcommandData data(Slash slash, ILocalizer localizer) {
        LocaleChecks.checkCommandName(localizer, "command", LocaleKey.name(slash.meta().name(), meta.name()));
        LocaleChecks.checkCommandDescription(localizer, "command", LocaleKey.description(slash.meta().name(), meta.name()));
        return new SubcommandData(meta.name(), localizer.localize(meta.description(), LocaleProvider.empty()))
                .addOptions(arguments.stream().map(a -> a.data(slash, this, localizer)).toList());
    }

    public SubcommandData data() {
        LocaleChecks.checkCommandName(meta.name());
        LocaleChecks.checkCommandDescription(meta.description());
        return new SubcommandData(meta.name(), meta.description())
                .addOptions(arguments.stream().map(Argument::data).toList());
    }
}
