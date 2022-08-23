package entity;

import java.awt.Graphics2D;

import asteroids.Game;
import asteroids.Textures;

public class Earth extends Entity {

	private int lifes = 5;

	public Earth(Game game) {
		super(game, 0, game.getHeight(), game.getWidth(), 200);
	}

	public void update(float deltaTime) {
		super.update(deltaTime);
	}

	@Override
	public void draw(Graphics2D canvas) {
		canvas.drawImage(Textures.EARTH.getImage(), 0, game.height() - height, game.width(), height + 100, null);
		for (int i = 0; i < lifes; i++) {
			canvas.drawImage(Textures.EARTH_LIFE.getImage(), 10 + i * 40, game.height() - 70, 40, 40, null);
		}
		super.draw(canvas);
	}

	public boolean onCollision(Entity entity) {
		if (entity instanceof Asteroid || entity instanceof Asteroidv2) {
			dropLife();
			entity.alive = false;
			game.spawnEntity(new Explosion(game, entity.x, entity.y, entity.width, entity.width));
		}
		return false;
	}

	public void dropLife() {
		lifes = lifes == 0 ? 0 : lifes - 1;
	}

	public boolean isDead() {
		return lifes <= 0;
	}

	public void reset() {
		lifes = 5;
	}

	public void addLife() {
		lifes = lifes == 5 ? 5 : lifes + 1; 
	}

}
