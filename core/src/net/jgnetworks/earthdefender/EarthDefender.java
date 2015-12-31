package net.jgnetworks.earthdefender;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import net.jgnetworks.earthdefender.enemy.Asteroid;
import net.jgnetworks.earthdefender.player.Player;
import net.jgnetworks.earthdefender.projectile.Laser;
import net.jgnetworks.earthdefender.projectile.Projectile;


public class EarthDefender extends ApplicationAdapter {
	//Various game mechanic objects
	private SpriteBatch batch;
	private float elapsedTime = 0;
	protected OrthographicCamera camera;
	protected Vector3 touchPos;
	private Music bgm;
	private long currentTime;
	protected EarthDefenderInput input;
	
	//Player specific objects
	protected Player player;
	
	private Array<Projectile> playerProjectiles;
	private long lastPlayerProjectile;
	
	private Iterator<Projectile> playerProjItr;
	
	
	//Enemy specific objects
	private TextureAtlas asteroidTextureAtlas;
	private Animation asteroidIdleAnimation;
	private Texture asteroidDestroyTexture;
	private Array<Asteroid> asteroids;
	private long lastEnemySpawn;
	private Iterator<Asteroid> astrItr;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 480, 720);
		touchPos = new Vector3();
		currentTime = TimeUtils.nanoTime();
		
		//Create and animate player
		player = new Player();
		player.create();
		playerProjectiles = new Array<Projectile>();
		
		
		//Create and animate enemies
		asteroids = new Array<Asteroid>();
		asteroidTextureAtlas = new TextureAtlas(Gdx.files.internal("enemy/asteroid/idleanim/asteroidanimpack.atlas"));
		asteroidIdleAnimation = new Animation(1/4f, asteroidTextureAtlas.getRegions());
		asteroidDestroyTexture = new Texture(Gdx.files.internal("enemy/asteroid/asteroid_expl.png"));
		spawnEnemy();
		lastEnemySpawn = currentTime;
		lastPlayerProjectile = lastEnemySpawn;
		
		input = new EarthDefenderInput();
		input.setCaller(this);  //I feel like I'm butchering inheritance with this, should clean up
		InputMultiplexer im = new InputMultiplexer();
		GestureDetector gd = new GestureDetector(input);
		im.addProcessor(gd);
		im.addProcessor(input);
		Gdx.input.setInputProcessor(im);
		
		bgm = Gdx.audio.newMusic(Gdx.files.internal("EarthDefenderFull.mp3"));
		bgm.setLooping(true);
		bgm.play();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		float deltaTime = Gdx.graphics.getDeltaTime();
		elapsedTime+=deltaTime;
		currentTime = TimeUtils.nanoTime();
		
		//TODO move these to the input class for sanity sake, although this seems easier
		
		//Keyboard directional key movement logic
		//NOTE: Touch/mouse movement handled in overridden listeners below
		if(Gdx.input.isKeyPressed(Keys.LEFT)) 
			player.x -= 400 * deltaTime;
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) 
			player.x += 400 * deltaTime;
		
		//Keep player in bounds of screen
		if(player.x < 0)
			player.x = 0;
		if(player.x>480-player.width)
			player.x = 480-player.width;
		
		if(currentTime - lastEnemySpawn > 1000000000)
			spawnEnemy();
		
		astrItr = asteroids.iterator();
		playerProjItr = playerProjectiles.iterator();
		while(playerProjItr.hasNext()) {
			Projectile projectile = playerProjItr.next();
			projectile.y += 200 * deltaTime;
			if(projectile.y - 10 > 720)
				playerProjItr.remove();
			else{
				astrItr = asteroids.iterator();
				while(astrItr.hasNext()){
					Asteroid asteroid = astrItr.next();
					if(projectile.overlaps(asteroid)){
						asteroid.isDestroyed=true;
						asteroid.destroyedTime=currentTime;
						playerProjItr.remove();
					}
				}
			}
		}
		
		astrItr = asteroids.iterator();
		while(astrItr.hasNext()) {
			Asteroid asteroid = astrItr.next();
			asteroid.y -= 200 * deltaTime;
			if(asteroid.y + 64 < 0)
				astrItr.remove();
			else if(asteroid.overlaps(player)) {
				//destroy player
			} 
		}
		
		batch.begin();
		batch.draw(player.shipIdleAnimation.getKeyFrame(elapsedTime, true), player.x, player.y, player.width, player.height);
		
		astrItr = asteroids.iterator();
		while(astrItr.hasNext()){
			Asteroid asteroid = astrItr.next();
			if(asteroid.isDestroyed){
				batch.draw(asteroidDestroyTexture, asteroid.x, asteroid.y, 64, 64);
				if(currentTime - asteroid.destroyedTime >= 250000000)
					astrItr.remove();
					
			}else {
				batch.draw(asteroidIdleAnimation.getKeyFrame(elapsedTime, true), asteroid.x, asteroid.y, 64, 64);
			}
		}
		playerProjItr = playerProjectiles.iterator();
		while(playerProjItr.hasNext()){
			Laser laser = (Laser) playerProjItr.next();
			batch.draw(laser.animation.getKeyFrame(elapsedTime, true), laser.x, laser.y, 10, 10);
		}
		batch.end();
		
	}
			
	
	@Override
	public void dispose() {
		player.dispose();
		asteroidTextureAtlas.dispose();
		input.dispose();
		bgm.dispose();
		batch.dispose(); 
		asteroidDestroyTexture.dispose();
		playerProjItr = playerProjectiles.iterator();
		astrItr = asteroids.iterator();
		while(playerProjItr.hasNext()) {
			Projectile projectile = playerProjItr.next();
			projectile.dispose(); //disposes of textures
			playerProjItr.remove();
		}
		while(astrItr.hasNext()){
			astrItr.remove();			
		}
	}
	
	public void spawnEnemy() {
		Asteroid asteroid = new Asteroid();
		asteroid.x = MathUtils.random(0, 480 - 64);
		asteroid.y = 720;
		asteroid.width = 64;
		asteroid.height = 64;
		asteroids.add(asteroid);
		lastEnemySpawn = currentTime;
	}

	public void shoot(Rectangle shooter) {
		if(shooter == player && currentTime - lastPlayerProjectile >= 500000000){
			Laser laser = new Laser();
			laser.create();
			laser.x = player.x + (player.width/2 - 5);
			laser.y = player.y + player.height;
			laser.width = 10;
			laser.height = 10;
			playerProjectiles.add(laser);
			lastPlayerProjectile = currentTime;
		}
	}
	
	//TODO ApplicationAdapter.pause() and ApplicationAdapter.resume()
}
