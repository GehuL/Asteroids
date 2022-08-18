package entity;

import java.awt.Graphics2D;

import asteroids.Game;
import asteroids.Textures;

public class Asteroid extends Entity
{

	private int speed;

	private int destroyCount;

	private int angle;

	public Asteroid(Game game, float x, float y, int width, int height, int speed, int destroyCount)
	{
		super(game, x, y, width, height);
		this.speed = speed;
		this.game = game;
		this.destroyCount = destroyCount;
		angle = 0;

		// On evite la multiplications de collisions
		collisionEnable = false; // C'est le player qui fait les collisions
	}

	@Override
	public void update(float deltaTime)
	{
		super.update(deltaTime);

		y += speed;
		if (y >= game.getHeight())
		{
			alive = false;
			return;
		}

		x += (int) (2.5 * Math.cos(angle * Math.PI / 180));
		angle += 10;
		angle %= 360;
	}

	public void destroy()
	{

		if (destroyCount > 0)
		{
			for (int i = 0; i < 2; i++)
			{ // On sépare en deux l'asteroid
				Asteroid asteroid = new Asteroid(game, x - Game.getRandom().nextInt(15) - (15 * 2),
						y - Game.getRandom().nextInt(30) - 15,
						width / 2, height / 2, 1, destroyCount - 1);
				game.spawnEntity(asteroid);
			}
		}

		ItemUtils.dropRandomItem(game, x + width / 2 - 15, y + height / 2 - 15);
		alive = false;
	}

	@Override
	public void draw(Graphics2D canvas)
	{

//		AffineTransform trans = new AffineTransform();
//		trans.setToTranslation(x, y);
//		trans.scale((double) width / (double) Textures.ASTEROID.getImage().getWidth(),
//				(double) height / (double) Textures.ASTEROID.getImage().getHeight());

		game.drawImage(Textures.ASTEROID.getImage(), x, y, width, height);
		super.draw(canvas);
	}

	@Override
	protected boolean onCollision(Entity entity)
	{
		// TODO Auto-generated method stub
		return false;
	}

}
