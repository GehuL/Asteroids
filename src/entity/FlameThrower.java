package entity;

import java.awt.Color;
import java.util.Random;

import asteroids.Game;

public class FlameThrower
{
	private int spawnTick;

	private float speed;

	private float angle;

	private float tick;

	// Angle en radian
	public FlameThrower(int spawnTick, float speed, float angle)
	{
		this.spawnTick = spawnTick;
		this.speed = speed;
		this.angle = angle;
	}

	public void update(Game game, float x, float y)
	{
		tick -= 1 * Entity.GAME_SPEED;
		if (tick <= 0)
		{
			Random rand = Game.getRandom();

			tick = spawnTick;

			float velX = (float) Math.cos(rand.nextFloat() * angle);
			float velY = (rand.nextFloat(0.5f) + 0.5f) * speed;

			float hue = rand.nextFloat(0.13f) + 0.48f;// Produce a color from red to yellow (60° / 360 = 0.166).
			Color col = Color.getHSBColor(hue, 1f, 1f);

			PixelParticle pixel = new PixelParticle(game, col, x, y, velX, velY, 30);
			game.spawnEntity(pixel);
		}
	}

}
