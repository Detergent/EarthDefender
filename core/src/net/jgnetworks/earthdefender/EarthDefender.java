package net.jgnetworks.earthdefender;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;


public class EarthDefender extends ApplicationAdapter {
	//Various game mechanic objects
	private SpriteBatch batch;
	private float elapsedTime = 0;
	private OrthographicCamera camera;
	private Vector3 touchPos;
	private Music bgm;
	
	//Player specific objects
	private TextureAtlas shipTextureAtlas;
	private Animation shipIdleAnimation;
	private Rectangle player;
	
	//Enemy specific objects
	private TextureAtlas asteroidTextureAtlas;
	private Animation asteroidIdleAnimation;
	private Array<Rectangle> asteroids;
	private long lastEnemySpawn;
	
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 480, 800);
		touchPos = new Vector3();
		
		//Create and animate player
		shipTextureAtlas = new TextureAtlas(Gdx.files.internal("player/ship/shippack/shippack.atlas"));
		shipIdleAnimation = new Animation (1/6f, shipTextureAtlas.getRegions());
		player = new Rectangle();
		player.width = 64;
		player.height = 64;
		player.x = 480/2-player.width/2;
		player.y = 20;
		
		
		//Create and animate enemies
		asteroids = new Array<Rectangle>();
		asteroidTextureAtlas = new TextureAtlas(Gdx.files.internal("enemy/asteroid/idleanim/asteroidanimpack.atlas"));
		asteroidIdleAnimation = new Animation(1/4f, asteroidTextureAtlas.getRegions());
		spawnEnemy();
		lastEnemySpawn = TimeUtils.nanoTime();
		
		bgm = Gdx.audio.newMusic(Gdx.files.internal("EarthDefenderFull.mp3"));
		bgm.setLooping(true);
		bgm.play();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		elapsedTime+=Gdx.graphics.getDeltaTime();
		batch.draw(shipIdleAnimation.getKeyFrame(elapsedTime, true), player.x, player.y, player.width, player.height);
		for(Rectangle asteroid : asteroids){
			batch.draw(asteroidIdleAnimation.getKeyFrame(elapsedTime, true), asteroid.x, asteroid.y, 64, 64);
		}
		batch.end();
		
		//Mouse/Touch movement logic
		if(Gdx.input.isTouched()) {
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			player.x = touchPos.x - player.width/2;
		}
		
		//Keyboard directional key movement logic
		if(Gdx.input.isKeyPressed(Keys.LEFT)) 
			player.x -= 300 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) 
			player.x += 300 * Gdx.graphics.getDeltaTime();
		
		//Keep player in bounds of screen
		if(player.x < 0)
			player.x = 0;
		if(player.x>480-player.width)
			player.x = 480-player.width;
		
		if(TimeUtils.nanoTime() - lastEnemySpawn > 1000000000)
			spawnEnemy();
		
		Iterator<Rectangle> iter = asteroids.iterator();
		while(iter.hasNext()) {
			Rectangle asteroid = iter.next();
			asteroid.y -= 200 * Gdx.graphics.getDeltaTime();
			if(asteroid.y + 64 < 0)
				iter.remove();
			if(asteroid.overlaps(player)){
				//destroy player
			}
		}
	}
	
	public void spawnEnemy() {
		Rectangle asteroid = new Rectangle();
		asteroid.x = MathUtils.random(0, 480 - 64);
		asteroid.y = 720;
		asteroid.width = 64;
		asteroid.height = 64;
		asteroids.add(asteroid);
		lastEnemySpawn = TimeUtils.nanoTime();
	}
	
	@Override
	public void dispose() {
		shipTextureAtlas.dispose();
		asteroidTextureAtlas.dispose();
		bgm.dispose();
		batch.dispose();
	}
	
	//TODO ApplicationAdapter.pause() and ApplicationAdapter.resume()
}
