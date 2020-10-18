package com.example.tnnfe.controller;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.example.tnnfe.manager.AssetManager;
import com.example.tnnfe.manager.EventManager;
import com.example.tnnfe.manager.events.Event;
import com.example.tnnfe.manager.events.EventListener;
import com.example.tnnfe.manager.events.EventType;
import com.example.tnnfe.utils.Globals;

public class AudioController {

    AssetManager assetManager;

    public static float sfxVolume = 0.1f;
    public static float musicVolume = 0.3f;

    public AudioController(AssetManager assetManager, EventManager eventManager) {

        this.assetManager = assetManager;
        registerGameEvents(eventManager);
    }

    Music music = null;

    private void registerGameEvents(EventManager eventManager) {

        eventManager.register(EventType.MAP_LOADED, new EventListener() {
            @Override
            public void executeEvent(final Event e) {

                if (e != null && e.getSource() != null) {

                    TiledMap map = (TiledMap) e.getSource();

                    MapProperties properties = map.getProperties();
                    String filename = (String) properties.get("MUSIC");

                    if (filename != null) {

                        try {
                            if(music != null) {
                                music.stop();
                            }

                            music = assetManager.getMusic(filename);
                            music.setVolume(musicVolume);
                            music.setLooping(true);
                            music.play();
                        } catch (Exception ex) {
                            // Ignore
                        }
                    }
                }
            }
        });

        eventManager.register(EventType.PLAYER_MOVED, new EventListener() {
            @Override
            public void executeEvent(final Event e) {
                playFootsteps();
            }
        });
    }

    private void playFootsteps() {

        int randomNumber = Globals.random.nextInt(10);

        switch (randomNumber) {
            case 0: {
                assetManager.getFootstepSfx0().play(sfxVolume);
                break;
            }
            case 1: {
                assetManager.getFootstepSfx1().play(sfxVolume);
                break;
            }
            case 2: {
                assetManager.getFootstepSfx2().play(sfxVolume);
                break;
            }
            case 3: {
                assetManager.getFootstepSfx3().play(sfxVolume);
                break;
            }
            case 4: {
                assetManager.getFootstepSfx4().play(sfxVolume);
                break;
            }
            case 5: {
                assetManager.getFootstepSfx5().play(sfxVolume);
                break;
            }
            case 6: {
                assetManager.getFootstepSfx6().play(sfxVolume);
                break;
            }
            case 7: {
                assetManager.getFootstepSfx7().play(sfxVolume);
                break;
            }
            case 8: {
                assetManager.getFootstepSfx8().play(sfxVolume);
                break;
            }
            case 9: {
                assetManager.getFootstepSfx9().play(sfxVolume);
                break;
            }
        }
    }
}
