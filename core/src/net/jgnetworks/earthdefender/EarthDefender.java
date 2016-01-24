package net.jgnetworks.earthdefender;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import net.jgnetworks.earthdefender.level.Level;
import net.jgnetworks.earthdefender.level.MainMenuScreen;
import net.jgnetworks.earthdefender.player.Player;
import net.jgnetworks.earthdefender.projectile.Laser;
import net.jgnetworks.earthdefender.projectile.Projectile;


public class EarthDefender extends Game {
	
	public BitmapFont font;
	public OrthographicCamera camera;
	
	public EarthDefenderInput input;
	public Level currentLevel;
	private InputMultiplexer im;
	private GestureDetector gd;
	
	public Player player;
	public Array<Projectile> playerProjectiles;
	public long lastPlayerProjectile;
	
	protected Vector3 touchPos;
	public long currentTime;
	
	
	public void create () {
		font = new BitmapFont();
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 480, 720);
		
		input = new EarthDefenderInput();
		input.setGame(this);
		im = new InputMultiplexer();
		gd = new GestureDetector(input);
		im.addProcessor(gd);
		im.addProcessor(input);
		Gdx.input.setInputProcessor(im);
		
		player = new Player();
		playerProjectiles = new Array<Projectile>();
		
		currentTime = TimeUtils.nanoTime();
		touchPos = new Vector3();
		
		currentLevel = new MainMenuScreen(this);
		setScreen(currentLevel);
	}

	public void render () {
		super.render();
	}
			
	public void dispose() {
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
