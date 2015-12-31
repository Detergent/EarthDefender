package net.jgnetworks.earthdefender.projectile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

@SuppressWarnings("serial")
public class Laser extends Projectile{
	public TextureAtlas texture;
	public Animation animation;
	
	@Override
	public void create() {
		texture = new TextureAtlas(Gdx.files.internal("player/projectile/projectilePack.atlas"));
		animation = new Animation (1/3f, texture.getRegions());
	}
	
	@Override
	public void dispose(){
		texture.dispose();
	}
}
