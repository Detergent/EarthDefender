package net.jgnetworks.earthdefender.enemy;

import com.badlogic.gdx.math.Rectangle;

@SuppressWarnings("serial")
public class Enemy extends Rectangle {
	public boolean isDestroyed = false;
	public long destroyedTime = 0;
}
