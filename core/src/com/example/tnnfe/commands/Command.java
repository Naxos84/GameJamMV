package com.example.tnnfe.commands;

import com.example.tnnfe.model.Entity;

public interface Command {

    void execute(Entity entity);
}
