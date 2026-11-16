/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash;

import de.chojo.jdautil.interactions.dispatching.InteractionHub;
import de.chojo.jdautil.interactions.premium.SKU;
import de.chojo.jdautil.interactions.slash.structure.handler.SlashHandler;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SlashTest {
    private static final long PREMIUM = 1234567890123456789L;

    @Test
    public void testBase() {
        var command = Slash.of("test", "test").command(throwingHandler()).build();

        var event = eventForPath("test");
        assertThrows(RuntimeException.class, () -> command.onSlashCommand(event, context(event)));
    }

    @Test
    public void testSub() {
        var command = Slash.of("test", "test")
                .guildOnly()
                .subCommand(SubCommand.of("test1", "test1").handler(throwingHandler()))
                .subCommand(SubCommand.of("sub2", "sub2").handler(emptyHandler()))
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
                        .subCommand(SubCommand.of("sub1", "sub1").handler(throwingHandler()))
                        .subCommand(SubCommand.of("sub2", "sub2").handler(emptyHandler())))
                .group(Group.of("group2", "group2")
                        .subCommand(SubCommand.of("sub1", "sub1").handler(throwingHandler()))
                        .subCommand(SubCommand.of("sub2", "sub2").handler(emptyHandler())))
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
                        .subCommand(SubCommand.of("sub1", "sub1").handler(throwingHandler()))
                        .subCommand(SubCommand.of("sub2", "sub2").handler(emptyHandler())))
                .group(Group.of("group2", "group2")
                        .subCommand(SubCommand.of("sub1", "sub1").handler(throwingHandler()))
                        .subCommand(SubCommand.of("sub2", "sub2").handler(emptyHandler())))
                .subCommand(SubCommand.of("sub1", "sub1").handler(throwingHandler()))
                .subCommand(SubCommand.of("sub2", "sub2").handler(emptyHandler()))
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

    @Test
    public void rootSkusGateSubCommands() {
        var command = Slash.of("test", "test")
                .skus(List.of(new SKU(PREMIUM)))
                .subCommand(SubCommand.of("sub", "sub").handler(throwingHandler()))
                .build();

        var event = eventForPath("test", "sub");

        assertDoesNotThrow(() -> command.onSlashCommand(event, context(event)));
        assertThrows(RuntimeException.class, () -> command.onSlashCommand(event, context(event, new SKU(PREMIUM))));
    }

    @Test
    public void groupSkusGateSubCommands() {
        var command = Slash.of("test", "test")
                .group(Group.of("group", "group")
                        .subCommand(SubCommand.of("sub", "sub").handler(throwingHandler()))
                        .skus(List.of(new SKU(PREMIUM))))
                .build();

        var event = eventForPath("test", "group", "sub");

        assertDoesNotThrow(() -> command.onSlashCommand(event, context(event)));
        assertThrows(RuntimeException.class, () -> command.onSlashCommand(event, context(event, new SKU(PREMIUM))));
    }

    @Test
    public void rootSkusGateAutoComplete() {
        var command = Slash.of("test", "test")
                .skus(List.of(new SKU(PREMIUM)))
                .subCommand(SubCommand.of("sub", "sub").handler(throwingHandler()))
                .build();

        var event = autoCompleteEvent("test", "sub");

        assertDoesNotThrow(() -> command.onAutoComplete(event, autoCompleteContext()));
        verify(event).replyChoices();
    }

    @Test
    public void subCommandSkusGateAutoCompleteWithoutReplyCallback() {
        var command = Slash.of("test", "test")
                .subCommand(
                        SubCommand.of("sub", "sub").handler(throwingHandler()).skus(List.of(new SKU(PREMIUM))))
                .build();

        var event = autoCompleteEvent("test", "sub");

        assertDoesNotThrow(() -> command.onAutoComplete(event, autoCompleteContext()));
        verify(event).replyChoices();
    }

    @Test
    public void groupSkusGateAutoComplete() {
        var command = Slash.of("test", "test")
                .group(Group.of("group", "group")
                        .subCommand(SubCommand.of("sub", "sub").handler(throwingHandler()))
                        .skus(List.of(new SKU(PREMIUM))))
                .build();

        var event = autoCompleteEvent("test", "group", "sub");

        assertDoesNotThrow(() -> command.onAutoComplete(event, autoCompleteContext()));
        verify(event).replyChoices();
    }

    @Test
    public void entitledAutoCompleteReachesTheHandler() {
        var command = Slash.of("test", "test")
                .skus(List.of(new SKU(PREMIUM)))
                .subCommand(SubCommand.of("sub", "sub")
                        .handler(throwingAutoComplete())
                        .skus(List.of(new SKU(PREMIUM))))
                .build();

        var event = autoCompleteEvent("test", "sub");

        assertThrows(
                RuntimeException.class, () -> command.onAutoComplete(event, autoCompleteContext(new SKU(PREMIUM))));
    }

    private CommandAutoCompleteInteractionEvent autoCompleteEvent(String... path) {
        var event = Mockito.mock(CommandAutoCompleteInteractionEvent.class, Mockito.RETURNS_DEEP_STUBS);
        when(event.getFullCommandName()).thenReturn(path(path));
        return event;
    }

    private EventContext autoCompleteContext(SKU... entitlements) {
        return new EventContext(null, null, ILocalizer.DEFAULT, null, null, null, null, List.of(entitlements));
    }

    private SlashCommandInteractionEvent eventForPath(String... path) {
        var event = Mockito.mock(SlashCommandInteractionEvent.class, Mockito.RETURNS_DEEP_STUBS);
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

    private SlashHandler throwingAutoComplete() {
        return SlashHandler.of((event, context) -> {}, (event, context) -> {
            throw new RuntimeException(event.getFullCommandName());
        });
    }

    private SlashHandler emptyHandler() {
        return (event, context) -> {};
    }

    private EventContext context(SlashCommandInteractionEvent event, SKU... entitlements) {
        var hub = Mockito.mock(InteractionHub.class);
        when(hub.premiumErrorMessage()).thenReturn("error.premium");
        return new EventContext(event, null, ILocalizer.DEFAULT, null, null, null, hub, List.of(entitlements));
    }
}
