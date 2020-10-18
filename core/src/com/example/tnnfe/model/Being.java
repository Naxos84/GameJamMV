package com.example.tnnfe.model;

public class Being extends Entity {

    public boolean isMoving;
    public Direction viewDirection;
    int health;

    /**
     * Defines speed of moving from one field to another field
     */
    int speed;

    public Being(final int x, final int y, final int width, final int height) {
        super(x, y, width, height);

        isMoving = true;

        int health = 100;
        viewDirection = Direction.DOWN;
    }

}
