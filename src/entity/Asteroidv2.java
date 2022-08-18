package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import asteroids.Game;
import asteroids.GameUtils;
import asteroids.NoiseGenerator;

public class Asteroidv2 extends Entity
{
	private static final NoiseGenerator noiseGen = new NoiseGenerator();

	private final Pixel[][] pixels;

	public static int PIXEL_SIZE = 5; // Rect radius = 5 px in screen

	public static float speed = 0.1f;

	public Asteroidv2(Game game, float x, float y)
	{
		super(game, x, y, 0, 0);

		long asteroidSeed = Game.getRandom().nextLong();
		Random random = new Random(asteroidSeed);

		noiseGen.setSeed(random.nextDouble()); // New seed for this asteroid. generate() and colorize use it.

		System.out.println("Asteroid's seed:" + asteroidSeed);

		pixels = generate(5, random.nextInt(16 - 6) + 6, 25.);
		colorize(pixels);

		width = pixels[0].length * PIXEL_SIZE;
		height = pixels.length * PIXEL_SIZE;
	}

	private Asteroidv2(Game game, float x, float y, Pixel[][] pixels)
	{
		super(game, x, y, 0, 0);

		this.pixels = pixels;

		width = pixels[0].length * PIXEL_SIZE;
		height = pixels.length * PIXEL_SIZE;
	}

	public Pixel[][] generate(int radiusMin, int radiusMax, double noise)
	{

		// Paramètres
		int resolution = 100;

		Pixel[][] pixelsTmp = new Pixel[2 * (int) radiusMax][2 * (int) radiusMax];

		int minX = Integer.MAX_VALUE, maxX = 0;
		int minY = Integer.MAX_VALUE, maxY = 0;

		for (float angle = 0; angle <= Math.PI * 2; angle += Math.PI * 2 / resolution)
		{
			double nX = GameUtils.map(Math.cos(angle), -1., 1., 0., noise);
			double nY = GameUtils.map(Math.sin(angle), -1., 1., 0., noise);

			double n = noiseGen.noise(nX, nY);
			double nVar = GameUtils.map(n, -1., 1., (double) radiusMin, (double) radiusMax);

			int x = (int) (Math.cos(angle) * nVar + radiusMax);
			int y = (int) (Math.sin(angle) * nVar + radiusMax);

			Pixel px = new Pixel();
			pixelsTmp[y][x] = px;

			maxX = Math.max(x, maxX);
			maxY = Math.max(y, maxY);

			minX = Math.min(x, minX);
			minY = Math.min(y, minY);
		}

		// Fit table dimension to the shape dimension
		Pixel[][] pixelsDest = new Pixel[maxY - minY + 1][maxX - minX + 1];
		for (int i = 0; i < pixelsDest.length; i++)
			System.arraycopy(pixelsTmp[minY + i], minX, pixelsDest[i], 0, pixelsDest[0].length);

		floodFill(pixelsDest[0].length / 2, pixelsDest.length / 2, pixelsDest);

		return pixelsDest;
	}

	public static void floodFill(int x, int y, Pixel[][] sourcePx)
	{
		// Border detection
		if (y >= sourcePx.length || y < 0 || x >= sourcePx[0].length || x < 0 || sourcePx[y][x] != null)
			return;

		sourcePx[y][x] = new Asteroidv2.Pixel();
		sourcePx[y][x].setDropItem(Game.getRandom().nextInt(500) == 1);

		floodFill(x + 1, y, sourcePx);
		floodFill(x - 1, y, sourcePx);
		floodFill(x, y - 1, sourcePx);
		floodFill(x, y + 1, sourcePx);
	}

	// Apply a perlin noise shading on every pixels.
	private static void colorize(Pixel[][] pixels)
	{
		final double coef = 1.5d;

		for (int y = 0; y < pixels.length; y++)
		{
			for (int x = 0; x < pixels[y].length; x++)
			{
				if (pixels[y][x] != null)
				{
					if (pixels[y][x].isDropItem())
					{
						pixels[y][x].setColor(Color.YELLOW);
						continue;
					}

					double nn = noiseGen.noise((double) x * coef, (double) y * coef);
					double n = GameUtils.map(nn, -1., 1., 0., 1.);

					int r = (int) (n * Pixel.DEFAULT_COLOR.getRed());
					int g = (int) (n * Pixel.DEFAULT_COLOR.getGreen());
					int b = (int) (n * Pixel.DEFAULT_COLOR.getBlue());

					pixels[y][x].setColor(new Color(r, g, b));
				}
			}
		}
	}

