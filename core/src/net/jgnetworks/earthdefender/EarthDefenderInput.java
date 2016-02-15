package net.jgnetworks.earthdefender;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;

import net.jgnetworks.earthdefender.level.EndScreen;
import net.jgnetworks.earthdefender.level.Level1;
import net.jgnetworks.earthdefender.level.Level1.soundStatus;
import net.jgnetworks.earthdefender.level.MainMenuScreen;

//TODO Clean up setLevel once I have the Level class more fleshed out (see note on line 21)

public class EarthDefenderInput implements InputProcessor, GestureListener {
	private EarthDefender game;
	private boolean holdingLeft = false;
	private boolean holdingRight = false;
	
	//This is not the way to do this... Level should likely have all of the functions this
	//class would ever need to access so I'm not keeping track of different
	private MainMenuScreen menuScreen;
	private EndScreen endScreen;
	private Level1 level1;
	
	
	public void setLevel1(Level1 passedLevel1) {
		level1 = passedLevel1;
	}
	
	public void setMenuScreen(MainMenuScreen passedMenu) {
		menuScreen = passedMenu;
	}
	
	public void setEndScreen(EndScreen passedEnd) {
		endScreen = passedEnd;
	}
	
	public void setGame(EarthDefender passedGame) {
		game = passedGame;
	}
	
	public void updatePos(float deltaTime){
		//Keyboard directional key movement logic
		//NOTE: Touch/mouse movement handled in overridden listeners below
		if(holdingLeft) 
			game.player.x -= 400 * deltaTime;
		else if(holdingRight)
			game.player.x += 400 * deltaTime;
				
		//Keep player in bounds of screen
		if(game.player.x < 0)
			game.player.x = 0;
		if(game.player.x>480-game.player.width)
			game.player.x = 480-game.player.width;
	}
	
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		if(game.currentLevel instanceof Level1){
			game.touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			game.camera.unproject(game.touchPos);
			if(level1.soundBtn.contains(game.touchPos.x, game.touchPos.y)){
				if(level1.currentSound == soundStatus.off){
					if(!level1.bgm.isPlaying()){
						level1.bgm.play();
					}
					level1.currentSound = soundStatus.on;
					level1.soundCurrent = level1.soundOn;
					level1.bgm.setVolume(1);
				}
				else {
					level1.currentSound = soundStatus.off;
					level1.soundCurrent = level1.soundOff;
					level1.bgm.setVolume(0);
				}
			}
			else{
				game.shoot(game.player);
			}
		}
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
		game.touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		game.camera.unproject(game.touchPos);
		game.player.x = game.touchPos.x - game.player.width/2;
		game.player.hitBox.x = game.player.x + (game.player.x/2);
		
		//Keep player in bounds of screen
		if(game.player.x < 0)
			game.player.x = 0;
		if(game.player.x>480-game.player.width)
			game.player.x = 480-game.player.width;
		
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
			game.shoot(game.player);
			System.out.println("Should shoot");
		}
		else if(keycode == Keys.LEFT)
			holdingLeft = true;
		else if(keycode == Keys.RIGHT)
			holdingRight = true;
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Keys.LEFT)
			holdingLeft = false;
		else if(keycode == Keys.RIGHT)
			holdingRight = false;
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(game.currentLevel instanceof MainMenuScreen){
			game.touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			game.camera.unproject(game.touchPos);
			if(menuScreen.startBtn.contains(game.touchPos.x, game.touchPos.y)){
				game.setScreen(new Level1(game));
			}
			else if(menuScreen.endAnimBtn.contains(game.touchPos.x, game.touchPos.y)){
				game.setScreen(new EndScreen(game));
			}
			return true;
		}
		else if(game.currentLevel instanceof EndScreen){
			game.touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			game.camera.unproject(game.touchPos);
			if(endScreen.menuBtn.contains(game.touchPos.x, game.touchPos.y)){
				game.setScreen(new MainMenuScreen(game));
			}
			return true;
		}
		//Player in active level
		else{
			if(button == Buttons.RIGHT){
				game.shoot(game.player);
			}
			System.out.println(button);
			return false;
		}	
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
}
