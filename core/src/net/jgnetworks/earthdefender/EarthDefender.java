package net.jgnetworks.earthdefender;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import net.jgnetworks.earthdefender.enemy.Asteroid;
import net.jgnetworks.earthdefender.projectile.Projectile;


public class EarthDefender extends ApplicationAdapter implements InputProcessor, GestureListener {
	//Various game mechanic objects
	private SpriteBatch batch;
	private float elapsedTime = 0;
	private OrthographicCamera camera;
	private Vector3 touchPos;
	private Music bgm;
	private long currentTime;
	
	//Player specific objects
	private TextureAtlas shipTextureAtlas;
	private Animation shipIdleAnimation;
	private Rectangle player;
	private Array<Projectile> playerProjectiles;
	private long lastPlayerProjectile;
	private TextureAtlas playerLaserTex;
	private Animation playerLaserAnim;
	
	//Enemy specific objects
	private TextureAtlas asteroidTextureAtlas;
	private Animation asteroidIdleAnimation;
	private Texture asteroidDestroyTexture;
	private Array<Asteroid> asteroids;
	private long lastEnemySpawn;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 480, 720);
		touchPos = new Vector3();
		currentTime = TimeUtils.nanoTime();
		
		//Create and animate player
		shipTextureAtlas = new TextureAtlas(Gdx.files.internal("player/ship/shippack/shippack.atlas"));
		shipIdleAnimation = new Animation (1/6f, shipTextureAtlas.getRegions());
		playerLaserTex = new TextureAtlas(Gdx.files.internal("player/projectile/projectilePack.atlas"));
		playerLaserAnim = new Animation (1/3f, playerLaserTex.getRegions());
		player = new Rectangle();
		playerProjectiles = new Array<Projectile>();
		player.width = 64;
		player.height = 64;
		player.x = 480/2-player.width/2;
		player.y = 20;
		
		//Create and animate enemies
		asteroids = new Array<Asteroid>();
		asteroidTextureAtlas = new TextureAtlas(Gdx.files.internal("enemy/asteroid/idleanim/asteroidanimpack.atlas"));
		asteroidIdleAnimation = new Animation(1/4f, asteroidTextureAtlas.getRegions());
		asteroidDestroyTexture = new Texture(Gdx.files.internal("enemy/asteroid/asteroid_expl.png"));
		spawnEnemy();
		lastEnemySpawn = currentTime;
		lastPlayerProjectile = lastEnemySpawn;
		
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
		
		//Define input sources to handle keypresses and gestures
		InputMultiplexer im = new InputMultiplexer();
	    GestureDetector gd = new GestureDetector(this);
	    im.addProcessor(gd);
	    im.addProcessor(this);
        Gdx.input.setInputProcessor(im);
		
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
		
		Iterator<Projectile> playerProjItr = playerProjectiles.iterator();
		Iterator<Asteroid> astrItr = asteroids.iterator();
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
			Projectile projectile = playerProjItr.next();
			batch.draw(playerLaserAnim.getKeyFrame(elapsedTime, true), projectile.x, projectile.y, 10, 10);
		}
		batch.end();
		
	}
			
	
	@Override
	public void dispose() {
		shipTextureAtlas.dispose();
		asteroidTextureAtlas.dispose();
		bgm.dispose();
		batch.dispose();
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
			Projectile projectile = new Projectile();
			projectile.x = player.x + (player.width/2 - 5);
			projectile.y = player.y + player.height;
			projectile.width = 10;
			projectile.height = 10;
			playerProjectiles.add(projectile);
			lastPlayerProjectile = currentTime;
		}
	}
	
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		shoot(player);
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(touchPos);
		player.x = touchPos.x - player.width/2;
		
		//Keep player in bounds of screen
		if(player.x < 0)
			player.x = 0;
		if(player.x>480-player.width)
			player.x = 480-player.width;
		
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.SPACE){
			shoot(player);
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button == Buttons.RIGHT){
			shoot(player);
		}
		System.out.println(button);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	//TODO ApplicationAdapter.pause() and ApplicationAdapter.resume()
}
