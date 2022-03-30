/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.localization;

import de.chojo.jdautil.localization.util.Replacement;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.Nullable;

public class ContextLocalizer {

    private final ILocalizer localizer;
    private final Guild guild;

    ContextLocalizer(ILocalizer localizer, Guild guild) {
        this.localizer = localizer;
        this.guild = guild;
    }

    /**
     * Localize a message in the given guild or private context.
     * @param message message
     * @param replacements replacements
     * @return localized message
     */
    public String localize(String message, Replacement... replacements) {
        return localizer.localize(message, guild, replacements);
    }

    /**
     * The wrapped localizer instance.
     * @return localizer
     */
    public ILocalizer localizer() {
        return localizer;
    }

    /**
     * The guild of the context localizer.
     * @return guild or null if context is a private channel
     */
    @Nullable
    public Guild guild() {
        return guild;
    }
}
