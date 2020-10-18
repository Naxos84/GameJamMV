package com.example.tnnfe.manager.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventListenerManager {

    private final Lock lock = new ReentrantLock();

    private Map<EventType, List<EventListener>> listenerByEventType = new HashMap<>();

    /**
     * Register a listener for a given event type
     *
     * @param eventType the type of the event the listener must be register to
     * @param listener  the listener to register
     */
    public void register(EventType eventType, EventListener listener) {
        lock.lock();
        try {
            final Map<EventType, List<EventListener>> copy = duplicateAListenerMap(listenerByEventType);
            listenerByEventType = addListenerToAListenerMap(copy, eventType, listener);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Remove a listener of a given event type
     *
     * @param eventType the type of the event the listener is register to
     * @param listener  the listener to unregister
     */
    public void unregister(EventType eventType, EventListener listener) {
        lock.lock();
        try {
            if (this.isListenerNotInCurrentListenerMap(eventType, listener)) {
                throw new IllegalArgumentException("Trying to unregister a non-registered listener");
            }
            final Map<EventType, List<EventListener>> copy = duplicateAListenerMap(listenerByEventType);
            listenerByEventType = removeListenerFromAListenerMap(copy, eventType, listener);
        } finally {
            lock.unlock();
        }
    }

    /**
     * @param eventType an event type
     * @return a stream of all listeners registered to the given event type
     */
    public Stream<EventListener> getEventListeners(EventType eventType) {
        return this.listenerByEventType.getOrDefault(eventType, Collections.emptyList()).stream();
    }


    /**
     * Check if a listener is not register for the provided event type in the current listener map
     *
     * @return true if not the listener is not register for the given event type
     */
    private boolean isListenerNotInCurrentListenerMap(EventType type, EventListener listener) {
        return !listenerByEventType.getOrDefault(type, Collections.emptyList()).contains(listener);
    }

    /**
     * @return a deep copy of the provide map
     */
    private static Map<EventType, List<EventListener>> duplicateAListenerMap(Map<EventType, List<EventListener>> mapToDuplicate) {
        return mapToDuplicate.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new ArrayList<>(e.getValue())));
    }

    /**
     * @return add a listener for a given event type to the provided map and returns it
     */
    private static Map<EventType, List<EventListener>> addListenerToAListenerMap(Map<EventType, List<EventListener>> mapToAddTo, EventType eventType, EventListener listener) {
        mapToAddTo.computeIfAbsent(eventType, t -> new ArrayList<>()).add(listener);
        return mapToAddTo;
    }

    /**
     * @return remove a listener for a given event type to the provided map and returns it
     */
    private static Map<EventType, List<EventListener>> removeListenerFromAListenerMap(Map<EventType, List<EventListener>> mapToRemoveFrom, EventType eventType, EventListener listener) {
        mapToRemoveFrom.computeIfPresent(eventType, (t, l) -> {
            l.remove(listener);
            return l.isEmpty() ? null : l;
        });
        return mapToRemoveFrom;
    }
}