package net.jgnetworks.earthdefender.player;

import com.badlogic.gdx.math.Rectangle;

public class Player extends Rectangle{
	
	public Rectangle hitBox;
	
	public Player() {
		this.width = 96;
		this.height = 96;
		this.x = 480/2-this.width/2;
		this.y = 20;
		
		hitBox = new Rectangle();
		hitBox.width = 32;
		hitBox.height = 32;
		hitBox.x = this.x + (this.x/2);
		hitBox.y = this.y + (this.y/2);
	}
	
}
                                  