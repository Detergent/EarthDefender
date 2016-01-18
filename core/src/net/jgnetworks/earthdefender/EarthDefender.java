package net.jgnetworks.earthdefender;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import net.jgnetworks.earthdefender.player.Player;
import net.jgnetworks.earthdefender.projectile.Laser;
import net.jgnetworks.earthdefender.projectile.Projectile;


public class EarthDefender extends Game {
	
	SpriteBatch batch;
	public BitmapFont font;
	public EarthDefenderInput input;
	private InputMultiplexer im;
	private GestureDetector gd;
	public long currentTime;
	public Player player;
	public Array<Projectile> playerProjectiles;
	public long lastPlayerProjectile;
	protected Vector3 touchPos;
	
	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont();
		
		input = new EarthDefenderInput();
		input.setGame(this);
		im = new InputMultiplexer();
		gd = new GestureDetector(input);
		im.addProcessor(gd);
		im.addProcessor(input);
		Gdx.input.setInputProcessor(im);
		
		touchPos = new Vector3();
		
		player = new Player();
		playerProjectiles = new Array<Projectile>();
		
		currentTime = TimeUtils.nanoTime();
		
		this.setScreen(new MainMenuScreen(this));
	}

	public void render () {
		super.render();
	}
			
	public void dispose() {
		batch.dispose();
		font.dispose();
	}
	
	public void shoot(Rectangle shooter) {
		if(shooter == player && currentTime - lastPlayerProjectile >= 500000000){
			Laser laser = new Laser(player);
			playerProjectiles.add(laser);
			lastPlayerProjectile = currentTime;
		}
	}
}
