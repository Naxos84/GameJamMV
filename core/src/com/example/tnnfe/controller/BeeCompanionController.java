package com.example.tnnfe.controller;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.example.tnnfe.manager.AssetManager;
import com.example.tnnfe.manager.EventManager;
import com.example.tnnfe.manager.events.Event;
import com.example.tnnfe.manager.events.EventListener;
import com.example.tnnfe.manager.events.EventType;
import com.example.tnnfe.model.Direction;
import com.example.tnnfe.model.Player;
import com.example.tnnfe.utils.Animator;
import com.example.tnnfe.utils.Globals;

public class BeeCompanionController implements EventListener {

    Animation<TextureRegion> anim;
    TextureRegion currentAnimFrame;
    Texture beeCompanion;
    float companionX;
    float companionY;
    float numCompanions;
    boolean flipped;
    float timePerFrame = 0.1f;
    float stateTime = 0;
    PlayerController playerController;

    public BeeCompanionController(AssetManager assetManager) {
        beeCompanion = assetManager.getBeeCompanion();
        EventManager.getInstance().register(EventType.PLAYER_MOVED, this);
        anim = Animator.getAnimation(beeCompanion, 16, 16, 0, 4, timePerFrame);
    }

    public void update(float delta) {
        stateTime += delta;
        currentAnimFrame = anim.getKeyFrame(stateTime, true);
    }

    public void render(SpriteBatch batch, Player player) {
        if (player.getBees() > 0) {
            batch.draw(currentAnimFrame, companionX, companionY);
        }
    }


    @Override
    public void executeEvent(Event e) {
        if (e.getType() == EventType.PLAYER_MOVED) {
            Player player = (Player) e.getSource();
            Direction playerDir = player.viewDirection;
            Vector2 rel = playerDir.getRelativeCoordinates();
            companionX = player.getX() * Globals.CELL_SIZE - rel.x * Globals.CELL_SIZE;
            companionY = player.getY() * Globals.CELL_SIZE - rel.y * Globals.CELL_SIZE;
            numCompanions = player.getBees();
            if (rel.x > 0 && !flipped) {
                flipped = true;
            } else if (rel.x < 0 && flipped) {
                flipped = false;
            }
        }
    }
}
