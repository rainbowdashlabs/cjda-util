/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command.slash;

import de.chojo.jdautil.command.base.Interaction;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.localization.util.Language;
import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public abstract class Slash implements Interaction {
    private final SlashMeta meta;

    protected Slash(SlashMeta meta) {
        this.meta = meta;
    }

    protected Slash(SlashMetaBuilder meta) {
        this(meta.build());
    }

    public static SubSlashBuilder subCommandBuilder() {
        return new SubSlashBuilder();
    }

    public static ArgumentsBuilder argsBuilder() {
        return new ArgumentsBuilder();
    }

    @Override
    public SlashMeta meta() {
        return meta;
    }

    /**
     * @deprecated Replaced by {@link #meta()}
     */
    @Deprecated(forRemoval = true)
    public String command() {
        return meta.name();
    }

    /**
     * @deprecated Replaced by {@link #meta()}
     */
    @Deprecated(forRemoval = true)
    public String description() {
        return meta.description();
    }

    /**
     * @deprecated Replaced by {@link #meta()}
     */
    @Deprecated(forRemoval = true)
    public Argument[] args() {
        return meta.argument();
    }

    /**
     * @deprecated Replaced by {@link #meta()}
     */
    @Deprecated(forRemoval = true)
    public SubSlash[] subCommands() {
        return meta.subCommands();
    }

    /**
     * @deprecated Replaced by {@link #meta()}
     */
    @Deprecated(forRemoval = true)
    public SubSlash[] getSubCommands() {
        return meta.subCommands();
    }

    public abstract void onSlashCommand(SlashCommandInteractionEvent event, EventContext context);

    public void onAutoComplete(CommandAutoCompleteInteractionEvent event, EventContext eventContext) {

    }

    public CommandData getCommandData(ILocalizer localizer, Language lang) {
        return meta.toCommandData(localizer, lang);
    }

    protected Message wrap(MessageEmbed embed) {
        return new MessageBuilder(embed).build();
    }

    protected Message wrap(String message) {
        return new MessageBuilder(message).build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Slash)) return false;

        Slash that = (Slash) o;

        return meta.equals(that.meta);
    }

    @Override
    public int hashCode() {
        return meta.hashCode();
    }
}
