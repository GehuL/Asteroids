package entity;
import asteroids.Game;
import asteroids.Sound;
import asteroids.Textures;

public class TripleBulletItem extends BulletItem {

	public TripleBulletItem(Game game, float x, float y, int width, int height) {
		super(game, x, y, width, height, Textures.UPGRADE.getImage());
	}

	@Override
	public void shoot(Game game, float x, float y) {
		game.spawnEntity(new TripleBullet(game, x - 3, y));
		Sound.GUNSHOT.play();
	}

	@Override
	protected boolean onCollision(Entity entity) {
		// TODO Auto-generated method stub
		return true;
	}

}
