package com.example.tnnfe.model;

import com.badlogic.gdx.math.Rectangle;

public class Obstacle {

    private final int neededBees;
    private final String preText;
    private final String okayText;
    private final Rectangle obstacleZone;

    public Obstacle(float posX, float posY, float width, float height, int neededBees, String preText, String okayText) {
        this.neededBees = neededBees;
        this.preText = preText;
        this.okayText = okayText;
        this.obstacleZone = new Rectangle(posX, posY, width, height);
    }

    public int getNeededBees() {
        return neededBees;
    }

    public String getPreText() {
        return preText;
    }

    public String getOkayText() {
        return okayText;
    }

    public Rectangle getObstacleZone() {
        return obstacleZone;
    }
}
