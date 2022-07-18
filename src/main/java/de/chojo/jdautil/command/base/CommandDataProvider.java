package de.chojo.jdautil.command.base;

import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.localization.util.Language;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface CommandDataProvider {
    CommandData toCommandData(ILocalizer localizer, Language lang);
}
