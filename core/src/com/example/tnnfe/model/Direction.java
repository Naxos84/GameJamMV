package com.example.tnnfe.model;

import com.badlogic.gdx.math.Vector2;

public enum Direction {

    UP(new Vector2(0, 1)),

    RIGHT(new Vector2(1, 0)),

    DOWN(new Vector2(0, -1)),

    LEFT(new Vector2(-1, 0));

    private final Vector2 relativeCoordinates;

    Direction(Vector2 relativeCoordinates) {
        this.relativeCoordinates = relativeCoordinates;
    }

    public Vector2 getRelativeCoordinates() {
        return relativeCoordinates;
    }
}
