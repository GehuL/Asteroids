package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

import asteroids.Game;
import asteroids.Textures;

public class Bullet extends AbstractBullet
{
	private static final float SPEED = 5f;

	protected Game game;

	public Bullet(Game game, float x, float y, int width, int height, float velX, float velY)
	{
		super(game, x, y, width, height);
		this.game = game;

		this.velX = velX; // Inherited from thrower
		this.velY = -SPEED; //Math.min(velY - MIN_VELY, -MIN_VELY); // Goes up
	}

	@Override
	public void update(float deltaTime)
	{
		super.update(deltaTime);

		if (y < 0)
		{// || x < -width || x > game.getWidth()) {
			alive = false;
			return;
		}
	}

	@Override
	public void draw(Graphics2D canvas)
	{
		canvas.setColor(Color.BLUE);
		game.drawImage(Textures.BULLET.getImage(), x, y, width, height);
		// canvas.fillRoundRect(x, y, width, height, 10, 10);
		super.draw(canvas);
	}

	protected boolean onCollision(Entity entity)
	{
		if (entity instanceof Asteroid)
		{

			Asteroid a = (Asteroid) entity;

			this.alive = false;

			a.destroy();

			// Créer une explosion au centre de la zone d'intersection.
			Rectangle2D.Float rect = (Float) hitbox.createIntersection(entity.hitbox);
			game.spawnEntity(new Explosion(game, (float) rect.getCenterX() - a.width / 2,
					(float) rect.getCenterY() - a.width / 2, a.width, a.width));

			Game.score++;
		} else if (entity instanceof Asteroidv2)
		{
			Asteroidv2 a = (Asteroidv2) entity;
			if (a.checkPixelCollision(getHitbox()))
			{
				a.dealDamage(this, (float) getHitbox().getCenterX(), (float) getHitbox().getCenterY(), getWidth() * 1.7f);
				game.spawnEntity(new Explosion(game, this.x - 5, this.y - 5, width + 10, width + 10));
				this.alive = false;
			}
		}

		return false;
	}

}
