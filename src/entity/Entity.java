package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import asteroids.Game;

public abstract class Entity
{
	public static float GAME_SPEED = 1f;

	protected float x, y;

	protected float velX, velY;

	protected int width, height;

	public Rectangle2D.Float hitbox;

	public boolean alive;

	protected boolean collisionEnable;

	protected Game game;

	public Entity(Game game, float x, float y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.game = game;

		hitbox = new Rectangle2D.Float(x, y, width, height);

		alive = true;

		collisionEnable = true;

	}

	public void update(float deltaTime)
	{
		float offsetX = velX * GAME_SPEED;// * deltaTime;
		float offsetY = velY * GAME_SPEED;// * deltaTime;

		x += offsetX;
		y += offsetY;

		// Every entities are teleported from left to right and vice-versa
		if (x < -width)
			x = game.width();
		else if (x > game.width())
			x = -width;

		hitbox.x = x;
		hitbox.y = y;
		hitbox.width = width;
		hitbox.height = height;

		if (collisionEnable)
		{
			ArrayList<Entity> entities = game.getEntities();
			for (int i = 0; i < entities.size(); i++)
			{
				Rectangle2D.Float rect = entities.get(i).hitbox;

				if (entities.get(i) != this && this.hitbox.intersects(rect) && entities.get(i).isCollisionEnable())
				{
					onCollision(entities.get(i));
				}
			}

		}

	};

	public void draw(Graphics2D g)
	{
		if (Game.DEBUG)
		{
			g.setColor(Color.RED);
			g.drawRect((int) hitbox.x, (int) hitbox.y, (int) hitbox.width, (int) hitbox.height);
		}
	}

	public float getX()
	{
		return x;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public Rectangle2D.Float getHitbox()
	{
		return hitbox;
	}

	public void setHitbox(Rectangle2D.Float hitbox)
	{
		this.hitbox = hitbox;
	}

	public boolean isAlive()
	{
		return alive;
	}

	public void setAlive(boolean alive)
	{
		this.alive = alive;
	}

	public boolean isCollisionEnable()
	{
		return collisionEnable;
	}

	public void setCollisionEnable(boolean collisionEnable)
	{
		this.collisionEnable = collisionEnable;
	}

	public void setX(float x)
	{
		this.x = x;
	}

	public void setY(float y)
	{
		this.y = y;
	}

	public float getY()
	{
		return y;
	}

	public float getVelY()
	{
		return velY;
	}

	public void setVelY(float velY)
	{
		this.velY = velY;
	}

	public float getVelX()
	{
		return velX;
	}

	public void setVelX(float velX)
	{
		this.velX = velX;
	}

	protected abstract boolean onCollision(Entity entity);

}
