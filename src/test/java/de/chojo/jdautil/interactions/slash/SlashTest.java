/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash;

import de.chojo.jdautil.interactions.slash.structure.handler.SlashHandler;
import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.when;

class SlashTest {

    @Test
    public void testBase() {
        var command = Slash.of("test", "test")
                           .command(throwingHandler()).build();

        var event = eventForPath("test");
        assertThrows(RuntimeException.class, () -> command.onSlashCommand(event, context(event)));
    }

    @Test
    public void testSub() {
        var command = Slash.of("test", "test")
                           .guildOnly()
                           .subCommand(SubCommand.of("test1", "test1")
                                                 .handler(throwingHandler()))
                           .subCommand(SubCommand.of("sub2", "sub2")
                                                 .handler(emptyHandler()))
                           .build();


        var sub1 = eventForPath("test", "test1");
        var sub2 = eventForPath("test", "sub2");

        assertThrows(RuntimeException.class, () -> command.onSlashCommand(sub1, context(sub1)));
        assertDoesNotThrow(() -> command.onSlashCommand(sub2, context(sub2)));
    }

    @Test
    public void testGroup() {
        var command = Slash.of("test", "test")
                           .group(Group.of("group1", "group1")
                                       .subCommand(SubCommand.of("sub1", "sub1")
                                                             .handler(throwingHandler()))
                                       .subCommand(SubCommand.of("sub2", "sub2")
                                                             .handler(emptyHandler())))
                           .group(Group.of("group2", "group2")
                                       .subCommand(SubCommand.of("sub1", "sub1")
                                                             .handler(throwingHandler()))
                                       .subCommand(SubCommand.of("sub2", "sub2")
                                                             .handler(emptyHandler())))
                           .build();


        var group1sub1 = eventForPath("test", "group1", "sub1");
        var group1sub2 = eventForPath("test", "group1", "sub2");
        var group2sub1 = eventForPath("test", "group2", "sub1");

        assertThrowsExactly(RuntimeException.class, () -> command.onSlashCommand(group1sub1, context(group1sub1)));
        assertDoesNotThrow(() -> command.onSlashCommand(group1sub2, context(group1sub2)));
        assertThrowsExactly(RuntimeException.class, () -> command.onSlashCommand(group2sub1, context(group2sub1)));
    }

    @Test
    public void combinedTest() {
        var command = Slash.of("test", "test")
                           .group(Group.of("group1", "group1")
                                       .subCommand(SubCommand.of("sub1", "sub1")
                                                             .handler(throwingHandler()))
                                       .subCommand(SubCommand.of("sub2", "sub2")
                                                             .handler(emptyHandler())))
                           .group(Group.of("group2", "group2")
                                       .subCommand(SubCommand.of("sub1", "sub1")
                                                             .handler(throwingHandler()))
                                       .subCommand(SubCommand.of("sub2", "sub2")
                                                             .handler(emptyHandler())))
                           .subCommand(SubCommand.of("sub1", "sub1")
                                                 .handler(throwingHandler()))
                           .subCommand(SubCommand.of("sub2", "sub2")
                                                 .handler(emptyHandler()))
                           .build();

        var group1sub1 = eventForPath("test", "group1", "sub1");
        var group1sub2 = eventForPath("test", "group1", "sub2");
        var group2sub1 = eventForPath("test", "group2", "sub1");

        assertThrowsExactly(RuntimeException.class, () -> command.onSlashCommand(group1sub1, context(group1sub1)));
        assertDoesNotThrow(() -> command.onSlashCommand(group1sub2, context(group1sub2)));
        assertThrowsExactly(RuntimeException.class, () -> command.onSlashCommand(group2sub1, context(group2sub1)));

        var sub1 = eventForPath("test", "sub1");
        var sub2 = eventForPath("test", "sub2");

        assertThrows(RuntimeException.class, () -> command.onSlashCommand(sub1, context(sub1)));
        assertDoesNotThrow(() -> command.onSlashCommand(sub2, context(sub2)));
    }

    private SlashCommandInteractionEvent eventForPath(String... path) {
        var event = Mockito.mock(SlashCommandInteractionEvent.class);
        when(event.getFullCommandName()).thenReturn(path(path));
        return event;
    }

    private String path(String... path) {
        return String.join(" ", path);
    }

    private SlashHandler throwingHandler() {
        return (event, context) -> {
            throw new RuntimeException(event.getFullCommandName());
        };
    }

    private SlashHandler emptyHandler() {
        return (event, context) -> {
        };
    }

    private EventContext context(SlashCommandInteractionEvent event) {
        return new EventContext(event, null, null, null, null, null, null, Collections.emptyList());
    }
}
