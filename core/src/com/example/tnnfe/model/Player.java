package com.example.tnnfe.model;

import com.example.tnnfe.manager.EventManager;
import com.example.tnnfe.manager.events.EventType;

public class Player extends Human {

    private final Crystal crystal;

    private int bees;

    private boolean isReading;

    public Player(final int x, final int y, final int width, final int height) {
        super(x, y, width, height);
        crystal = new Crystal();
        bees = 0;

        EventManager eventManager = EventManager.getInstance();
        eventManager.register(EventType.SHOW_DIALOG, e -> isReading = true);
        eventManager.register(EventType.DIALOG_CLOSED, e -> isReading = false);
    }

    public Crystal getCrystal() {
        return crystal;
    }

    public void addBees(final int bees) {
        this.bees += bees;
    }

    public int getBees() {
        return bees;
    }

    public boolean isReading() {
        return isReading;
    }
}
