package net.jgnetworks.earthdefender.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import net.jgnetworks.earthdefender.EarthDefender;

public class EndScreen extends Level {

	final EarthDefender game;
	private SpriteBatch batch;
	private TextureAtlas earthDestroAtlas;
	private Animation earthDestroAnim;
	private float elapsedTime = 0;
	
	public EndScreen(final EarthDefender passedGame){
		game = passedGame;
		game.currentLevel = this;
		
		batch = new SpriteBatch();
		
		earthDestroAtlas = new TextureAtlas(Gdx.files.internal("earth/earthDestroPack.atlas"));
		earthDestroAnim = new Animation (1/6f, earthDestroAtlas.getRegions());
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
		batch.draw(earthDestroAnim.getKeyFrame(elapsedTime, true), 90, 220, 300, 280);
		batch.end();
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
