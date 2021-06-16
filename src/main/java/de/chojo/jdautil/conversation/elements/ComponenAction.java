package de.chojo.jdautil.conversation.elements;

import net.dv8tion.jda.api.interactions.components.Component;

import java.util.function.Supplier;

public class ComponenAction {
    private final Component component;
    private final Supplier<Result> onClick;

    public ComponenAction(Component component, Supplier<Result> onClick) {
        this.component = component;
        this.onClick = onClick;
    }

    public Component component() {
        return component;
    }

    public Result clicked() {
        return onClick.get();
    }
}
