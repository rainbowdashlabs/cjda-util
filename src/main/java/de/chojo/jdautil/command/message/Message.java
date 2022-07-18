package de.chojo.jdautil.command.message;

import de.chojo.jdautil.command.base.Interaction;
import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;

public abstract class Message implements Interaction {
    public abstract void onMessage(MessageContextInteractionEvent event, EventContext context);
}
