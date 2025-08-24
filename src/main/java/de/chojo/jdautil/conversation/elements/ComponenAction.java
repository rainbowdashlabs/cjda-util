/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.conversation.elements;

import de.chojo.jdautil.localization.ILocalizer;
import net.dv8tion.jda.api.components.ActionComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Guild;

import java.util.function.Function;

public class ComponenAction {
    private final ActionComponent component;
    private final Function<InteractionContext, Result> onClick;

    public ComponenAction(ActionComponent component, Function<InteractionContext, Result> onClick) {
        this.component = component;
        this.onClick = onClick;
    }

    public ActionComponent component() {
        return component;
    }

    public ActionRowChildComponent getTranslatedComponent(ILocalizer localizer, Guild guild) {
        return switch (component.getType()) {
            case BUTTON -> {
                var button = (Button) this.component;
                yield Button.of(button.getStyle(), button.getId() == null ? button.getUrl() : button.getId(),
                        localizer.localize(button.getLabel(), guild));
            }
            default -> null;
        };
    }

    public Result clicked(InteractionContext context) {
        return onClick.apply(context);
    }
}
