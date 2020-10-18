package com.example.tnnfe.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.example.tnnfe.utils.Distance;
import com.example.tnnfe.manager.AssetManager;
import com.example.tnnfe.manager.EventManager;
import com.example.tnnfe.manager.events.Event;
import com.example.tnnfe.manager.events.EventType;
import com.example.tnnfe.model.Bee;
import com.example.tnnfe.model.Player;
import com.example.tnnfe.utils.Globals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BeeController implements InputProcessor, com.example.tnnfe.manager.events.EventListener {

    private AssetManager assetManager;
    float currentPan = 0;
    float currentVolume = 0.5f;
    List<Bee> bees;

    float playerX = 0;
    float playerY = 0;

    float mapHeight = 1;
    float mapWidth = 1;

    float counter = 0f;

    boolean enabled = false;

    float maxDistance = 1;

    public BeeController(AssetManager assetManager) {
        this.assetManager = assetManager;
        EventManager manager = EventManager.getInstance();

        manager.register(EventType.MAP_LOADED, this);
        manager.register(EventType.PLAYER_MOVED, this);
        manager.register(EventType.CRYSTAL_ACTIVATED, this);
        manager.register(EventType.CRYSTAL_DEACTIVATED, this);
    }

    private void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void update(final float deltaTime) {
        if (enabled) {
            for (Bee bee : bees) {
                float x = 1 / (float) Math.pow(Globals.CELLS_TILL_SILENCE * Globals.CELL_SIZE, 2);
                float distanceBetweenPlayerAndBee = Distance.between(bee.x, bee.y, this.playerX, this.playerY);
                float volume = 1 - Math.min(1, x * (float) Math.pow(distanceBetweenPlayerAndBee, 2));
                this.counter += deltaTime;
                float pan = MathUtils.clamp((bee.x - this.playerX) / (Globals.CELL_SIZE * Globals.CELL_PANING), -1f, 1f);
                assetManager.getBee().setPan(bee.soundId, pan, volume);
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (Bee bee : bees) {
            batch.draw(this.assetManager.getBeeTexture(), bee.x, bee.y, bee.width, bee.height);
        }
    }

    void increasePan() {
        currentPan += .1;
        currentPan = MathUtils.clamp(currentPan, -1, 1);
    }

    void decreasePan() {
        currentPan -= .1;
        currentPan = MathUtils.clamp(currentPan, -1, 1);
    }

    void increaseVolume() {
        currentVolume += .1;
        currentVolume = MathUtils.clamp(currentVolume, 0, 1);
    }

    void decreaseVolume() {
        currentVolume -= .1;
        currentVolume = MathUtils.clamp(currentVolume, 0, 1);
    }

    @Override
    public boolean keyDown(final int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(final int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(final char character) {

        return false;
    }

    @Override
    public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
        return false;
    }

    @Override
    public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
        return false;
    }

    @Override
    public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(final int screenX, final int screenY) {


        return false;
    }

    @Override
    public boolean scrolled(final int amount) {
        return false;
    }

    @Override
    public void executeEvent(Event e) {
        if (e.getType() == EventType.MAP_LOADED) {
            TiledMap map = (TiledMap) e.getSource();
            List<MapObject> beeObjects = MapController.getMapObjectsFromMap(map, "BEES");
            this.bees = new ArrayList<>();

            Gdx.app.debug("", "Found " + beeObjects.size() + " bees");
            for (MapObject beeObject : beeObjects) {
                float beeX = beeObject.getProperties().get("x", Float.class);
                float beeY = beeObject.getProperties().get("y", Float.class);
                int beeCount = beeObject.getProperties().get("BEES_COUNT", Integer.class);
                float width = beeObject.getProperties().get("width", Float.class);
                float height = beeObject.getProperties().get("height", Float.class);
                Bee bee = new Bee();
                bee.x = beeX;
                bee.y = beeY;
                bee.beeCount = beeCount;
                bee.width = width;
                bee.height = height;
                bee.rect = new Rectangle(beeX, beeY, width, height);
                this.bees.add(bee);
            }


            this.mapWidth = map.getProperties().get("width", Integer.class);
            this.mapHeight = map.getProperties().get("height", Integer.class);
            this.maxDistance = Distance.between(0, 0, mapWidth * Globals.CELL_SIZE, mapHeight + Globals.CELL_SIZE);
        }
        if (e.getType() == EventType.PLAYER_MOVED) {
            Player player = (Player) e.getSource();
            this.playerX = player.getX() * (float) Globals.CELL_SIZE;
            this.playerY = player.getY() * (float) Globals.CELL_SIZE;
            checkBeeCollision();
        }

        if (e.getType() == EventType.CRYSTAL_ACTIVATED) {
            setEnabled(true);
            for (Bee bee : bees) {
                bee.soundId = assetManager.getBee().loop(0, MathUtils.random(0.5f, 2f), 0);
            }
        }

        if (e.getType() == EventType.CRYSTAL_DEACTIVATED) {
            setEnabled(false);
            for (Bee bee : bees) {
                assetManager.getBee().stop(bee.soundId);
            }
        }
    }

    private void checkBeeCollision() {
        Rectangle playerRect = new Rectangle(playerX, playerY, Globals.CELL_SIZE, Globals.CELL_SIZE);
        for (Iterator<Bee> iter = bees.iterator(); iter.hasNext(); ) {
            Bee bee = iter.next();
            if (Intersector.overlaps(playerRect, bee.rect)) {
                EventManager.getInstance().submit(new Event(bee.beeCount, EventType.BEE_COLLECTED));
                assetManager.getBee().stop(bee.soundId);
                iter.remove();
            }

        }
    }
}
