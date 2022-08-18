package entity;
import java.awt.Graphics2D;

import asteroids.Game;
import asteroids.Sound;
import asteroids.Textures;

public class LaserItem extends BulletItem {

	public LaserItem(Game game, float x, float y) {
		super(game, x, y, 30, 30, Textures.LASER_ITEM.getImage());
		ammount = 15;
		fireDelay = 1500;
	}

	@Override
	protected void shoot(Game game, float x, float y) {
		game.spawnEntity(new LaserBullet(game));
		Sound.LASER.play();
	}

	public void update(float deltaTime) {
		super.update(deltaTime);
	}

	public void draw(Graphics2D g) {
		game.drawImage(Textures.LASER_ITEM.getImage(), x, y, width, height);
	}

	@Override
	protected boolean onCollision(Entity entity) {
		// TODO Auto-generated method stub
		return false;
	}

}
