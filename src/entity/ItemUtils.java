package entity;

import asteroids.Game;

public class ItemUtils
{
	public static void dropRandomItem(Game game, float x, float y)
	{
		int randInt = Game.getRandom().nextInt(3);

		switch (randInt)
		{
		case 0:
			game.spawnEntity(new Life(game, x, y, 20, 20));
			break;
		case 1:
			game.spawnEntity(new TripleBulletItem(game, x, y, 20, 20));
			break;
		case 2:
			game.spawnEntity(new LaserItem(game, x, y, 20, 20));
			break;
		}

	}
}
