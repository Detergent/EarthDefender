package net.jgnetworks.earthdefender.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import net.jgnetworks.earthdefender.EarthDefender;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title="EarthDefender";
		config.width = 480;
		config.height = 720;
		new LwjglApplication(new EarthDefender(), config);
	}
}