	// Count how many "agglomeration" of pixel are in the pixel array.
	// One pixel alone count for a sub asteroid.
	// An agglomeration is formed when a pixel has pixel neighbor (left, right, up,
	// down, and diagonals).
	private static ArrayList<Pixel[][]> findSubAsteroid(Pixel[][] sourcePx)
	{
		ArrayList<Pixel[][]> subAsteroid = new ArrayList<>();
		Pixel[][] buffer = new Pixel[sourcePx.length][sourcePx[0].length];
		for (int i = 0; i < buffer.length; i++)
			System.arraycopy(sourcePx[i], 0, buffer[i], 0, buffer[0].length);

		for (int line = 0; line < sourcePx.length; line++)
		{
			for (int col = 0; col < sourcePx[line].length; col++)
			{
				// There is a pixel alive
				if (buffer[line][col] != null && !buffer[line][col].isDead())
				{
					Pixel[][] pixelFound = new Pixel[sourcePx.length][sourcePx[0].length];
					findSubAsteroidBis(col, line, buffer, pixelFound);

					subAsteroid.add(pixelFound);
				}
			}
		}

		return subAsteroid;
	}

	public static boolean containPixel(Pixel[][] pixels, Pixel pixel)
	{
		for (int y = 0; y < pixels.length; y++)
			for (int x = 0; x < pixels[y].length; x++)
				if (pixels[y][x] == pixel)
					return true;
		return false;
	}

	// SourcePx is modified for performance issues.
	// For each pixel found it is stored in pixelFound and removed in sourcePx for
	// performance optimization.
	private static void findSubAsteroidBis(int x, int y, Pixel[][] sourcePx, Pixel[][] pixelFound)
	{
		// Pixel detection, stop if array bounds are found or empty pixel or pixel
		if (y >= sourcePx.length || y < 0 || x >= sourcePx[0].length || x < 0 || sourcePx[y][x] == null || sourcePx[y][x].isDead())
			return;

		Pixel pixel = sourcePx[y][x];
		sourcePx[y][x] = null; // Mark it as found

		pixelFound[y][x] = pixel;

		// HORIZONTAL / VERTICAL
		findSubAsteroidBis(x - 1, y, sourcePx, pixelFound);
		findSubAsteroidBis(x + 1, y, sourcePx, pixelFound);
		findSubAsteroidBis(x, y + 1, sourcePx, pixelFound);
		findSubAsteroidBis(x, y - 1, sourcePx, pixelFound);

		// DIAGONAL
		findSubAsteroidBis(x - 1, y - 1, sourcePx, pixelFound);
		findSubAsteroidBis(x - 1, y + 1, sourcePx, pixelFound);
		findSubAsteroidBis(x + 1, y - 1, sourcePx, pixelFound);
		findSubAsteroidBis(x + 1, y + 1, sourcePx, pixelFound);
	}

	public boolean checkPixelCollision(Rectangle2D.Float rect)
	{
		boolean collide = false;
		for (int y = 0; y < pixels.length; y++)
		{
			for (int x = 0; x < pixels[y].length; x++)
			{
				Pixel pixel = pixels[y][x];
				if (pixel != null && !pixel.isDead())
				{
					Rectangle2D.Float pxRect = new Rectangle2D.Float();

					pxRect.x = (int) this.x + PIXEL_SIZE * x;
					pxRect.y = (int) this.y + PIXEL_SIZE * y;
					pxRect.width = PIXEL_SIZE;
					pxRect.height = PIXEL_SIZE;

					if (pxRect.intersects(rect))
					{
						collide = true;
					}
				}
			}
		}
		return collide;
	}

	public void dealDamage(Rectangle2D.Float rect)
	{
		for (int y = 0; y < pixels.length; y++)
		{
			for (int x = 0; x < pixels[y].length; x++)
			{
				Pixel pixel = pixels[y][x];
				if (pixel != null && !pixel.isDead())
				{
					Rectangle2D.Float pxRect = new Rectangle2D.Float();

					pxRect.x = (int) this.x + PIXEL_SIZE * x;
					pxRect.y = (int) this.y + PIXEL_SIZE * y;
					pxRect.width = PIXEL_SIZE;
					pxRect.height = PIXEL_SIZE;

					if (pxRect.intersects(rect))
					{
						pixel.removeLife();

						if (pixel.isDead())
							ItemUtils.dropRandomItem(game, this.x + x * PIXEL_SIZE - PIXEL_SIZE / 2, this.y + y * PIXEL_SIZE - PIXEL_SIZE / 2);
					}
				}
			}
		}
	}

