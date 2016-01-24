package net.jgnetworks.earthdefender.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;

import net.jgnetworks.earthdefender.EarthDefender;

public class EndScreen extends Level {

	final EarthDefender game;
	
	private SpriteBatch batch;
	private TextureAtlas earthDestroAtlas;
	private Animation earthDestroAnim;
	private Texture background;
	private Texture menuBtnTexture;
	private Texture titleImage;
	
	public Rectangle menuBtn;
	
	private float elapsedTime = 0;
	
	public EndScreen(final EarthDefender passedGame){
		game = passedGame;
		game.currentLevel = this;
		game.input.setEndScreen(this);
		
		batch = new SpriteBatch();
		
		menuBtn = new Rectangle();
		menuBtn.width = 170;
		menuBtn.height = 44;
		menuBtn.x = 480/2 - menuBtn.width/2;
		menuBtn.y = 400;
		
		loadTextures();
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		game.camera.update();
		batch.setProjectionMatrix(game.camera.combined);
		
		batch.begin();
		elapsedTime+=delta;
		batch.draw(background, 0, 0);
		if(!(earthDestroAnim.isAnimationFinished(elapsedTime)))
			batch.draw(earthDestroAnim.getKeyFrame(elapsedTime, true), 0, 0, 480, 720);
		else {
			batch.draw(menuBtnTexture, menuBtn.x, menuBtn.y);
			batch.draw(titleImage, 480/2 - 300/2, menuBtn.y+70);
		}
		batch.end();
	}

	public void loadTextures() {
		earthDestroAtlas = new TextureAtlas(Gdx.files.internal("earth/earthDestroPack.atlas"));
		earthDestroAnim = new Animation (1/6f, earthDestroAtlas.getRegions());
		background = new Texture(Gdx.files.internal("background.png"));
		titleImage = new Texture(Gdx.files.internal("title.png"));
		menuBtnTexture = new Texture(Gdx.files.internal("menubutton.png"));
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

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
