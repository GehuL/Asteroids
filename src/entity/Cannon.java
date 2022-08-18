package entity;

import java.awt.Color;
import java.awt.Graphics2D;

import asteroids.Game;

public class Cannon {

	private static final long BLINK_TIME = 350; // Millisecond

	private BulletItem bulletItem;

	private long shootTime, warningTime;

	private int ammountInit; // Quantité de munition de base

	private boolean blink;

	private Game game;
	
	private static final Color text_yellow = new Color(244, 208, 63);
	
	public Cannon(Game game) {
		this.game = game;
	}

	public void reload(BulletItem bulletItem) {
		this.bulletItem = bulletItem;
		ammountInit = bulletItem.getAmmount();
	}

	public void shoot(Game game, float x, float y) {
		if (System.currentTimeMillis() - shootTime > bulletItem.getFireDelay()) {
			bulletItem.invoke(game, x, y);
			shootTime = System.currentTimeMillis();
		}
	}

	public void draw(Graphics2D g) {
		drawAmmos(g, 10, game.height() / 2);
	}

	private void drawAmmos(Graphics2D g, int x, int y) {

		final int width = 30, height = 100;
		final float coef = 0.18f; // X gap dans le drawRect

		final int step = height - height * bulletItem.getAmmount() / ammountInit + 5;
		final int y2 = y + step;
		final int height2 = height - step - 5;

		// Ammunition amount progress
		if (bulletItem.getAmmount() / (float) ammountInit <= 0.30) // less than 20% remaining
		{
			if (System.currentTimeMillis() - warningTime > BLINK_TIME) {
				blink = !blink;
				warningTime = System.currentTimeMillis();
			}
		} else
			blink = false;

		g.setColor(Color.blue);
		
		// Bullet item slot
		final int ovalY = y - width - 5;
		g.drawOval(x, ovalY, width, width);

		// Ammos border
		if (blink)
			g.setColor(Color.red);
		g.drawRect(x, y, width, height);

		g.setColor(Color.CYAN);
		if (blink)
			g.setColor(Color.red);

		g.fillRect((int) (x + width * coef) + 1, y2, (int) (width - width * coef * 2), height2);

		// Ammunition amount text
		g.setColor(text_yellow);
		g.drawString(bulletItem.getAmmount() + "/" + ammountInit, x, y + height + 10);

		// Bullet's image type
		final int imgW = (int) (width * 0.8);
		g.drawImage(bulletItem.getImage(), x + width / 2 - imgW / 2, ovalY + width / 2 - imgW / 2, imgW, imgW, null);
	}
	
	public BulletItem getBulletType()
	{
		return bulletItem;
	}

}
