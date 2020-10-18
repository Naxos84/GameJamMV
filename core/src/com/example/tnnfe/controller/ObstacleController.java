package com.example.tnnfe.controller;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.example.tnnfe.manager.AssetManager;
import com.example.tnnfe.manager.EventManager;
import com.example.tnnfe.manager.events.Event;
import com.example.tnnfe.manager.events.EventListener;
import com.example.tnnfe.manager.events.EventType;
import com.example.tnnfe.model.Obstacle;
import com.example.tnnfe.utils.Animator;
import com.example.tnnfe.utils.Globals;

import java.util.ArrayList;
import java.util.List;

public class ObstacleController implements EventListener {


    Animation<TextureRegion> anim;
    TextureRegion currentAnimFrame;
    Texture beeCompanion;
    AssetManager assetManager;
    MapController mapController;
    float stateTime = 0;
    float timePerFrame = 0.2f;

    public ObstacleController(AssetManager assetManager, MapController mapController) {
        this.assetManager = assetManager;
        this.mapController = mapController;

        beeCompanion = assetManager.getBeeCompanion();
        anim = Animator.getAnimation(beeCompanion, 16, 16, 0, 4, timePerFrame);
        EventManager.getInstance().register(EventType.PLAYER_MOVED, this);

        EventManager.getInstance().register(EventType.MAP_LOADED, this);
    }

    public void render(SpriteBatch batch) {
        for (Obstacle obstalce : mapController.obstacles) {
            float x = obstalce.getObstacleZone().x;
            float y = obstalce.getObstacleZone().y;
            float width = obstalce.getObstacleZone().width * Globals.CELL_SIZE;
            float height = obstalce.getObstacleZone().height * Globals.CELL_SIZE;
            batch.draw(currentAnimFrame, x * Globals.CELL_SIZE, y * Globals.CELL_SIZE, width, height);
        }
    }

    public void update(float delta) {
        stateTime += delta;
        currentAnimFrame = anim.getKeyFrame(stateTime, true);
    }

    @Override
    public void executeEvent(Event e) {

    }
}
