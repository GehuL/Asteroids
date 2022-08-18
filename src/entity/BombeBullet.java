package entity;
import java.awt.Graphics2D;

import asteroids.Game;
import asteroids.Textures;

public class BombeBullet extends Bullet {

	int angle;

	public BombeBullet(Game game, float x, float y) {
		super(game, x, y, 30, 30, 0f, 0f);
		// TODO Auto-generated constructor stub
	}

	public void update(float deltaTime) {
		super.update(deltaTime);
		int d = (int) (2.5 * Math.cos(angle * Math.PI / 180));
		height += d;
		width += d;

		x -= d;

		angle += 5;
		angle %= 360;
	}

	public void draw(Graphics2D g) {
		game.drawImage(Textures.BOMBE_ITEM.getImage(), x, y, width, height);
	}

	public boolean onCollision(Entity entity) {
		super.onCollision(entity);
		if (entity instanceof Asteroid) {
			this.alive = true;
		}
		return true;
	}

}
