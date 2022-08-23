package entity;

import java.awt.Graphics2D;

import asteroids.Game;
import asteroids.Sound;
import asteroids.Sprite;
import asteroids.Textures;

public class Explosion extends Entity {

	private Sprite explosion;
	
	public Explosion(Game game, float x, float y, int width, int height) {
		super(game, x, y, width, height);
		collisionEnable = false;
		explosion = new Sprite(Textures.EXPLOSION.getImage(), 16);

		Sound.EXPLOSION.play();
	}

	public void draw(Graphics2D g) {
		explosion.update();
		explosion.draw(g, x, y, width, height);
		if (explosion.index == 16)
			alive = false;
		
		super.draw(g);
	}

	@Override
	protected boolean onCollision(Entity entity) {
		// TODO Auto-generated method stub
		return false;
	}

}
