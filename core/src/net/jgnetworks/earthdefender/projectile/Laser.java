package net.jgnetworks.earthdefender.projectile;

import net.jgnetworks.earthdefender.player.Player;

@SuppressWarnings("serial")
public class Laser extends Projectile{

	public Laser(Player player){
		//explicit call required to set projectile position and size
		super(player);
		//Other variables can later be created here for mechanics such as damage.
		//Right now lasers are the same a projectiles, but this will likely change
		//when powerups are added.
	
	}

}
