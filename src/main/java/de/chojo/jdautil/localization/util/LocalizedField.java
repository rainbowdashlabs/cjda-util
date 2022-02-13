/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.chojo.jdautil.localization.util;

import de.chojo.jdautil.localization.Localizer;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

public class LocalizedField {
    private final MessageEmbed.Field field;

    /**
     * Creates a localized field.
     *
     * @param title        title of the field
     * @param description  description of the field
     * @param inline       true if inline
     * @param eventWrapper message context for language detection
     */
    public LocalizedField(Localizer localizer, String title, String description, boolean inline, CommandInteraction eventWrapper) {
        field = new MessageEmbed.Field(localizer.localize(title, eventWrapper),
                localizer.localize(description, eventWrapper),
                inline);
    }

    /**
     * Get the localized field.
     *
     * @return localized field
     */
    public MessageEmbed.Field getField() {
        return field;
    }
}
