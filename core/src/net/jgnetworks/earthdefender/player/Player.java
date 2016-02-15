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
		hitBox.width = this.width/2;
		hitBox.height = this.height/4;
		hitBox.x = this.x + this.width/4;
		hitBox.y = this.y + this.height/4;
	}
	
}
                                  