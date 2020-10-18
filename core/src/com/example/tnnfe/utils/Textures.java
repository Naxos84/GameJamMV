package com.example.tnnfe.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class Textures {

    public static Texture loadTexture(String path, int width, int height) {
        return resizeTexture(new Texture(Gdx.files.internal(path)), width, height);
    }

    public static Texture loadTexture(String path) {
        return loadTexture(path, Globals.CELL_SIZE, Globals.CELL_SIZE);
    }

    public static Texture resizeTexture(Texture texture, int width, int height) {
        if (!texture.getTextureData().isPrepared()) {
            texture.getTextureData().prepare();
        }
        Pixmap pixmapOriginal = texture.getTextureData().consumePixmap();
        Pixmap pixmapScaled = new Pixmap(width, height, pixmapOriginal.getFormat());
        pixmapScaled.drawPixmap(pixmapOriginal,
                0, 0, pixmapOriginal.getWidth(), pixmapOriginal.getHeight(),
                0, 0, pixmapScaled.getWidth(), pixmapScaled.getHeight()
        );
        texture.dispose();
        Texture resized = new Texture(pixmapScaled);
        pixmapOriginal.dispose();
        return resized;
    }

    public static Texture grayOut(Texture texture, float gray) {
        if (!texture.getTextureData().isPrepared()) {
            texture.getTextureData().prepare();
        }
        Pixmap pixmap = texture.getTextureData().consumePixmap();

        for (int y = 0; y < pixmap.getHeight(); y++) {
            for (int x = 0; x < pixmap.getWidth(); x++) {

                Color color = new Color();
                Color.rgba8888ToColor(color, pixmap.getPixel(x, y));
                color.r = (float) (color.r * (1 - 0.5 * gray));
                color.g = (float) (color.g * (1 - 0.5 * gray));
                color.b = (float) (color.b * (1 - 0.5 * gray));
                pixmap.setColor(color);
                pixmap.drawPixel(x, y);
            }
        }

        texture.dispose();
        texture = new Texture(pixmap);
        pixmap.dispose();

        return texture;
    }
}
