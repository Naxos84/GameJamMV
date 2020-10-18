package com.example.tnnfe.manager;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.example.tnnfe.utils.Files;

public class AssetManager {

    private final com.badlogic.gdx.assets.AssetManager manager = new com.badlogic.gdx.assets.AssetManager();

    // Music
    private final String credits = "music/credits.ogg";
    private final String happy = "music/happy.ogg";
    private final String neutral = "music/neutral.ogg";
    private final String sad = "music/sad.ogg";
    private final String start = "music/start.ogg";

    private final String beeSound = "sfx/bee-hive-loop.ogg";

    // SFX
    private final String footstepSfx0 = "sfx/footstep00.ogg";
    private final String footstepSfx1 = "sfx/footstep01.ogg";
    private final String footstepSfx2 = "sfx/footstep02.ogg";
    private final String footstepSfx3 = "sfx/footstep03.ogg";
    private final String footstepSfx4 = "sfx/footstep04.ogg";
    private final String footstepSfx5 = "sfx/footstep05.ogg";
    private final String footstepSfx6 = "sfx/footstep06.ogg";
    private final String footstepSfx7 = "sfx/footstep07.ogg";
    private final String footstepSfx8 = "sfx/footstep08.ogg";
    private final String footstepSfx9 = "sfx/footstep09.ogg";

    // Textures
    private final String charactersTextureFilename = "sprites/characters.png";
    private final String beeCompanion = "sprites/swarm.png";
    private final String bee = "sprites/Bee-Companion.png";

    public void loadInitialAssets() {

        // Load Music
//        String[] filenames = Files.getFiles("music/", "*.ogg");
//
//        for (String file : filenames) {
//            manager.load("music/" + file, Music.class);
//        }

        manager.load(credits, Music.class);
        manager.load(happy, Music.class);
        manager.load(neutral, Music.class);
        manager.load(sad, Music.class);
        manager.load(start, Music.class);

        // Load sfx
        manager.load(footstepSfx0, Sound.class);
        manager.load(footstepSfx1, Sound.class);
        manager.load(footstepSfx2, Sound.class);
        manager.load(footstepSfx3, Sound.class);
        manager.load(footstepSfx4, Sound.class);
        manager.load(footstepSfx5, Sound.class);
        manager.load(footstepSfx6, Sound.class);
        manager.load(footstepSfx7, Sound.class);
        manager.load(footstepSfx8, Sound.class);
        manager.load(footstepSfx9, Sound.class);

        manager.load(beeSound, Sound.class);

        // Load textures
        manager.load(charactersTextureFilename, Texture.class);
        manager.load(beeCompanion, Texture.class);
        manager.load(bee, Texture.class);

        // Wait until all assets are loaded
        manager.finishLoading();
    }

    public Music getMusic(String filename) {

        String musicFilename = "music/" + filename;
        return manager.get(musicFilename);
    }

    public Sound getBee() {
        return manager.get(beeSound);
    }

    public Sound getFootstepSfx0() {
        return manager.get(footstepSfx0);
    }

    public Sound getFootstepSfx1() {
        return manager.get(footstepSfx0);
    }

    public Sound getFootstepSfx2() {
        return manager.get(footstepSfx0);
    }

    public Sound getFootstepSfx3() {
        return manager.get(footstepSfx0);
    }

    public Sound getFootstepSfx4() {
        return manager.get(footstepSfx0);
    }

    public Sound getFootstepSfx5() {
        return manager.get(footstepSfx0);
    }

    public Sound getFootstepSfx6() {
        return manager.get(footstepSfx0);
    }

    public Sound getFootstepSfx7() {
        return manager.get(footstepSfx0);
    }

    public Sound getFootstepSfx8() {
        return manager.get(footstepSfx0);
    }

    public Sound getFootstepSfx9() {
        return manager.get(footstepSfx0);
    }

    public Texture getCharactersTexture() {
        return manager.get(charactersTextureFilename);
    }

    public Texture getBeeCompanion() {
        return manager.get(beeCompanion);
    }
    public Texture getBeeTexture() { return manager.get(bee);}

    public void dispose() {
        manager.dispose();
    }
}
