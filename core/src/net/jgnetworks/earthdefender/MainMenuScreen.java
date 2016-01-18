package net.jgnetworks.earthdefender;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

import net.jgnetworks.earthdefender.level.Level1;

public class MainMenuScreen implements Screen {

	final EarthDefender game;
	
	OrthographicCamera camera;
	
	public MainMenuScreen(final EarthDefender passedGame) {
		game = passedGame;
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 480, 720);
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		
		game.batch.begin();
		game.font.draw(game.batch, "Welcome to EarthDefender!", 50, 400);
		game.font.draw(game.batch, "Welcome to EarthDefender!", 50, 350);
		game.batch.end();
		
		if(Gdx.input.isTouched()){
			game.setScreen(new Level1(game));
		}
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
