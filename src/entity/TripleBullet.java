package entity;

import asteroids.Game;

public class TripleBullet extends Bullet
{

	public TripleBullet(Game game, float x, float y)
	{
		super(game, x, y, 7, 15, 0, 0);

		game.spawnEntity(new Bullet(game, x, y, 7, 15, -2, game.getPlayer().getVelY()));
		game.spawnEntity(new Bullet(game, x, y, 7, 15, -0.5f, game.getPlayer().getVelY()));
		game.spawnEntity(new Bullet(game, x, y, 7, 15, 0.5f, game.getPlayer().getVelY()));
		game.spawnEntity(new Bullet(game, x, y, 7, 15, 2, game.getPlayer().getVelY()));

		collisionEnable = false;
		alive = false;
	}

}
