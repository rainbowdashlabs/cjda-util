/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */
/**
 * Utilities for Discord bots built on JDA: interactions, localization, menus, pagination, conversations and bot list
 * integration.
 */
module de.chojo.jdautil {
    requires transitive net.dv8tion.jda;
    requires transitive io.javalin;
    requires transitive com.fasterxml.jackson.databind;
    requires transitive com.google.common;
    requires transitive org.apache.commons.text;
    requires transitive java.desktop;
    requires transitive java.net.http;
    requires org.eclipse.jetty.http;
    requires org.slf4j;
    requires transitive org.jspecify;
    requires static org.jetbrains.annotations;

    exports de.chojo.jdautil.botlist;
    exports de.chojo.jdautil.botlist.builder;
    exports de.chojo.jdautil.botlist.builder.stage;
    exports de.chojo.jdautil.botlist.modules.shared;
    exports de.chojo.jdautil.botlist.modules.submission;
    exports de.chojo.jdautil.botlist.modules.voting;
    exports de.chojo.jdautil.botlist.modules.voting.poll;
    exports de.chojo.jdautil.botlist.modules.voting.post;
    exports de.chojo.jdautil.botlist.modules.voting.post.payload;
    exports de.chojo.jdautil.buffer;
    exports de.chojo.jdautil.configuration;
    exports de.chojo.jdautil.configuration.exception;
    exports de.chojo.jdautil.consumer;
    exports de.chojo.jdautil.container;
    exports de.chojo.jdautil.conversation;
    exports de.chojo.jdautil.conversation.builder;
    exports de.chojo.jdautil.conversation.elements;
    exports de.chojo.jdautil.eventmanager;
    exports de.chojo.jdautil.functions;
    exports de.chojo.jdautil.interactions.base;
    exports de.chojo.jdautil.interactions.dispatching;
    exports de.chojo.jdautil.interactions.locale;
    exports de.chojo.jdautil.interactions.message;
    exports de.chojo.jdautil.interactions.message.builder;
    exports de.chojo.jdautil.interactions.message.provider;
    exports de.chojo.jdautil.interactions.premium;
    exports de.chojo.jdautil.interactions.slash;
    exports de.chojo.jdautil.interactions.slash.provider;
    exports de.chojo.jdautil.interactions.slash.structure;
    exports de.chojo.jdautil.interactions.slash.structure.builder;
    exports de.chojo.jdautil.interactions.slash.structure.builder.argument;
    exports de.chojo.jdautil.interactions.slash.structure.builder.components;
    exports de.chojo.jdautil.interactions.slash.structure.handler;
    exports de.chojo.jdautil.interactions.slash.structure.meta;
    exports de.chojo.jdautil.interactions.user;
    exports de.chojo.jdautil.interactions.user.builder;
    exports de.chojo.jdautil.interactions.user.provider;
    exports de.chojo.jdautil.localization;
    exports de.chojo.jdautil.localization.util;
    exports de.chojo.jdautil.menus;
    exports de.chojo.jdautil.menus.entries;
    exports de.chojo.jdautil.modals.handler;
    exports de.chojo.jdautil.modals.service;
    exports de.chojo.jdautil.pagination;
    exports de.chojo.jdautil.pagination.bag;
    exports de.chojo.jdautil.pagination.exceptions;
    exports de.chojo.jdautil.parsing;
    exports de.chojo.jdautil.text;
    exports de.chojo.jdautil.threading;
    exports de.chojo.jdautil.util;
    exports de.chojo.jdautil.wrapper;

    opens de.chojo.jdautil.botlist to
            com.fasterxml.jackson.databind;
    opens de.chojo.jdautil.botlist.modules.voting.post.payload to
            com.fasterxml.jackson.databind;
    opens de.chojo.jdautil.configuration to
            com.fasterxml.jackson.databind;
    opens de.chojo.jdautil.localization.util to
            com.fasterxml.jackson.databind;
}
