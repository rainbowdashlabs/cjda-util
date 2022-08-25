/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.localization.util;

import de.chojo.jdautil.localization.LocalizationContext;
import de.chojo.jdautil.localization.ILocalizer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.time.temporal.TemporalAccessor;

/**
 * Wrapper for auto localization of embeds.
 */
public class LocalizedEmbedBuilder extends EmbedBuilder {
    private static final Replacement[] NONE = new Replacement[0];
    private final LocalizationContext localizer;

    /**
     * Creates a new localized embed builder.
     *
     * @param event message context for guild and language detection
     */
    public LocalizedEmbedBuilder(ILocalizer localizer, CommandInteraction event) {
        this.localizer = localizer.context(LocaleProvider.guild(event));
    }

    public LocalizedEmbedBuilder(LocalizationContext localizer) {
        this.localizer = localizer;
    }

    public LocalizedEmbedBuilder(ILocalizer localizer, @Nullable Guild guild) {
        this.localizer = localizer.context(LocaleProvider.guild(guild));
    }

    private String localize(String message, Replacement... replacements) {
        return localizer.localize(message, replacements);
    }

    public LocalizedEmbedBuilder addField(@Nullable String name, @Nullable String value, boolean inline, Replacement... replacements) {
        super.addField(localize(name, replacements), localize(value, replacements), inline);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder addField(@Nullable String name, @Nullable String value, boolean inline) {
        return addField(name, value, inline, new Replacement[0]);
    }

    @Nonnull
    @Override
    @Deprecated
    public LocalizedEmbedBuilder addField(@Nullable MessageEmbed.Field field) {
        return addField(field.getName(), field.getValue(), field.isInline());
    }

    /**
     * Add a localized field.
     *
     * @param field localized field to add.
     * @return the builder after the field has been set
     */
    public LocalizedEmbedBuilder addField(LocalizedField field) {
        super.addField(field.getField());
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setTitle(@Nullable String title) {
        return setTitle(title, NONE);
    }

    @Nonnull
    public LocalizedEmbedBuilder setTitle(@Nullable String title, Replacement... replacements) {
        super.setTitle(localize(title, replacements));
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setTitle(@Nullable String title, @Nullable String url) {
        return setTitle(localize(title), url, NONE);
    }

    @Nonnull
    public LocalizedEmbedBuilder setTitle(@Nullable String title, @Nullable String url, Replacement... replacements) {
        super.setTitle(localize(title, replacements), url);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setFooter(@Nullable String text) {
        return setFooter(text, NONE);
    }

    @Nonnull
    public LocalizedEmbedBuilder setFooter(@Nullable String text, Replacement... replacements) {
        super.setFooter(localize(text, replacements));
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setFooter(@Nullable String text, @Nullable String iconUrl) {
        return setFooter(text, iconUrl, NONE);
    }

    @Nonnull
    public LocalizedEmbedBuilder setFooter(@Nullable String text, @Nullable String iconUrl, Replacement... replacements) {
        super.setFooter(localize(text, replacements), iconUrl);
        return this;
    }

    /**
     * Set the description with auto translation.
     *
     * @param text text to set
     * @return the builder after the description has been set
     */
    public LocalizedEmbedBuilder setDescription(String text) {
        return setDescription(text, NONE);
    }

    public LocalizedEmbedBuilder setDescription(String text, Replacement... replacements) {
        super.setDescription(localize(text, replacements));
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder appendDescription(@Nonnull CharSequence description) {
        return appendDescription(description.toString(), NONE);
    }

    @Nonnull
    public LocalizedEmbedBuilder appendDescription(@Nonnull CharSequence description, Replacement... replacements) {
        super.appendDescription(localize(description.toString(), replacements));
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder clear() {
        super.clear();
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setTimestamp(@Nullable TemporalAccessor temporal) {
        super.setTimestamp(temporal);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setColor(@Nullable Color color) {
        super.setColor(color);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setColor(int color) {
        super.setColor(color);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setThumbnail(@Nullable String url) {
        super.setThumbnail(url);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setImage(@Nullable String url) {
        super.setImage(url);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setAuthor(@Nullable String name) {
        return setAuthor(name, NONE);
    }

    @Nonnull
    public LocalizedEmbedBuilder setAuthor(@Nullable String name, Replacement... replacements) {
        super.setAuthor(localize(name, replacements));
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setAuthor(@Nullable String name, @Nullable String url) {
        return setAuthor(name, url, NONE);
    }

    @Nonnull
    public LocalizedEmbedBuilder setAuthor(@Nullable String name, @Nullable String url, Replacement... replacements) {
        super.setAuthor(localize(name, replacements), url);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setAuthor(@Nullable String name, @Nullable String url, @Nullable String iconUrl) {
        return setAuthor(name, url, iconUrl, NONE);
    }

    @Nonnull
    public LocalizedEmbedBuilder setAuthor(@Nullable String name, @Nullable String url, @Nullable String iconUrl, Replacement... replacements) {
        super.setAuthor(localize(name, replacements), url, iconUrl);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder addBlankField(boolean inline) {
        super.addBlankField(inline);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder clearFields() {
        super.clearFields();
        return this;
    }
}
