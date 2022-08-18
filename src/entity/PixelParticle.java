package entity;

import java.awt.Color;
import java.awt.Graphics2D;

import asteroids.Game;

public class PixelParticle extends Entity
{
	private float alphaCoef;

	private Color color;

	private int tick;
	
	public static final int PIXEL_SIZE = 5;

	public PixelParticle(Game game, Color color, float x, float y, float velX, float velY, int tick)
	{
		super(game, x, y, PIXEL_SIZE, PIXEL_SIZE);

		this.velX = velX;
		this.velY = velY;

		this.collisionEnable = false;
		this.color = color;

		this.tick = tick;

		this.alphaCoef = color.getAlpha() / (float) tick;
	}

	public void update(float deltaTime)
	{
		super.update(deltaTime);

		color = new Color(color.getRed(), color.getBlue(), color.getGreen(), (int) (tick * alphaCoef));

		if (tick <= 0)
			this.alive = false;

		tick--;
	}

	public void draw(Graphics2D g)
	{
		g.setColor(color);
		g.fillRect((int) x, (int) y, width, height);

		super.draw(g);
	}

	@Override
	protected boolean onCollision(Entity entity)
	{
		return false;
	}

	public Color getColor()
	{
		return color;
	}
}