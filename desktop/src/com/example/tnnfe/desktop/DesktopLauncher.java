package com.example.tnnfe.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.example.tnnfe.BatoqyGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

//		config.fullscreen = true;

		config.title = "A World Without";
		config.width = 800;
		config.height = 600;

		new LwjglApplication(new BatoqyGame(), config);
	}
}
