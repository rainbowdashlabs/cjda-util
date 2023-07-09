/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.pagination.bag;

import de.chojo.jdautil.localization.LocalizationContext;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface PageButton {
    Button button(IPageBag page);

    void invoke(IPageBag pageBag, ButtonInteraction event);

    static PageButton of(Function<IPageBag, Button> button, BiConsumer<IPageBag, ButtonInteraction> consumer) {
        return new PageButton() {
            @Override
            public Button button(IPageBag page) {
                return button.apply(page);
            }

            @Override
            public void invoke(IPageBag pageBag, ButtonInteraction event) {
                consumer.accept(pageBag, event);
            }
        };
    }

    default ActionComponent component(IPageBag page, long id, LocalizationContext localizer) {
            Button button = button(page);
            if (button.getId() == null) {
            if(button.getLabel().isBlank()){
                return button;
            }
            return button.withLabel(localizer.localize(button.getLabel()));
        }
        if (button.getLabel().isBlank()) {
            return button.withId(String.format("%s:%s", id, button.getId()));
        }
        return button.withId(String.format("%s:%s", id, button.getId()))
                .withLabel(localizer.localize(button.getLabel()));
    }
}
