package com.example.tnnfe.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.example.tnnfe.controller.MapController;
import com.example.tnnfe.manager.EventManager;
import com.example.tnnfe.manager.events.EventType;
import com.example.tnnfe.model.Crystal;
import com.example.tnnfe.model.Player;
import com.example.tnnfe.utils.Animator;
import com.example.tnnfe.utils.Globals;
import com.example.tnnfe.utils.Textures;

public class PlayerView {

    private final static int CRYSTAL_WIDTH = 40;
    private final static int CRYSTAL_HEIGHT = 33;

    Animation<TextureRegion> standDown;
    Animation<TextureRegion> standLeft;
    Animation<TextureRegion> standRight;
    Animation<TextureRegion> standUp;

    Animation<TextureRegion> walkDown;
    Animation<TextureRegion> walkLeft;
    Animation<TextureRegion> walkRight;
    Animation<TextureRegion> walkUp;

    TextureRegion currentAnimationFrame;

    private boolean isShaking;
    private float shakeStartTime = -1;

    float stateTime = 0;

    public PlayerView(final Texture textureRegion) {

        float timePerFrame = 0.100f;

        standDown = Animator.getAnimation(textureRegion, 16, 16, 7, 7, timePerFrame);
        standLeft = Animator.getAnimation(textureRegion, 16, 16, 19, 19, timePerFrame);
        standRight = Animator.getAnimation(textureRegion, 16, 16, 31, 31, timePerFrame);
        standUp = Animator.getAnimation(textureRegion, 16, 16, 43, 43, timePerFrame);

        walkDown = Animator.getAnimation(textureRegion, 16, 16, 6, 8, timePerFrame);
        walkLeft = Animator.getAnimation(textureRegion, 16, 16, 18, 20, timePerFrame);
        walkRight = Animator.getAnimation(textureRegion, 16, 16, 30, 32, timePerFrame);
        walkUp = Animator.getAnimation(textureRegion, 16, 16, 42, 44, timePerFrame);

        EventManager.getInstance().register(EventType.COLLECTED_DUST, e -> {
            isShaking = true;
            shakeStartTime = stateTime;
        });
    }

    public void update(final float deltaTime, final Player player) {

        stateTime += deltaTime;

        switch (player.viewDirection) {
            case UP: {

                if (player.isMoving) {
                    currentAnimationFrame = walkUp.getKeyFrame(stateTime, true);
                } else {
                    currentAnimationFrame = standUp.getKeyFrame(stateTime, true);
                }

                break;
            }
            case RIGHT: {
                if (player.isMoving) {
                    currentAnimationFrame = walkRight.getKeyFrame(stateTime, true);
                } else {
                    currentAnimationFrame = standRight.getKeyFrame(stateTime, true);
                }

                break;
            }
            case DOWN: {
                if (player.isMoving) {
                    currentAnimationFrame = walkDown.getKeyFrame(stateTime, true);
                } else {
                    currentAnimationFrame = standDown.getKeyFrame(stateTime, true);
                }

                break;
            }
            case LEFT: {
                if (player.isMoving) {
                    currentAnimationFrame = walkLeft.getKeyFrame(stateTime, true);
                } else {
                    currentAnimationFrame = standLeft.getKeyFrame(stateTime, true);
                }

                break;
            }
        }
    }

    public void render(SpriteBatch spriteBatch, Player player) {
        // Gdx.app.debug("PlayerView", "Rendering player on grid at: " + player.getX() + ":" + player.getY());
        spriteBatch.draw(currentAnimationFrame, player.getX() * Globals.CELL_SIZE, player.getY() * Globals.CELL_SIZE);
    }

    Texture crystalTextureBlurred;
    Texture crystalTexture;

    public void renderCrystal(SpriteBatch spriteBatch, Player player, float cameraX, float cameraY, int width, int height) {
        if(crystalTextureBlurred != null) {
            crystalTextureBlurred.dispose();
        }
        if(crystalTexture != null) {
            crystalTexture.dispose();
        }

        if (player.getCrystal().isFound()) {
            float dustScale = 1 - Math.min(1, 1f * player.getCrystal().getDust() / Crystal.FULL_DUST);
            double crystalBlurredScale = Math.max(0.1, 0.8 + Math.sin(stateTime * 1.5) / 6 - dustScale);
            int blurredWidth = (int) (75 * crystalBlurredScale);
            int blurredHeight = (int) (58 * crystalBlurredScale);

            int shakeOffset = 0;
            if (isShaking) {
                float delta = stateTime - shakeStartTime;
                shakeOffset = (int) (3 * Math.sin(delta * 50));
                if (delta > 0.33) {
                    isShaking = false;
                }
            }

            float x = cameraX + 1.25f * width + shakeOffset;
            float y = cameraY + 0.8f * height;

            float xBlurred = x - (blurredWidth - CRYSTAL_WIDTH) / 2f;
            float yBlurred = y - (blurredHeight - CRYSTAL_HEIGHT) / 2f;

            crystalTextureBlurred = Textures.grayOut(
                    Textures.loadTexture("sprites/mapTile_044_blurred.png", blurredWidth, blurredHeight),
                    dustScale
            );
            crystalTexture = Textures.grayOut(
                    Textures.loadTexture(player.getCrystal().isActivated() ?
                            "sprites/mapTile_044_activated.png" :
                            "sprites/mapTile_044.png", CRYSTAL_WIDTH, CRYSTAL_HEIGHT),
                    dustScale
            );

            spriteBatch.draw(crystalTextureBlurred, xBlurred, yBlurred);
            spriteBatch.draw(crystalTexture, x, y);
        }
    }
}
