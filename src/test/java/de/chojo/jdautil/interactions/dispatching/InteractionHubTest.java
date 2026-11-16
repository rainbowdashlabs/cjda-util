/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.dispatching;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.chojo.jdautil.interactions.message.Message;
import de.chojo.jdautil.interactions.premium.SKU;
import de.chojo.jdautil.interactions.premium.SKUConfiguration;
import de.chojo.jdautil.interactions.slash.Slash;
import de.chojo.jdautil.interactions.user.User;
import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.entities.Entitlement;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class InteractionHubTest {
    private static final long PREMIUM = 1234567890123456789L;

    @Test
    void userCommandsAreRegisteredAsUserCommands() {
        var user = User.of("profile").handler((event, context) -> {}).build();
        InteractionHub<Slash, Message, User> hub = InteractionHub.<Slash, Message, User>builder(
                        Mockito.mock(ShardManager.class))
                .withUsers(user)
                .build();

        assertTrue(hub.getUser("profile").isPresent());
        assertTrue(hub.getMessage("profile").isEmpty());
    }

    @Test
    void contextContainsDiscordAndProviderEntitlements() {
        var contexts = new ArrayList<EventContext>();
        var command = Slash.of("test", "test")
                .unlocalized()
                .command((event, context) -> contexts.add(context))
                .build();
        var hub = InteractionHub.<Slash, Message, User>builder(Mockito.mock(ShardManager.class))
                .withCommands(command)
                .withEntitlementProvider((user, guild) -> List.of(new SKU(1L)))
                .build();
        var event = slashEvent("test");
        var entitlement = Mockito.mock(Entitlement.class);
        when(entitlement.getSkuIdLong()).thenReturn(PREMIUM);
        when(event.getEntitlements()).thenReturn(List.of(entitlement));

        hub.onSlashCommandInteraction(event);

        assertEquals(1, contexts.size());
        var skus = contexts.getFirst().entitlements().sku();
        assertTrue(skus.contains(new SKU(1L)));
        assertTrue(skus.contains(new SKU(PREMIUM)));
    }

    @Test
    void configuredSkusAreCheckedAgainstSkuIdsAndRunThePostHook() throws JsonProcessingException {
        var invoked = new ArrayList<String>();
        var result = new AtomicReference<InteractionResult>();
        var command = Slash.of("test", "test")
                .unlocalized()
                .command((event, context) -> invoked.add("test"))
                .build();
        var hub = InteractionHub.<Slash, Message, User>builder(Mockito.mock(ShardManager.class))
                .withCommands(command)
                .withSKUConfiguration(skuConfiguration("test", PREMIUM))
                .withPostCommandHook(result::set)
                .build();

        hub.onSlashCommandInteraction(slashEvent("test"));

        assertTrue(invoked.isEmpty());
        assertFalse(result.get().failed());

        var entitled = slashEvent("test");
        var entitlement = Mockito.mock(Entitlement.class);
        when(entitlement.getSkuIdLong()).thenReturn(PREMIUM);
        when(entitled.getEntitlements()).thenReturn(List.of(entitlement));

        hub.onSlashCommandInteraction(entitled);

        assertEquals(List.of("test"), invoked);
    }

    private static SlashCommandInteractionEvent slashEvent(String name) {
        var event = Mockito.mock(SlashCommandInteractionEvent.class, Mockito.RETURNS_DEEP_STUBS);
        when(event.getName()).thenReturn(name);
        when(event.getFullCommandName()).thenReturn(name);
        when(event.getGuild()).thenReturn(null);
        when(event.getEntitlements()).thenReturn(List.of());
        when(event.getOptions()).thenReturn(List.of());
        return event;
    }

    private static SKUConfiguration skuConfiguration(String command, long sku) throws JsonProcessingException {
        var mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return mapper.readValue("{\"slash\": {\"%s\": [%d]}}".formatted(command, sku), SKUConfiguration.class);
    }
}
