package net.jgnetworks.earthdefender.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import net.jgnetworks.earthdefender.EarthDefender;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(480, 720);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new EarthDefender();
        }
}