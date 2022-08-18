package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

import asteroids.Game;
import asteroids.Sound;
import asteroids.Textures;

public class Player extends Entity
{
	private static final int MAX_SPEED = 5;

	private static final float ACCELERATION = 0.5f;

	private static final float DECELERATION = 0.95f;

	private long flammeTime;

	private int flammeWidth;

	private int flammeIndice;

	private Image[] flammeImages = new Image[2];

	private Earth earth;

	private final Cannon cannon;

	private final FlameThrower reactorL, reactorR;

	public Player(Game game, Earth earth, float x, float y, int width, int height)
	{
		super(game, x, y, width, height);

		this.earth = earth;

		reactorL = new FlameThrower(7, 3.5f, (float) Math.PI);
		reactorR = new FlameThrower(7, 3.5f, (float) Math.PI);
		
		cannon = new Cannon(game);

		reset();
	}

	public void update(float deltaTime)
	{
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
		{

			g.setColor(Color.RED);
			game.drawImage(Textures.VAISSEAU.getImage(), x, y, width, height);

			if (System.currentTimeMillis() - flammeTime > 250)
			{
				flammeIndice = flammeIndice == 1 ? 0 : 1;
				flammeTime = System.currentTimeMillis();
			}

			flammeWidth++;
			flammeWidth %= width / 2;
			// game.drawImage(flammeImages[flammeIndice], x + width / 2 - flammeWidth / 2, y
			// + height, flammeWidth, flammeWidth);
		}

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
			if (((Asteroidv2) entity).checkPixelCollision(this.getHitbox()))
				this.alive = false;
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
