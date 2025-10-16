/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.menus.entries;

import de.chojo.jdautil.localization.LocalizationContext;
import de.chojo.jdautil.menus.EntryContext;
import net.dv8tion.jda.api.components.ActionComponent;
import net.dv8tion.jda.api.components.Component.Type;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import net.dv8tion.jda.api.components.buttons.Button;

import java.util.function.Consumer;

public abstract class MenuEntry<Component extends ActionComponent, Event extends ComponentInteraction> {
    private Component component;
    private final Consumer<EntryContext<Event, Component>> eventConsumer;

    private boolean visible = true;

    public MenuEntry(Component component, Consumer<EntryContext<Event, Component>> eventConsumer) {
        this.component = component;
        this.eventConsumer = eventConsumer;
    }

    public abstract ActionRowChildComponent component(long id, LocalizationContext localizer);

    public static MenuEntry<?, ?> of(Button component, Consumer<EntryContext<ButtonInteractionEvent, Button>> eventConsumer) {
        return new ButtonEntry(component, eventConsumer);
    }

    public static MenuEntry<?, ?> of(StringSelectMenu component, Consumer<EntryContext<StringSelectInteractionEvent, StringSelectMenu>> eventConsumer) {
        return new StringSelectMenuEntry(component, eventConsumer);
    }

    public static MenuEntry<?, ?> of(EntitySelectMenu component, Consumer<EntryContext<EntitySelectInteractionEvent, EntitySelectMenu>> eventConsumer) {
        return new EntitySelectMenuEntry(component, eventConsumer);
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
        return component.getCustomId();
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
