package entity;

import java.awt.Graphics2D;

import entity.Entity;
import asteroids.Game;

public abstract class AbstractBullet extends Entity {

	public AbstractBullet(Game game, float x, float y, int width, int height) {
		super(game, x, y, width, height);
		// TODO Auto-generated constructor stub
	}

	public void update(float deltaTime) {
		super.update(deltaTime);
	}

	public void draw(Graphics2D canvas) {
		super.draw(canvas);
	}

	@Override
	protected abstract boolean onCollision(Entity entity);

}
