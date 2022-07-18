package de.chojo.jdautil.command.message;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

public class MessageMetaBuilder {
    private String name;
    private boolean guildOnly;
    private DefaultMemberPermissions permission;

    public MessageMetaBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public MessageMetaBuilder setGuildOnly(boolean guildOnly) {
        this.guildOnly = guildOnly;
        return this;
    }

    public MessageMetaBuilder withPermission(DefaultMemberPermissions permission) {
        this.permission = permission;
        return this;
    }

    public MessageMetaBuilder withPermission(Permission... permissions) {
        this.permission = DefaultMemberPermissions.enabledFor(permissions);
        return this;
    }


    public MessageMeta build() {
        return new MessageMeta(name, guildOnly, permission);
    }
}
