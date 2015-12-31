package net.jgnetworks.earthdefender.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;

public class Player extends Rectangle{
	public TextureAtlas shipTextureAtlas;
	public Animation shipIdleAnimation;
	
	public void create() {
		shipTextureAtlas = new TextureAtlas(Gdx.files.internal("player/ship/shippack/shippack.atlas"));
		shipIdleAnimation = new Animation (1/6f, shipTextureAtlas.getRegions());
		this.width = 64;
		this.height = 64;
		this.x = 480/2-this.width/2;
		this.y = 20;
	}
	
	public void dispose() {
		shipTextureAtlas.dispose();
	}
}
                                  