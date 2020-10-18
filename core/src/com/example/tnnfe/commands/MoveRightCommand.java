package com.example.tnnfe.commands;

import com.example.tnnfe.model.Entity;

public class MoveRightCommand implements Command {

    @Override
    public void execute(final Entity entity) {
        entity.setX(entity.getX() + 1);
    }
}
