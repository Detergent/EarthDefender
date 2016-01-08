package net.jgnetworks.earthdefender.projectile;

import com.badlogic.gdx.math.Rectangle;

import net.jgnetworks.earthdefender.player.Player;

@SuppressWarnings("serial")
public class Projectile extends Rectangle{
	
	public Projectile(Player player) {
		this.x = player.x + (player.width/2 - 5);
		this.y = player.y + player.height;
		this.width = 10;
		this.height = 10;
	}
	
}
