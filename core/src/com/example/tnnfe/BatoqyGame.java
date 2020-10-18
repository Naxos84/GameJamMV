package com.example.tnnfe;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.example.tnnfe.screens.StartScreen;

public class BatoqyGame extends Game {

    @Override
    public void create() {

        Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);
        this.setScreen(new StartScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
    }
}
