package entity;
import asteroids.Game;
import asteroids.Sound;
import asteroids.Textures;

public class LaserItem extends BulletItem {

	public LaserItem(Game game, float x, float y, int w, int h) {
		super(game, x, y, w, h, Textures.LASER_ITEM.getImage());
		ammount = 15;
		fireDelay = 120;
	}

	@Override
	protected void shoot(Game game, float x, float y) {
		game.spawnEntity(new LaserBullet(game));
		Sound.LASER.play();
	}

	@Override
	protected boolean onCollision(Entity entity) {
		// TODO Auto-generated method stub
		return false;
	}

}
