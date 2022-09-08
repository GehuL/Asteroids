package entity;
import asteroids.Game;
import asteroids.Sound;

public class BasicBulletItem extends BulletItem {

	public static final int default_width = 7;
	public static final int default_height = 7;
	
	public BasicBulletItem(Game game) {
		super(game, 0, 0, 0, 0, null);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void shoot(Game game, float x, float y) {
		Bullet bullet = new Bullet(game, x - default_width / 2, y - default_height / 2, default_width, default_height,  game.getPlayer().getVelX(), game.getPlayer().getVelY());
		game.spawnEntity(bullet);
		Sound.GUNSHOT.play(); 
	}

	@Override
	protected boolean onCollision(Entity entity) {
		// TODO Auto-generated method stub
		return false;
	}

}
