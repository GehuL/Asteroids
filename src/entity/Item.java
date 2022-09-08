package entity;

import java.awt.Graphics2D;
import java.awt.Image;

import asteroids.Game;

public abstract class Item extends Entity
{
	private static final int BLINK_TIME = 30;
	
	private static final int LIFE_TIME = 60 * 15; // 60 tick/s * 15

	private int lifeTick, clignoTick;

	private boolean show;

	private Image image;

	protected Item(Game game, float x, float y, int width, int height, Image image)
	{
		super(game, x, y, width, height);
		this.image = image;
		show = true;
		lifeTick = LIFE_TIME; 
	}

	public void update(float deltaTime)
	{
		super.update(deltaTime);

		lifeTick--;
		if (lifeTick <= 0)
		{
			alive = false;
			return;
		}

		if (lifeTick <= 60 * 5)
			if (clignoTick-- <= 0)
			{
				clignoTick = BLINK_TIME;
				show = !show;
			}

	}

	public void draw(Graphics2D g)
	{
		if (show)
			game.drawImage(image, x, y, width, height);
		super.draw(g);
	}

	public Image getImage()
	{
		return image;
	}
}
