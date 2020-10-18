package com.example.tnnfe.model;

import java.util.List;

public class Rail {

    List<Direction> commands;

    public Rail(final List<Direction> commands) {
        this.commands = commands;
    }

    public List<Direction> getCommands() {
        return commands;
    }
}