	public void dealDamage(AbstractBullet bullet, float f, float g, float radius)
	{
		boolean isPixelDead = false; // If at least one pixel is dead to perform splitting
		for (int y1 = 0; y1 < pixels.length; y1++)
		{
			for (int x1 = 0; x1 < pixels[y1].length; x1++)
			{
				Pixel pixel = pixels[y1][x1];
				if (pixel != null && !pixel.isDead())
				{
					Rectangle2D.Float pxRect = new Rectangle2D.Float();

					pxRect.x = (int) this.x + PIXEL_SIZE * x1;
					pxRect.y = (int) this.y + PIXEL_SIZE * y1;
					pxRect.width = PIXEL_SIZE;
					pxRect.height = PIXEL_SIZE;

					float dist = (float) Math.sqrt(Math.pow(f - pxRect.getCenterX(), 2) + Math.pow(g - pxRect.getCenterY(), 2));

					if (dist < radius)
					{
						pixel.removeLife();

						if (pixel.isDead())
						{

							PixelParticle particle = new PixelParticle(game, pixel.getInitColor(), (int) pxRect.getCenterX(), (int) pxRect.getCenterY(), -bullet.getVelX(), getVelY() - bullet.getVelY() * 0.7f, 60);

							particle.velX = (float) Math.cos(Game.getRandom().nextFloat() * Math.PI / 2) * bullet.velX;

							particle.velX += Math.cos(Game.getRandom().nextFloat() * Math.PI);

							game.spawnEntity(particle);

							if (pixel.isDropItem())
								ItemUtils.dropRandomItem(game, this.x + x1 * PIXEL_SIZE - PIXEL_SIZE / 2, this.y + y1 * PIXEL_SIZE - PIXEL_SIZE / 2);

							pixels[y1][x1] = null;

							isPixelDead = true;
						}
					}
				}
			}
		}

		if (isPixelDead)
		{
			ArrayList<Pixel[][]> subList = findSubAsteroid(pixels);
			if (subList.size() > 1)
			{
				// Splitting
				for (Pixel[][] sub : subList)
				{
					Asteroidv2 a = new Asteroidv2(game, x, y, sub);
					a.velX = (float) Math.cos(Game.getRandom().nextFloat() * Math.PI / 2) * bullet.velX;

					a.velX += Math.cos(Game.getRandom().nextFloat() * Math.PI);
					a.velY = this.velY;

					game.spawnEntity(a);
				}
				this.alive = false;
			}
		}

	}

	@Override
	public void update(float deltaTime)
	{
		super.update(deltaTime);

		if (y >= game.getHeight())
		{
			alive = false;
			return;
		}
	}

	@Override
	public void draw(Graphics2D g)
	{
		for (int y = 0; y < pixels.length; y++)
			for (int x = 0; x < pixels[y].length; x++)
			{
				if (pixels[y][x] != null && pixels[y][x].life > 0)
				{
					Pixel pixel = pixels[y][x];;
					g.setColor(pixel.getColor());
					g.fillRect((int) this.x + PIXEL_SIZE * x, (int) this.y + PIXEL_SIZE * y, PIXEL_SIZE, PIXEL_SIZE);
				}
			}
		super.draw(g);
	}

	@Override
	protected boolean onCollision(Entity entity)
	{

		return false;
	}

	private static class Pixel
	{
		private boolean dropItem; // Drop item if destroyed

		private static final int DEFAULT_LIFE = 3;

		private static final Color DEFAULT_COLOR = new Color(176, 170, 157);

		private Color color, initColor;

		private int life, initLife;

		public Pixel()
		{
			this.initColor = DEFAULT_COLOR;
			this.color = DEFAULT_COLOR;
			this.life = DEFAULT_LIFE;
			this.initLife = life;
		}

		public Pixel(Color col, int life, boolean dropItem)
		{
			this.initColor = col;
			this.color = col;
			this.life = life;
			this.initLife = life;
			this.dropItem = dropItem;
		}

		public void setDropItem(boolean b)
		{
			this.dropItem = b;
		}

		public void setColor(Color col)
		{
			this.initColor = this.color = col;
		}

		public boolean isDropItem()
		{
			return dropItem;
		}

		public boolean isDead()
		{
			return life <= 0;
		}

		public Color getColor()
		{
			return color;
		}

		public void removeLife()
		{
			this.life--;
			this.color = new Color(life * initColor.getRed() / initLife, life * initColor.getGreen() / initLife, life * initColor.getBlue() / initLife);
		}

		public Color getInitColor()
		{
			return initColor;
		}
	}
}
