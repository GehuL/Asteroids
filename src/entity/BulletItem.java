package entity;
import java.awt.Image;

import asteroids.Game;

public abstract class BulletItem extends Item {

	protected int ammount = 10000;
	protected int fireDelay = 5; // TICK

	public BulletItem(Game game, float x, float y, int width, int height, Image image) {
		super(game, x, y, width, height, image);
	}

	public void invoke(Game game, float x, float y) {
		if (ammount > 0) {
			shoot(game, x, y);
			ammount--;
		}
	}

	protected abstract void shoot(Game game, float x, float y);

	public int getAmmount() {
		return ammount;
	}

	public int getFireDelay() {
		return fireDelay;
	}
}
