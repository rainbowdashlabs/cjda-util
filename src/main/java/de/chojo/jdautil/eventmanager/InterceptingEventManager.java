package de.chojo.jdautil.eventmanager;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.InterfacedEventManager;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiConsumer;

public class InterceptingEventManager implements IEventManager {
    private final InterfacedEventManager eventManager = new InterfacedEventManager();
    private final BiConsumer<GenericEvent, Throwable> exceptionHandler;

    public InterceptingEventManager(BiConsumer<GenericEvent, Throwable> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void register(@Nonnull Object listener) {
        eventManager.register(listener);
    }

    @Override
    public void unregister(@Nonnull Object listener) {
        eventManager.unregister(listener);
    }

    @Nonnull
    @Override
    public List<Object> getRegisteredListeners() {
        return eventManager.getRegisteredListeners();
    }

    @Override
    public void handle(@Nonnull GenericEvent event) {
        for (Object obj : getRegisteredListeners()) {
            if (!(obj instanceof EventListener listener)) continue;
            try {
                listener.onEvent(event);
            } catch (Throwable throwable) {
                exceptionHandler.accept(event, throwable);
            }
        }
    }

}
