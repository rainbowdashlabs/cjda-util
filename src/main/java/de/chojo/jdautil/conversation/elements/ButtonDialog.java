/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.conversation.elements;

import de.chojo.jdautil.localization.ILocalizer;
import net.dv8tion.jda.api.components.ActionComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ButtonDialog {
    private final Map<String, ComponenAction> components = new HashMap<>();

    public ButtonDialog add(ComponenAction action) {
        components.put(action.component().getCustomId(), action);
        return this;
    }

    public ButtonDialog add(ActionComponent component, Function<InteractionContext, Result> onClick) {
        components.put(component.getCustomId(), new ComponenAction(component, onClick));
        return this;
    }

    public Result handle(InteractionContext context) {
        if (components.containsKey(context.getComponentId())) {
            return components.get(context.getComponentId()).clicked(context);
        }
        return Result.freeze();
    }

    public Collection<ActionRow> getActions(ILocalizer localizer, Guild guild) {
        var components = this.components.values().stream()
                                        .map(action -> action.getTranslatedComponent(localizer, guild))
                                        .collect(Collectors.toList());
        return ActionRow.partitionOf(components);
    }
}
