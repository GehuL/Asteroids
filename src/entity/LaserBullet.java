package entity;

import java.awt.Color;
import java.awt.Graphics2D;

import asteroids.Game;

public class LaserBullet extends AbstractBullet {

	public LaserBullet(Game game) {
		super(game, 0, 0, 50, 0);
	}

	public void update(float deltaTime) {
		super.update(deltaTime);

		Player p = game.getPlayer();
		x = p.getX() + p.width / 2 - width / 2;
		height = (int) p.y;

		width--;
		if (width <= 0)
			alive = false;
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.pink);
		g.fillRoundRect((int) x, (int) y, width, height, 30, 30);
		// g.drawImage(Textures.TRIPLE_BULLET.getImage(), x, y, width, height, null);
	}

	public boolean onCollision(Entity entity) {
		if (entity instanceof Asteroid) {

			Asteroid a = (Asteroid) entity;

			game.spawnEntity(new Explosion(game, a.x, a.y, a.width, a.height));

			a.destroy();

			Game.score++;
		}else if(entity instanceof Asteroidv2)
		{
			Asteroidv2 a = (Asteroidv2) entity;
			if (a.checkPixelCollision(getHitbox()))
			{
				a.dealDamage(this, (float) getHitbox().getCenterX(), (float) getHitbox().getCenterY(), getWidth() * 1.7f);
				game.spawnEntity(new Explosion(game, this.x - 5, this.y - 5, width + 10, width + 10));
				this.alive = false;
			}
		}
		return true;
	}

}
