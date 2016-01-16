package net.jgnetworks.earthdefender;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

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
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

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
	private InputMultiplexer im;
	private GestureDetector gd;
	
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
	
	//Textures
	private TextureAtlas laserTextureAtlas;
	private Animation laserAnimation;
	private TextureAtlas shipTextureAtlas;
	private Animation shipIdleAnimation;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 480, 720);
		touchPos = new Vector3();
		currentTime = TimeUtils.nanoTime();
		
		//Create and animate player
		player = new Player();
		playerProjectiles = new Array<Projectile>();
		loadTextures();
		
		
		//Create and animate enemies
		asteroids = new Array<Asteroid>();
		spawnEnemy();
		lastEnemySpawn = currentTime;
		lastPlayerProjectile = lastEnemySpawn;
		
		input = new EarthDefenderInput();
		input.setCaller(this);  //I feel like I'm butchering inheritance with this, should clean up
		im = new InputMultiplexer();
		gd = new GestureDetector(input);
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
		
		input.updatePos(deltaTime);
		
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
		batch.draw(shipIdleAnimation.getKeyFrame(elapsedTime, true), player.x, player.y, player.width, player.height);
		
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
			batch.draw(laserAnimation.getKeyFrame(elapsedTime, true), laser.x, laser.y, 10, 10);
		}
		batch.end();
		
	}
			
	
	@Override
	public void dispose() {
		//Timer delay thrown in to keep this method from disposing of game objects
		//while they are still in use by render method.
		float delay = 1; //seconds
		Timer.schedule(new Task(){
			@Override
			public void run() {
				bgm.dispose();
				batch.dispose(); 
				Iterator<Projectile>removePlayerProjItr = playerProjectiles.iterator();
				Iterator<Asteroid>removeAstItr = asteroids.iterator();
				while(removePlayerProjItr.hasNext()) {
					removePlayerProjItr.remove();
				}
				while(removeAstItr.hasNext()){
					removeAstItr.remove();			
				}
				asteroidDestroyTexture.dispose();
				asteroidTextureAtlas.dispose();
				shipTextureAtlas.dispose();
				laserTextureAtlas.dispose();
				input.dispose();
			}
		}, delay);
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
			Laser laser = new Laser(player);
			playerProjectiles.add(laser);
			lastPlayerProjectile = currentTime;
		}
	}
	
	public void loadTextures() {
		shipTextureAtlas = new TextureAtlas(Gdx.files.internal("player/ship/shippack/shippack.atlas"));
		shipIdleAnimation = new Animation (1/6f, shipTextureAtlas.getRegions());
		laserTextureAtlas = new TextureAtlas(Gdx.files.internal("player/projectile/projectilePack.atlas"));
		laserAnimation = new Animation (1/3f, laserTextureAtlas.getRegions());
		asteroidTextureAtlas = new TextureAtlas(Gdx.files.internal("enemy/asteroid/idleanim/asteroidanimpack.atlas"));
		asteroidIdleAnimation = new Animation(1/4f, asteroidTextureAtlas.getRegions());
		asteroidDestroyTexture = new Texture(Gdx.files.internal("enemy/asteroid/asteroid_expl.png"));
	}
	//TODO ApplicationAdapter.pause() and ApplicationAdapter.resume()
}
