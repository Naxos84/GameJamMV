package com.example.tnnfe.model;

import com.badlogic.gdx.Gdx;
import com.example.tnnfe.manager.EventManager;
import com.example.tnnfe.manager.events.Event;
import com.example.tnnfe.manager.events.EventType;

public class Crystal {

    private final static byte REMOVE_DUST_ON_ACTIVATION = 20;

    private final static byte REMOVE_DUST_PER_SECOND = 20;

    public final static short FULL_DUST = 300;

    private final EventManager eventManager;

    private boolean isFound;

    private int dust;

    private boolean isActivated;

    private float deltaTime;

    Crystal() {
        eventManager = EventManager.getInstance();
    }

    public int getDust() {
        return dust;
    }

    public void found() {
        isFound = true;
        eventManager.submit(new Event(this, EventType.CRYSTAL_FOUND));
    }

    public boolean isFound() {
        return isFound;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void addDust(final int dust) {
        this.dust += dust;
        eventManager.submit(new Event(this, EventType.COLLECTED_DUST));
    }

    /**
     * Turns crystal on/off
     *
     * @return whether activation was successful
     */
    public boolean switchOnOff() {
        if (isActivated) {
            isActivated = false;
            eventManager.submit(new Event(this, EventType.CRYSTAL_DEACTIVATED));
        } else {
            if (isFound && dust >= REMOVE_DUST_ON_ACTIVATION) {
                isActivated = true;
                dust -= REMOVE_DUST_ON_ACTIVATION;
                eventManager.submit(new Event(this, EventType.CRYSTAL_ACTIVATED));
                Gdx.app.debug("Crystal", "Activated, removed " + REMOVE_DUST_ON_ACTIVATION + " dust");
            } else {
                eventManager.submit(new Event(this, EventType.CRYSTAL_ACTIVATION_FAILED));
                return false;
            }
        }

        return true;
    }

    public void update(final float delta) {
        if (isActivated) {
            deltaTime += delta;
            if (Math.floor(deltaTime) == 1) {
                if ((dust -= REMOVE_DUST_PER_SECOND) == 0) {
                    switchOnOff();
                }
                deltaTime = 0;
                Gdx.app.debug("Crystal", "Dust " + dust);
            }
        }
    }
}
