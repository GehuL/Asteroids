package entity;

import java.awt.Color;
import java.util.Random;

import asteroids.Game;

public class FlameThrower
{
	private int ammount;

	private float speed;

	private float angle;

	private int tick;

	// Angle en radian
	public FlameThrower(int ammount, float speed, float angle)
	{
		this.ammount = ammount;
		this.speed = speed;
		this.angle = angle;
	}

	public void update(Game game, float x, float y)
	{
		tick--;
		if (tick <= 0)
		{
			Random rand = Game.getRandom();

			tick += ammount;

			float velX = (float) Math.cos(rand.nextFloat() * angle);
			float velY = (rand.nextFloat(0.5f) + 0.5f) * speed;

			float hue = rand.nextFloat(0.166f); // Produce a color from red to yellow (60° / 360 = 0.166).
			Color col = Color.getHSBColor(hue, 1f, 1f);
			
			PixelParticle pixel = new PixelParticle(game, col, x, y, velX, velY, 30);
			game.spawnEntity(pixel);
		}
	}

}
