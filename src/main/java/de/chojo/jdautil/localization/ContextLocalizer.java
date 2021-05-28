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
}
