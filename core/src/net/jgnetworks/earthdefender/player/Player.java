package net.jgnetworks.earthdefender.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;

public class Player extends Rectangle{
	
	public Player() {
		this.width = 96;
		this.height = 96;
		this.x = 480/2-this.width/2;
		this.y = 20;
	}
	
}
                                  