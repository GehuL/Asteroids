package entity;

import java.awt.Graphics2D;

import asteroids.Game;

public class AsteroidSpawner extends Entity
{

	private static final int MIN_TIME = 5000;

	private static final int MAX_TIME = 10000;

	private long asteroidTime;

	public AsteroidSpawner(Game game)
	{
		super(game, 0, 0, 0, 0);
		collisionEnable = false;

	}

	public void update(float deltaTime)
	{
		// Pas de supercall car pas besoin de hitbox
		if (System.currentTimeMillis() - asteroidTime > Game.getRandom().nextInt(MAX_TIME - MIN_TIME) + MIN_TIME)
		{
			if (createAsteroid())
				asteroidTime = System.currentTimeMillis();
		}

	}

	@Override
	public void draw(Graphics2D canvas)
	{
		super.draw(canvas);
	}

	@Deprecated
	private boolean createAsteroidOldVersion()
	{
// 		Ancienne version
		int radius = Game.getRandom().nextInt(25, 100);
		int speed = 100 / radius;
		int explodeCount = 3 * radius / 100;
		float posX = Game.getRandom().nextFloat(radius, game.width() - radius);
		Asteroid a = new Asteroid(game, posX, -radius, radius, radius, speed, explodeCount);

		for (Entity en : game.getEntitiesOf(Asteroidv2.class))
			if (en.getHitbox().intersects(a.getHitbox()))
				return false;

		game.spawnEntity(a);
		return true;
	}

	private boolean createAsteroid()
	{
		Asteroidv2 a = new Asteroidv2(game, 0, 0);
		a.x = Game.getRandom().nextInt(game.width() - a.getWidth());
		a.y = -a.getHeight();
		a.velY = Asteroidv2.speed * 2;

		for (Entity en : game.getEntitiesOf(Asteroidv2.class))
		{
			if (en.getHitbox().intersects(a.getHitbox()))
			{
				return false;
			}
		}

		game.spawnEntity(a);
		return true;
	}

	@Override
	protected boolean onCollision(Entity entity)
	{
		// TODO Auto-generated method stub
		return false;
	}

}
