package entity;

import java.awt.Color;
import java.awt.Graphics2D;

import asteroids.Game;

public class PixelParticle extends Entity
{
	private float alphaCoef;

	private Color color;

	private float tick;

	public static final int PIXEL_SIZE = 5;

	public PixelParticle(Game game, Color color, float x, float y, float velX, float velY, int tick)
	{
		super(game, x, y, PIXEL_SIZE, PIXEL_SIZE);
		this.collisionEnable = false;

		this.velX = velX;
		this.velY = velY;

		this.color = color;

		this.tick = tick;

		this.alphaCoef = color.getAlpha() / (float) tick;
	}

	public void update(float deltaTime)
	{
		super.update(deltaTime);

		int alpha = (int) (tick * alphaCoef) << 24;
		int col = color.getRGB() & 0x00ffffff;
		col |= alpha;
		
		color = new Color(col, true);
		
		if (tick <= 0)
			this.alive = false;

		tick -= Entity.GAME_SPEED;
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