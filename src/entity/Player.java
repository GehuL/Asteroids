package entity;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

import asteroids.Game;
import asteroids.Sound;
import asteroids.Textures;

public class Player extends Entity
{
	// private static final int MAX_SPEED = 5;

	public int layer = 1;

	private float lastX;

	private float lastY;

	private static final float ACCELERATION = 0.35f;

	private static final float DECELERATION = 0.95f;

	private Earth earth;

	private final Cannon cannon;

	private final FlameThrower reactorL, reactorR;

	public Player(Game game, Earth earth, float x, float y, int width, int height)
	{
		super(game, x, y, width, height);

		this.earth = earth;

		reactorL = new FlameThrower(6, 3.5f, (float) Math.PI);
		reactorR = new FlameThrower(6, 3.5f, (float) Math.PI);

		cannon = new Cannon(game);

		reset();

	}

	public void setLayer(int layer)
	{
		layer = layer < 0 ? 0 : layer > 2 ? 2 : layer;
		this.layer = layer;
		int newSize;
		switch (this.layer)
		{
		case 0:
			newSize = 50;
			break;
		case 1:
			newSize = 60;
			break;
		case 2:
			newSize = 70;
			break;
		default:
			this.layer = layer;
			newSize = 50;
		}
		x -= (newSize - width) / 2;
		y -= (newSize - height) / 2;
		height = width = newSize;
	}

	public void update(float deltaTime)
	{
		lastX = x;
		lastY = y;

		super.update(deltaTime);

		int horizontal = Boolean.compare(game.input.keyDown(KeyEvent.VK_D), game.input.keyDown(KeyEvent.VK_Q));
		int vertical = Boolean.compare(game.input.keyDown(KeyEvent.VK_S), game.input.keyDown(KeyEvent.VK_Z));

		velX += ACCELERATION * horizontal;
		velY += ACCELERATION * vertical;

		velX *= DECELERATION;
		velY *= DECELERATION;

		if (game.input.keyDown(KeyEvent.VK_SPACE))
		{
			cannon.shoot(game, x + width / 2, y);
		}

		if (y > game.height() - height)
			y = game.height() - height;
		else if (y < 0)
			y = 0;

		reactorL.update(game, x + 10, y + height); // LEFT
		reactorR.update(game, x + 35, y + height); // LEFT
	}

	public void draw(Graphics2D g)
	{
		if (alive)
			game.drawImage(Textures.VAISSEAU.getImage(), x, y, width, height);

		cannon.draw(g);

		super.draw(g);
	}

	public boolean onCollision(Entity entity)
	{
		if (entity instanceof Asteroid)
		{
			this.alive = false;

			Rectangle2D.Float rect = (Float) hitbox.createIntersection(entity.hitbox);
			game.spawnEntity(new Explosion(game, (float) rect.getCenterX() - width / 2, (float) rect.getCenterY() - width / 2, width, height));

		} else if (entity instanceof Life)
		{
			entity.alive = false;
			earth.addLife();
			Sound.HEAL.play();
		} else if (entity instanceof BulletItem)
		{
			cannon.reload((BulletItem) (entity));
			entity.alive = false;
			Sound.RELOAD.play();
		} else if (entity instanceof Asteroidv2)
		{
			Asteroidv2 asteroid = (Asteroidv2) entity;
			Rectangle2D.Float collisionRect = asteroid.getPixelCollisionRect(hitbox);
			if (collisionRect != null)
			{
//				if (collisionRect.x < this.y || collisionRect.getCenterY() > this.y + width)
//					this.velY = -1;
//
//				final float bouncing = 1.f;
//
//				this.velX = asteroid.velX - velX * bouncing;
//				this.velY = asteroid.velY - velY * bouncing;

//				int playerMasse = 5;
//				int asteroidMasse = 30;
//				
//				float masseFactor = (playerMasse - asteroidMasse) / (playerMasse + asteroidMasse);
//				float masseFactor2 = 2 * asteroidMasse / (playerMasse - asteroidMasse);
//				
//				this.velX = masseFactor * velX + masseFactor2 * asteroid.velX;
//				this.velY = masseFactor * velY + masseFactor2 * asteroid.velY;
			}
		}
		return false;
	}

	public void reset()
	{
		alive = true;
		cannon.reload(new BasicBulletItem(game));
		velX = 0;
		velY = 0;
		x = game.width() / 2 - width / 2;
		y = game.height() / 2 - height / 2;

	}

//	public void moveLeft() {
//		velocityX -= ACCELERATION;
//		velocityX = Math.max(-MAX_SPEED, velocityX);
//	}
//
//	public void moveRight() {
//		velocityX += ACCELERATION;
//		velocityX = Math.min(MAX_SPEED, velocityX);
//	}
//
//	public void moveUp() {
//		velocityY -= ACCELERATION;
//		velocityY = Math.max(-MAX_SPEED, velocityY);
//	}
//
//	public void moveDown() {
//		velocityY += ACCELERATION;
//		velocityY = Math.min(MAX_SPEED, velocityY);
//	}

}
