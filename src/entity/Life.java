package entity;
import java.awt.Graphics2D;

import asteroids.Game;
import asteroids.Textures;

public class Life extends Item {


	public Life(Game game, float x, float y, int width, int height) {
		super(game, x, y, width, height, Textures.EARTH_LIFE.getImage());
	}
	
	public void update(float deltaTime) {
		super.update(deltaTime);
	}

	public void drawItem(Graphics2D canvas) {
		game.drawImage(Textures.EARTH_LIFE.getImage(), x, y, width, height);
	}

	@Override
	protected boolean onCollision(Entity entity) {
		return false;
	}

}
