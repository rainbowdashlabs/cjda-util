/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command.message.builder;

import de.chojo.jdautil.command.message.Message;
import de.chojo.jdautil.command.message.MessageHandler;
import de.chojo.jdautil.command.message.MessageMeta;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

public class MessageBuilder implements PartialMessageBuilder {
    private final String name;
    private boolean guildOnly;
    private DefaultMemberPermissions permissions = DefaultMemberPermissions.ENABLED;
    private MessageHandler handler;

    private MessageBuilder(String name) {
        this.name = name;
    }

    public static PartialMessageBuilder of(String name) {
        return new MessageBuilder(name);
    }

    @Override
    public MessageBuilder handler(MessageHandler handler) {
        this.handler = handler;
        return this;
    }

    public MessageBuilder withPermission(Permission permission, Permission... permissions) {
        var collect = Arrays.stream(permissions).collect(Collectors.toCollection(HashSet::new));
        collect.add(permission);
        this.permissions = DefaultMemberPermissions.enabledFor(collect);
        return this;
    }

    public Message build() {
        return new Message(new MessageMeta(name, guildOnly, permissions), handler);
    }
}
