package de.chojo.jdautil.localization.util;

import de.chojo.jdautil.localization.Localizer;
import de.chojo.jdautil.wrapper.MessageEventWrapper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.time.temporal.TemporalAccessor;

/**
 * Wrapper for auto localization of embeds.
 */
public class LocalizedEmbedBuilder extends EmbedBuilder {
    private final MessageEventWrapper messageContext;
    private final Localizer localizer;

    /**
     * Creates a new localized embed builder.
     *
     * @param messageContext message context for guild and language detection
     */
    public LocalizedEmbedBuilder(Localizer localizer, MessageEventWrapper messageContext) {
        this.localizer = localizer;
        this.messageContext = messageContext != null ? messageContext : MessageEventWrapper.fake();
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder addField(@Nullable String name, @Nullable String value, boolean inline) {
        super.addField(localizer.localizeByWrapper(name, messageContext), localizer.localizeByWrapper(value, messageContext), inline);
        return this;
    }

    @Nonnull
    @Override
    @Deprecated
    public LocalizedEmbedBuilder addField(@Nullable MessageEmbed.Field field) {
        super.addField(field);
        return this;
    }

    /**
     * Add a localized field.
     *
     * @param field localized field to add.
     *
     * @return the builder after the field has been set
     */
    public LocalizedEmbedBuilder addField(LocalizedField field) {
        return addField(field.getField());
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setTitle(@Nullable String title) {
        super.setTitle(localizer.localizeByWrapper(title, messageContext));
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setTitle(@Nullable String title, @Nullable String url) {
        super.setTitle(localizer.localizeByWrapper(title, messageContext), url);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setFooter(@Nullable String text) {
        super.setFooter(localizer.localizeByWrapper(text, messageContext));
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setFooter(@Nullable String text, @Nullable String iconUrl) {
        super.setFooter(localizer.localizeByWrapper(text, messageContext), iconUrl);
        return this;
    }

    /**
     * Set the description with auto translation.
     *
     * @param text text to set
     *
     * @return the builder after the description has been set
     */
    public LocalizedEmbedBuilder setDescription(String text) {
        super.setDescription(localizer.localizeByWrapper(text, messageContext));
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder appendDescription(@Nonnull CharSequence description) {
        super.appendDescription(localizer.localizeByWrapper(description.toString(), messageContext));
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
        super.setAuthor(localizer.localizeByWrapper(name, messageContext));
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setAuthor(@Nullable String name, @Nullable String url) {
        super.setAuthor(localizer.localizeByWrapper(name, messageContext), url);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setAuthor(@Nullable String name, @Nullable String url, @Nullable String iconUrl) {
        super.setAuthor(localizer.localizeByWrapper(name, messageContext), url, iconUrl);
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
