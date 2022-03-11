/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.util;

import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload;

import java.util.ArrayList;
import java.util.List;

public class SlashCommandUtil {
    public static String commandAsString(CommandInteractionPayload event) {
        List<String> components = new ArrayList<>();
        components.add(event.getName());
        if (event.getSubcommandGroup() != null) components.add(event.getSubcommandGroup());
        if (event.getSubcommandName() != null) components.add(event.getSubcommandName());
        for (var option : event.getOptions()) {
            var type = option.getType();
            var value = option.getAsString();
            var name = option.getName();
            components.add(String.format("{name: %s, type: %s, value: %s}", name, type, value));
        }
        return String.join(" ", components);
    }

}
