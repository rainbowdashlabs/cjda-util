package de.chojo.jdautil.command.message;

import de.chojo.jdautil.command.base.CommandDataProvider;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.localization.util.Language;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class MessageMeta implements CommandDataProvider {

    private final String name;
    private final boolean guildOnly;
    private final DefaultMemberPermissions defaultMemberPermissions;

    public MessageMeta(String name, boolean guildOnly, DefaultMemberPermissions defaultMemberPermissions) {
        this.name = name;
        this.guildOnly = guildOnly;
        this.defaultMemberPermissions = defaultMemberPermissions;
    }

    @Override
    public CommandData toCommandData(ILocalizer localizer, Language lang) {
        return Commands.message("")
                .setGuildOnly(guildOnly)
                .setDefaultPermissions(defaultMemberPermissions);
    }
}
