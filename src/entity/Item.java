package entity;
import java.awt.Graphics2D;
import java.awt.Image;

import asteroids.Game;

public abstract class Item extends Entity {

	private long lifeTime;

	private long clignoTime;

	private boolean show;

	private Image image;

	protected Item(Game game, float x, float y, int width, int height, Image image) {
		super(game, x, y, width, height);
		show = true;
		lifeTime = System.currentTimeMillis();
		this.image = image;
	}

	public void update(float deltaTime) {
		super.update(deltaTime);

		// Dur�e de vie de 15 secondes
		if (System.currentTimeMillis() - lifeTime > 15000) {
			alive = false;
			return;
		}

		// Clignotement apr�s 10 secondes
		if (System.currentTimeMillis() - lifeTime > 10000) {
			if (System.currentTimeMillis() - clignoTime > 250) {
				clignoTime = System.currentTimeMillis();
				show = !show;
			}
		}

	}

	public void draw(Graphics2D g) {
		if (show)
			game.drawImage(image, x, y, width, height);
		super.draw(g);
	}

	public Image getImage() {
		return image;
	}
}
