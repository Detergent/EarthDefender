package net.jgnetworks.earthdefender;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;

import net.jgnetworks.earthdefender.player.Player;

public class EarthDefender extends Game {
	
	public SpriteBatch batch;
	public BitmapFont font;
	public EarthDefenderInput input;
	public InputMultiplexer im;
	public GestureDetector gd;
	
	//Player specific objects
	protected Player player;
	
	@Override
	public void create() {
		batch = new SpriteBatch();
		font = new BitmapFont();
		
		player = new Player();
		
		input = new EarthDefenderInput();
		input.setGame(this);  //I feel like I'm butchering inheritance with this, should clean up
		im = new InputMultiplexer();
		gd = new GestureDetector(input);
		im.addProcessor(gd);
		im.addProcessor(input);
		Gdx.input.setInputProcessor(im);
		
		this.setScreen(new MainMenuScreen(this));
	}
	
	public void render() {
		super.render();
	}
	
	public void dispse() {
		batch.dispose();
		font.dispose();
		input.dispose();
	}
	
}