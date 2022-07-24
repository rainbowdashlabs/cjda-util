/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.menus.entries;

import de.chojo.jdautil.localization.LocalizationContext;
import de.chojo.jdautil.menus.EntryContext;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.Component.Type;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

import java.util.function.Consumer;

public abstract class MenuEntry<Component extends ActionComponent, Event extends GenericComponentInteractionCreateEvent> {
    private Component component;
    private final Consumer<EntryContext<Event, Component>> eventConsumer;

    private boolean visible = true;

    public MenuEntry(Component component, Consumer<EntryContext<Event, Component>> eventConsumer) {
        this.component = component;
        this.eventConsumer = eventConsumer;
    }

    public abstract ActionComponent component(long id, LocalizationContext localizer);

    public static MenuEntry<?, ?> of(Button component, Consumer<EntryContext<ButtonInteractionEvent, Button>> eventConsumer) {
        return new ButtonEntry(component, eventConsumer);
    }

    public static MenuEntry<?, ?> of(SelectMenu component, Consumer<EntryContext<SelectMenuInteractionEvent, SelectMenu>> eventConsumer) {
        return new SelectMenuEntry(component, eventConsumer);
    }

    public void invoke(EntryContext<Event, Component> event) {
        eventConsumer.accept(event);
    }

    public void component(Component component) {
        this.component = component;
    }

    public Component component() {
        return component;
    }

    public Consumer<EntryContext<Event, Component>> eventConsumer() {
        return eventConsumer;
    }

    public String id() {
        return component.getId();
    }

    public Type type() {
        return component.getType();
    }

    public MenuEntry<Component, Event> visible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public MenuEntry<Component, Event> hidden() {
        return visible(false);
    }

    public boolean visible() {
        return visible;
    }
}
