package net.jgnetworks.earthdefender.level;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import net.jgnetworks.earthdefender.EarthDefender;
import net.jgnetworks.earthdefender.enemy.Asteroid;
import net.jgnetworks.earthdefender.projectile.Laser;
import net.jgnetworks.earthdefender.projectile.Projectile;

//TODO I really need to be loading textures/animations from within the object class. Implement render() method or use scene2d?

public class Level1 extends Level {
	//Various game mechanic objects
	final EarthDefender game;
	private SpriteBatch batch;
	private float elapsedTime = 0;
	public Music bgm;
	private float bgPos;
	private float midBgPos;
	private float foreBgPos;
	private long bgScroll;
	public Rectangle soundBtn;
	public enum soundStatus {off, on};
	public soundStatus currentSound;
	
	//Player specific objects
	private Iterator<Projectile> playerProjItr;
	
	//Enemy specific objects
	private TextureAtlas asteroidTextureAtlas;
	private Animation asteroidIdleAnimation;
	private Texture asteroidDestroyTexture;
	private Iterator<Asteroid> astrItr;
	public Array<Asteroid> asteroids;
	public long lastEnemySpawn;
	
	//Textures
	private TextureAtlas laserTextureAtlas;
	private Animation laserAnimation;
	private TextureAtlas shipTextureAtlas;
	private Animation shipIdleAnimation;
	private Texture bg0;
	private Texture bg1;
	private Texture bg2;
	private Texture bg3;
	private Texture bg4;
	private Texture bg5;
	private Texture bgSwap;
	public Texture soundOn;
	public Texture soundOff;
	public Texture soundCurrent;
	
