package com.example.tnnfe.manager.events;

public class Event {

    private final Object source;

    private final EventType type;

    public Event(Object source, EventType type) {
        this.source = source;
        this.type = type;
    }

    public Object getSource() {
        return source;
    }

    public EventType getType() {
        return type;
    }
}