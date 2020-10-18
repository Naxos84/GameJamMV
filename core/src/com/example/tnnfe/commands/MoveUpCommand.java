package com.example.tnnfe.commands;

import com.example.tnnfe.model.Entity;

public class MoveUpCommand implements Command {

    @Override
    public void execute(final Entity entity) {
        entity.setY(entity.getY() + 1);
    }
}
