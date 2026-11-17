/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.premium;

import de.chojo.jdautil.interactions.base.SkuMeta;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("FieldMayBeFinal")
public class SKUConfiguration {
    private Map<String, Set<Long>> slash = new HashMap<>();
    private Map<String, Set<Long>> messages = new HashMap<>();
    private Map<String, Set<Long>> users = new HashMap<>();

    public SKUConfiguration() {}

    public boolean isEntitled(SlashCommandInteractionEvent interaction) {
        return isEntitled(slash, interaction);
    }

    public boolean isEntitled(CommandAutoCompleteInteractionEvent interaction) {
        return isEntitled(slash, interaction);
    }

    public boolean isEntitled(MessageContextInteractionEvent interaction) {
        return isEntitled(messages, interaction);
    }

    public boolean isEntitled(UserContextInteractionEvent interaction) {
        return isEntitled(users, interaction);
    }

    /**
     * Checks the entitlement of a slash command or its autocompletion under a given full command name.
     * Used when the command was invoked under an alias, since the payload only knows the alias.
     *
     * @param interaction     interaction payload
     * @param fullCommandName full command name with the name of the command instead of the alias
     * @return true if the user is entitled to use the command
     */
    public boolean isCommandEntitled(CommandInteractionPayload interaction, String fullCommandName) {
        return isEntitled(slash, interaction, fullCommandName);
    }

    /**
     * The SKUs required for a slash command.
     *
     * @param fullCommandName full command name
     * @return required SKUs, empty when the command is not restricted
     */
    public SkuMeta commandSkus(String fullCommandName) {
        return skuMeta(slash, fullCommandName);
    }

    /**
     * The SKUs required for a message command.
     *
     * @param name command name
     * @return required SKUs, empty when the command is not restricted
     */
    public SkuMeta messageSkus(String name) {
        return skuMeta(messages, name);
    }

    /**
     * The SKUs required for a user command.
     *
     * @param name command name
     * @return required SKUs, empty when the command is not restricted
     */
    public SkuMeta userSkus(String name) {
        return skuMeta(users, name);
    }

    private static SkuMeta skuMeta(Map<String, Set<Long>> map, String name) {
        List<SKU> skus = map.getOrDefault(name, Collections.emptySet()).stream()
                .map(SKU::new)
                .toList();
        return () -> skus;
    }

    public Set<Long> commands(String name) {
        return slash.get(name);
    }

    public Set<Long> messages(String name) {
        return messages.get(name);
    }

    public Set<Long> users(String name) {
        return users.get(name);
    }

    private boolean isEntitled(Map<String, Set<Long>> map, CommandInteractionPayload payload) {
        return isEntitled(map, payload, payload.getFullCommandName());
    }

    private boolean isEntitled(Map<String, Set<Long>> map, CommandInteractionPayload payload, String fullCommandName) {
        Set<Long> entitlements = map.getOrDefault(fullCommandName, Collections.emptySet());
        if (!entitlements.isEmpty()) {
            return payload.getEntitlements().stream().anyMatch(e -> entitlements.contains(e.getSkuIdLong()));
        }
        return true;
    }
}
