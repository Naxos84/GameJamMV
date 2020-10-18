package com.example.tnnfe.manager;

import com.example.tnnfe.manager.events.Event;
import com.example.tnnfe.manager.events.EventListener;
import com.example.tnnfe.manager.events.EventListenerManager;
import com.example.tnnfe.manager.events.EventType;

public class EventManager {

    private static EventManager eventManager;

    private EventManager() {
    }

    private final EventListenerManager listenerManager = new EventListenerManager();

    public void register(EventType type, EventListener listener) {
        this.listenerManager.register(type, listener);
    }

    public void unregister(EventType type, EventListener listener) {
        this.listenerManager.unregister(type, listener);
    }

    public void submit(Event event) {
        if (event == null) {
            throw new NullPointerException("When submitting an event, the given event must not be null.");
        }
        listenerManager.getEventListeners(event.getType()).forEach(l -> l.executeEvent(event));
    }

    public static EventManager getInstance() {
        if (eventManager == null) {
            eventManager = new EventManager();
        }

        return eventManager;
    }
}