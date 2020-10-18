package com.example.tnnfe.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.example.tnnfe.BatoqyGame;
import com.example.tnnfe.controller.AudioController;
import com.example.tnnfe.controller.BeeCompanionController;
import com.example.tnnfe.controller.BeeController;
import com.example.tnnfe.controller.MapController;
import com.example.tnnfe.controller.ObstacleController;
import com.example.tnnfe.controller.PlayerController;
import com.example.tnnfe.manager.AssetManager;
import com.example.tnnfe.manager.EventManager;
import com.example.tnnfe.manager.events.EventType;
import com.example.tnnfe.stages.DialogStage;
import com.example.tnnfe.utils.Globals;

public class GameScreen implements Screen {

    private final BatoqyGame game;
    private AssetManager assetManager;
    private OrthographicCamera camera;
    private AudioController audioController;
    private MapController mapController;
    private PlayerController playerController;
    private EventManager eventManager;
    private Stage gameStage;
    private DialogStage dialogStage;
    private InputMultiplexer inputs;
    private BeeController beeController;
    private BeeCompanionController beeCompanionController;
    private ObstacleController obstacleController;

    long beeId = 0;

    SpriteBatch playerBatch = new SpriteBatch();
    SpriteBatch crystalBatch = new SpriteBatch();
    SpriteBatch overlayBatch = new SpriteBatch();

    public GameScreen(final BatoqyGame game, final AssetManager assetManager) {

        this.game = game;
        this.eventManager = EventManager.getInstance();

        this.assetManager = assetManager;

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        width = 320;
        height = 240;

        dialogStage = new DialogStage(new Skin(Gdx.files.internal("skin/star-soldier/star-soldier-ui.json")), eventManager);

        inputs = new InputMultiplexer();
        inputs.addProcessor(dialogStage);

        // Camera setup
        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);
        camera.update();

        // Init event dispatcher
        eventManager.register(EventType.SHOW_DIALOG, dialogStage);

        // Init audio controller
        audioController = new AudioController(assetManager, eventManager);
        // Add debug controller
        beeController = new BeeController(assetManager);

        // Init map and player (and real start point)
        mapController = new MapController("maps/intro.tmx", eventManager);
        playerController = new PlayerController(8, 13, assetManager.getCharactersTexture(), mapController, eventManager);

        // Debug start
//        mapController = new MapController("maps/ow-0-0.tmx", eventManager);
//        playerController = new PlayerController(8, 10, assetManager.getCharactersTexture(), mapController, eventManager);

        beeCompanionController = new BeeCompanionController(assetManager);
        obstacleController = new ObstacleController(assetManager, mapController);

        inputs.addProcessor(playerController);
        inputs.addProcessor(beeController);

        Gdx.input.setInputProcessor(inputs);

        // Fire initial move event
        playerController.firePlayerMovedEvent();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(final float delta) {

        // Init OpenGl
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        beeController.update(delta);

        dialogStage.act(delta);

        mapController.update(delta);
        playerController.update(delta);
        beeCompanionController.update(delta);
        obstacleController.update(delta);

        // Gdx.app.debug("Game", "Camera is at: " + camera.position.x + ":" + camera.position.y);
//        camera.position.set(
//                playerController.getPlayerPositionX() * Globals.CELL_SIZE + 0.16f,
//                playerController.getPlayerPositionY() * Globals.CELL_SIZE + 0.16f,
//                0);

        // Border free camera system
        float playerCameraX = playerController.getPlayerPositionX() * Globals.CELL_SIZE + 0.16f;
        float playerCameraY = playerController.getPlayerPositionY() * Globals.CELL_SIZE + 0.16f;

        float cameraX = 0;
        float cameraY = 0;

//        Gdx.app.debug("Player (x)", "" + playerController.getPlayerPositionX() );

        int edgeArea = 25;

        if (playerController.getPlayerPositionX() < edgeArea) {
            cameraX = Math.max(playerCameraX, 10f * Globals.CELL_SIZE);
        } else if (playerController.getPlayerPositionX() > mapController.getWidth() - edgeArea) {
            cameraX = Math.min(playerCameraX, mapController.getWidth() * Globals.CELL_SIZE - (Globals.CELL_SIZE * 10f));
        } else {
            cameraX = playerCameraX;
        }

//        Gdx.app.debug("Player (y)", "" + playerController.getPlayerPositionY());

        if (playerController.getPlayerPositionY() < edgeArea) {
            cameraY = Math.max(playerCameraY, 8f * Globals.CELL_SIZE);
        } else if (playerController.getPlayerPositionY() > mapController.getHeight() - edgeArea) {
            cameraY = Math.min(playerCameraY, mapController.getHeight() * Globals.CELL_SIZE - (Globals.CELL_SIZE * 8f));
        } else {
            cameraY = playerCameraY;
        }

//        Gdx.app.debug("Camera (x)", "" + cameraX);
//        Gdx.app.debug("Camera (y)", "" + cameraY);

        camera.position.set(cameraX, cameraY, 0);

        camera.update();

        // Render map (without over layer)
        mapController.render(camera);

        // Render player with texture

        playerBatch.setProjectionMatrix(camera.combined);
        playerBatch.begin();
        beeController.render(playerBatch);
        playerController.render(playerBatch);
        beeCompanionController.render(playerBatch, playerController.getPlayer());
        obstacleController.render(playerBatch);
        playerBatch.end();

        // Render map (only over layer)
        mapController.renderOver(camera);

        // Render Crystal
        crystalBatch.setProjectionMatrix(camera.combined);
        crystalBatch.begin();
        playerController.renderCrystal(crystalBatch, cameraX, cameraY, mapController.getWidth(), mapController.getHeight());
        crystalBatch.end();

        overlayBatch.setProjectionMatrix(camera.combined);
        overlayBatch.begin();
        playerController.renderOverlay(overlayBatch);
        overlayBatch.end();

        dialogStage.draw();
    }

    @Override
    public void resize(final int width, final int height) {
        dialogStage.resize(width, height);
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

        playerBatch.dispose();
        crystalBatch.dispose();
        overlayBatch.dispose();

        mapController.dispose();
        assetManager.dispose();
    }
}