	public Level1(final EarthDefender passedGame) {
		
		this.game = passedGame;
		game.currentLevel = this;
		game.input.setLevel1(this);
		batch = new SpriteBatch();
	
		loadTextures();
		
		soundBtn = new Rectangle();
		soundBtn.width = 32;
		soundBtn.height = 32;
		soundBtn.x = 475 - soundBtn.width;
		soundBtn.y = 128;
		soundCurrent = soundOff;
		currentSound = soundStatus.off;
		
		//Create and animate enemies
		asteroids = new Array<Asteroid>();
		spawnEnemy();
		
		game.lastPlayerProjectile = lastEnemySpawn;
		
		bgm = Gdx.audio.newMusic(Gdx.files.internal("EarthDefenderFull.mp3"));
		bgm.setLooping(true);
		bgm.setVolume(0);
		
		bgPos = 720;
		midBgPos = 720;
		foreBgPos = 720;
		bgScroll = game.currentTime;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(game.camera.combined);
		elapsedTime+=delta;
		
		setBg();
		
		game.input.updatePos(delta);
		
		if(game.currentTime - lastEnemySpawn > 1000000000)
			spawnEnemy();
		
		astrItr = asteroids.iterator();
		playerProjItr = game.playerProjectiles.iterator();
		while(playerProjItr.hasNext()) {
			Projectile projectile = playerProjItr.next();
			projectile.y += 200 * delta;
			if(projectile.y - 10 > 720)
				playerProjItr.remove();
			else{
				astrItr = asteroids.iterator();
				while(astrItr.hasNext()){
					Asteroid asteroid = astrItr.next();
					if(projectile.overlaps(asteroid)){
						asteroid.isDestroyed=true;
						asteroid.destroyedTime=game.currentTime;
						playerProjItr.remove();
						game.score+=10;
						game.scoreString = "Score: " + game.score;
					}
				}
			}
		}
		
		astrItr = asteroids.iterator();
		while(astrItr.hasNext()) {
			Asteroid asteroid = astrItr.next();
			asteroid.y -= 200 * delta;
			if(asteroid.y + 64 < 0)
				astrItr.remove();
			else if(asteroid.overlaps(game.player.hitBox)) {
				bgm.stop();
				game.setScreen(new EndScreen(game));
			} 
		}
		
		batch.begin();
		batch.draw(bg0, 0, bgPos);
		batch.draw(bg1, 0, bgPos-720);
		batch.draw(bg2, 0, midBgPos);
		batch.draw(bg3, 0, midBgPos-720);
		batch.draw(bg4, 0, foreBgPos);
		batch.draw(bg5, 0, foreBgPos-720);
		
		batch.draw(soundCurrent, soundBtn.x, soundBtn.y, soundBtn.width, soundBtn.height);
		
		game.font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		game.font.draw(batch, game.scoreString, 470/2-game.scoreString.length(), 700);
		
		batch.draw(shipIdleAnimation.getKeyFrame(elapsedTime, true), game.player.x, game.player.y, game.player.width, game.player.height);
		
		astrItr = asteroids.iterator();
		while(astrItr.hasNext()){
			Asteroid asteroid = astrItr.next();
			if(asteroid.isDestroyed){
				batch.draw(asteroidDestroyTexture, asteroid.x, asteroid.y, asteroid.width, asteroid.height);
				if(game.currentTime - asteroid.destroyedTime >= 250000000)
					astrItr.remove();
					
			}else {
				batch.draw(asteroidIdleAnimation.getKeyFrame(elapsedTime, true), asteroid.x, asteroid.y, asteroid.width, asteroid.height);
			}
		}
		playerProjItr = game.playerProjectiles.iterator();
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
				Iterator<Projectile>removePlayerProjItr = game.playerProjectiles.iterator();
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
			}
		}, delay);
	}
	
	private void loadTextures() {
		shipTextureAtlas = new TextureAtlas(Gdx.files.internal("player/ship/shippack/shippack.atlas"));
		shipIdleAnimation = new Animation (1/6f, shipTextureAtlas.getRegions());
		
		laserTextureAtlas = new TextureAtlas(Gdx.files.internal("player/projectile/projectilePack.atlas"));
		laserAnimation = new Animation (1/3f, laserTextureAtlas.getRegions());
		
		asteroidTextureAtlas = new TextureAtlas(Gdx.files.internal("enemy/enemyShip/enemyShipPack.atlas"));
		asteroidIdleAnimation = new Animation(1/4f, asteroidTextureAtlas.getRegions());
		asteroidDestroyTexture = new Texture(Gdx.files.internal("enemy/asteroid/asteroid_expl.png"));
		
		bg0 = new Texture(Gdx.files.internal("background/bg0.png"));
		bg1 = new Texture(Gdx.files.internal("background/bg1.png"));
		bg2 = new Texture(Gdx.files.internal("background/bg2.png"));
		bg3 = new Texture(Gdx.files.internal("background/bg2.png"));
		bg4 = new Texture(Gdx.files.internal("background/bg3.png"));
		bg5 = new Texture(Gdx.files.internal("background/bg3.png"));
		
		soundOn = new Texture(Gdx.files.internal("soundOn.png"));
		soundOff = new Texture(Gdx.files.internal("soundOff.png"));
	}

	
	private void spawnEnemy() {
		Asteroid asteroid = new Asteroid();
		asteroid.x = MathUtils.random(0, 480 - 64);
		asteroid.y = 720;
		asteroid.width = 64;
		asteroid.height = 64;
		asteroids.add(asteroid);
		lastEnemySpawn = game.currentTime;
	}
	
	private void setBg() {
		if(game.currentTime - bgScroll > 10000000) {
			bgPos -= .25;
			midBgPos -= 1;
			foreBgPos -= 4;
			bgScroll = game.currentTime;
		}
		
		if(bgPos == 0){
			bgSwap = bg0;
			bg0 = bg1;
			bg1 = bgSwap;
			bgPos  = 720;
			/* Could load next level or main menu once player reaches end of background textures; just loops for now
			game.currentLevel = new MainMenuScreen(game);
			game.setScreen(game.currentLevel);
			*/
		}
		if(midBgPos == 0){
			midBgPos = 720;
		}
		if(foreBgPos == 0){
			foreBgPos = 720;
		}
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}
}
