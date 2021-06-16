package de.chojo.jdautil.conversation.elements;

import de.chojo.jdautil.util.ComponentUtil;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ButtonDialog {
    private Map<String, ComponenAction> components = new HashMap<>();

    public ButtonDialog add(ComponenAction action) {
        components.put(action.component().getId(), action);
        return this;
    }

    public Result handle(ButtonClickEvent event) {
        if (components.containsKey(event.getComponentId())) {
            return components.get(event.getComponentId()).clicked();
        }
        return Result.freeze();
    }

    public Collection<? extends ActionRow> getActions() {
        var components = this.components.values().stream().map(ComponenAction::component).collect(Collectors.toList());
        return ComponentUtil.getActionRows(components);
    }
}
