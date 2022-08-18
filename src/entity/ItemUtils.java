package entity;

import asteroids.Game;

public class ItemUtils
{	
	public static void dropRandomItem(Game game, float x, float y)
	{
		int randInt = Game.getRandom().nextInt(4);

		switch (randInt) {
		case 1:
			game.spawnEntity(new Life(game, x, y, 30, 30));
			break;
		case 2:
			game.spawnEntity(new TripleBulletItem(game, x, y, 30, 30));
			break;
		case 3:
			game.spawnEntity(new LaserItem(game, x, y));
			break;
		}

	}
}
