/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.chojo.jdautil.conversation.elements;

import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.util.ComponentUtil;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ButtonDialog {
    private Map<String, ComponenAction> components = new HashMap<>();

    public ButtonDialog add(ComponenAction action) {
        components.put(action.component().getId(), action);
        return this;
    }

    public ButtonDialog add(Component component, Function<InteractionContext, Result> onClick) {
        components.put(component.getId(), new ComponenAction(component, onClick));
        return this;
    }

    public Result handle(InteractionContext context) {
        if (components.containsKey(context.getComponentId())) {
            return components.get(context.getComponentId()).clicked(context);
        }
        return Result.freeze();
    }

    public Collection<? extends ActionRow> getActions(ILocalizer localizer, Guild guild) {
        var components = this.components.values().stream().map(action -> action.getTranslatedComponent(localizer, guild)).collect(Collectors.toList());
        return ComponentUtil.getActionRows(components);
    }
}
