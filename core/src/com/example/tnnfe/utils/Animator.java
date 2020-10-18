package com.example.tnnfe.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Animator {

    public static Animation<TextureRegion> getAnimation(Texture walkSheet, int xSize, int ySize, int start, int end, float timePerFrame) {

        Animation<TextureRegion> walkAnimation;

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(walkSheet, xSize, ySize);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] walkFrames = new TextureRegion[end - start + 1];

        int index = 0;
        boolean finish = false;

        int i = 0;

        for (int y = 0; y < tmp.length; y++) {

            TextureRegion[] rows = tmp[y];

            for (int x = 0; x < rows.length; x++) {

                if (index >= start) {
                    walkFrames[i++] = tmp[y][x];
                }

                index++;

                if (index > end) {
                    finish = true;
                    break;
                }
            }

            if (finish) {
                break;
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        walkAnimation = new Animation<TextureRegion>(timePerFrame, walkFrames);

        return walkAnimation;
    }
}