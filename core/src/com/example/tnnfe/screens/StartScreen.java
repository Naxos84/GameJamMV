package com.example.tnnfe.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.example.tnnfe.BatoqyGame;
import com.example.tnnfe.controller.AudioController;
import com.example.tnnfe.manager.AssetManager;

public class StartScreen implements Screen {

    private final Stage stage;
    private final BatoqyGame game;

    private AssetManager assetManager;

    TextureRegion backgroundTexture;
    BitmapFont font = new BitmapFont();

    public StartScreen(BatoqyGame game) {

        // Load background image
        backgroundTexture = new TextureRegion(new Texture("background.jpg"), 0, 0, 1920, 1080);

        this.game = game;
        this.stage = new Stage(new ScreenViewport());

        // Init asset manager
        assetManager = new AssetManager();
        assetManager.loadInitialAssets();

        // Load music
        Music music = assetManager.getMusic("start.ogg");
        music.setLooping(true);
        music.setVolume(AudioController.musicVolume);
        music.play();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    boolean showStartText = false;
    float minimumDeltaTime = 0.55f;
    float currentDeltaTime = 0;

    SpriteBatch batch = new SpriteBatch();

    @Override
    public void render(final float delta) {

        if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
            game.setScreen(new GameScreen(game, assetManager));
        }

        if(Gdx.input.isKeyPressed(Input.Keys.B)) {
            Gdx.net.openURI("https://www.youtube.com/watch?v=I1Ns-nVULzA");
        }

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Display start text
        currentDeltaTime += delta;

        if (currentDeltaTime > minimumDeltaTime) {
            currentDeltaTime = 0;
            showStartText = !showStartText;
        }

        if (showStartText) {
            final GlyphLayout layout = new GlyphLayout(font, "Press any key to start");
            font.draw(batch, layout, Gdx.graphics.getWidth() / 2 - (layout.width / 2), 100);
        }

        batch.end();

    }

    @Override
    public void resize(final int width, final int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
    }
}