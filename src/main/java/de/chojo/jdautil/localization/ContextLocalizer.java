/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.chojo.jdautil.localization;

import de.chojo.jdautil.localization.util.Replacement;
import net.dv8tion.jda.api.entities.Guild;

public class ContextLocalizer {

    ILocalizer localizer;
    private final Guild guild;

    ContextLocalizer(ILocalizer localizer, Guild guild) {
        this.localizer = localizer;
        this.guild = guild;
    }

    public String localize(String message, Replacement... replacements) {
        return localizer.localize(message, guild, replacements);
    }

    public Guild guild() {
        return guild;
    }
}
