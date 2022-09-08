package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import asteroids.Game;
import asteroids.GameUtils;
import asteroids.NoiseGenerator;
import util.FloodFill;

public class Asteroidv2 extends Entity
{
	private static final NoiseGenerator noiseGen = new NoiseGenerator();

	private final Pixel[][] pixels;

	public static int PIXEL_SIZE = 5; // Rect radius = 5 px in screen

	public static float speed = 0.1f;

	// Used with asteroid generator
	public Asteroidv2(Game game, float x, float y)
	{
		super(game, x, y, 0, 0);

		long seed = Game.getRandom().nextLong();
		Random random = new Random(seed);

		noiseGen.setSeed(random.nextDouble()); // New seed for this asteroid. generate() and colorize use it.

		pixels = generate(5, random.nextInt(10) + 6, 25.);

		width = pixels[0].length * PIXEL_SIZE;
		height = pixels.length * PIXEL_SIZE;

		String info = String.format("Asteroid généré (pixels: %d, seed: %s).", pixels.length * pixels[0].length, seed);
		System.out.println(info);
	}

	// Used when asteroid is split
	private Asteroidv2(Game game, float x, float y, Pixel[][] pixels)
	{
		super(game, x, y, 0, 0);

		this.pixels = pixels;

		width = pixels[0].length * PIXEL_SIZE;
		height = pixels.length * PIXEL_SIZE;

		System.out.println("Asteroid crée par transfert (" + pixels.length * pixels[0].length + " pixels).");
	}

	// TEST (for structure génération ?)
	public Asteroidv2(Game game, float x, float y, BufferedImage image)
	{
		super(game, x, y, 0, 0);
		pixels = fromImage(image);

		width = pixels[0].length * PIXEL_SIZE;
		height = pixels.length * PIXEL_SIZE;

		System.out.println("Asteroid crée par image (" + pixels.length * pixels[0].length + " pixels).");
	}

	public static Pixel[][] fromImage(BufferedImage image)
	{
		Pixel[][] pxArray = new Pixel[image.getHeight()][image.getWidth()];
		for (int line = 0; line < pxArray.length; line++)
			for (int col = 0; col < pxArray[line].length; col++)
			{
				int argb = image.getRGB(col, line);
				if (((argb >> 24) & 0xff) > 254)
				{
					Pixel px = new Pixel(new Color(argb, true), 3, false);
					pxArray[line][col] = px;
				}
			}
		return shrinkAsteroid(pxArray, null);
	}

	// There is no longer pixel alive
	public boolean isDead()
	{
		boolean dead = true;
		for (int line = 0; line < pixels.length; line++)
			for (int col = 0; col < pixels[line].length; col++)
				if (pixels[line][col] != null && !pixels[line][col].isDead())
					dead = false;
		return dead;
	}

	public Pixel[][] generate(int radiusMin, int radiusMax, double noise)
	{
		int resolution = 100;

		Pixel[][] pixelsTmp = new Pixel[2 * (int) radiusMax][2 * (int) radiusMax];

		for (float angle = 0; angle <= Math.PI * 2; angle += Math.PI * 2 / resolution)
		{
			double nX = GameUtils.map(Math.cos(angle), -1., 1., 0., noise);
			double nY = GameUtils.map(Math.sin(angle), -1., 1., 0., noise);

			double n = noiseGen.noise(nX, nY);
			double nVar = GameUtils.map(n, -1., 1., (double) radiusMin, (double) radiusMax - 1);

			int x = (int) (Math.cos(angle) * nVar + radiusMax);
			int y = (int) (Math.sin(angle) * nVar + radiusMax);

			pixelsTmp[y][x] = Pixel.DEFAULT_PIXEL;
		}

		// Fit table dimension to the shape dimension
		Pixel[][] pixelsDest = shrinkAsteroid(pixelsTmp, null);

		// Tryed to do colorizing in floodfill, but there is sometime pixel miss...
		floodFill(pixelsDest[0].length / 2, pixelsDest.length / 2, pixelsDest);

		// Coloring every pixel
		for (int line = 0; line < pixelsDest.length; line++)
			for (int col = 0; col < pixelsDest[line].length; col++)
				if (pixelsDest[line][col] != null)
					colorize(col, line, pixelsDest);

		return pixelsDest;
	}

	public static void floodFill(int x, int y, Pixel[][] sourcePx)
	{
		// Border detection
		boolean OutOfBound = y >= sourcePx.length || y < 0 || x >= sourcePx[0].length || x < 0;
		if (OutOfBound || sourcePx[y][x] != null)
			return;

		sourcePx[y][x] = Pixel.DEFAULT_PIXEL; // No pixel allocation, just mark it for coloring.

		floodFill(x + 1, y, sourcePx);
		floodFill(x - 1, y, sourcePx);
		floodFill(x, y - 1, sourcePx);
		floodFill(x, y + 1, sourcePx);
	}

	// Apply a perlin noise shading on every pixels.
	private static void colorize(int x, int y, Pixel[][] dest)
	{
		Color col;
		boolean dropItem = Game.getRandom().nextInt(500) == 1;
		if (!dropItem)
		{
			final double coef = 1.5d;

			double nn = noiseGen.noise((double) x * coef, (double) y * coef);
			double n = GameUtils.map(nn, -1., 1., 0., 1.);

			int r = (int) (n * Pixel.DEFAULT_COLOR.getRed());
			int g = (int) (n * Pixel.DEFAULT_COLOR.getGreen());
			int b = (int) (n * Pixel.DEFAULT_COLOR.getBlue());

			col = new Color(r, g, b);
		} else
		{
			col = Color.yellow;
		}

		dest[y][x] = new Asteroidv2.Pixel(col, Pixel.DEFAULT_LIFE, dropItem);
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

	// Reduce the array to fit the asteroid.
	// Translate x, y from rect to keep the good position in screen.
	private static Pixel[][] shrinkAsteroid(Pixel[][] sourcePx, Rectangle2D.Float rect)
	{
		int minX = Integer.MAX_VALUE, maxX = 0;
		int minY = Integer.MAX_VALUE, maxY = 0;

		for (int line = 0; line < sourcePx.length; line++)
		{
			for (int col = 0; col < sourcePx[line].length; col++)
			{
				if (sourcePx[line][col] != null && !sourcePx[line][col].isDead())
				{
					maxX = Math.max(col, maxX);
					maxY = Math.max(line, maxY);

					minX = Math.min(col, minX);
					minY = Math.min(line, minY);
				}
			}
		}

		if (rect != null)
		{
			rect.x += minX * PIXEL_SIZE;
			rect.y += minY * PIXEL_SIZE;
		}

		// Fit table dimension to the shape dimension
		Pixel[][] destPx = new Pixel[maxY - minY + 1][maxX - minX + 1];
		for (int i = 0; i < destPx.length; i++)
			System.arraycopy(sourcePx[minY + i], minX, destPx[i], 0, destPx[0].length);

		return destPx;
	}

	public static boolean containPixel(Pixel[][] pixels, Pixel pixel)
	{
		for (int y = 0; y < pixels.length; y++)
			for (int x = 0; x < pixels[y].length; x++)
				if (pixels[y][x] == pixel)
					return true;
		return false;
	}

	// Each pixel found at x y is marked as null in sourcePx.
	private static void findSubAsteroidBis(int x, int y, Pixel[][] sourcePx, Pixel[][] pixelFound)
	{
		// Pixel detection, stop if array bounds are found or empty pixel or pixel
		if (y >= sourcePx.length || y < 0 || x >= sourcePx[0].length || x < 0 || sourcePx[y][x] == null || sourcePx[y][x].isDead())
			return;

		pixelFound[y][x] = sourcePx[y][x];
		sourcePx[y][x] = null; // Mark it as found

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

	// Return the position of the collision with the pixel
	public Rectangle2D.Float getPixelCollisionRect(Rectangle2D.Float rect)
	{
		Rectangle2D.Float point = null;
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
						return (Float) pxRect.createIntersection(rect);
				}
			}
		}
		return point;
	}

	// Return the position of the collision with the pixel
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
						collide = true;
				}
			}
		}
		return collide;
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

							PixelParticle particle = new PixelParticle(game, pixel.getInitColor(), (int) pxRect.getCenterX(), (int) pxRect.getCenterY(), -bullet.getVelX(), getVelY() - bullet.getVelY() * 0.7f, 30);

							particle.velX = (float) Math.cos(Game.getRandom().nextFloat() * Math.PI / 2) * bullet.velX;
							particle.velX += Math.cos(Game.getRandom().nextFloat() * Math.PI);

							game.spawnEntity(particle);

							if (pixel.isDropItem())
								ItemUtils.dropRandomItem(game, this.x + x1 * PIXEL_SIZE - PIXEL_SIZE / 2, this.y + y1 * PIXEL_SIZE - PIXEL_SIZE / 2);

							// Keep reference for the regeneate method
							// pixels[y1][x1] = null;

							isPixelDead = true;
						}
					}
				}
			}
		}

		if (isPixelDead) // At least one pixel is dead
		{
			if (isDead())
			{
				this.alive = false;
				Game.score++;
			} else
			{
				ArrayList<Pixel[][]> subList = findSubAsteroid(pixels);
				if (subList.size() > 1)
				{
					// Splitting
					for (Pixel[][] sub : subList)
					{
						Rectangle2D.Float rect = (Float) hitbox.clone();
						sub = shrinkAsteroid(sub, rect);

						if (sub.length == 1 && sub[0].length == 1) // This is a single pixel
						{
							PixelParticle particle = new PixelParticle(game, sub[0][0].getInitColor(), (int) rect.getCenterX(), (int) rect.getCenterY(), -bullet.getVelX(), getVelY() - bullet.getVelY() * 0.7f, 30);

							particle.velX = (float) Math.cos(Game.getRandom().nextFloat() * Math.PI / 2) * bullet.velX;
							particle.velX += Math.cos(Game.getRandom().nextFloat() * Math.PI);

							game.spawnEntity(particle);
						} else
						{
							Asteroidv2 a = new Asteroidv2(game, rect.x, rect.y, sub);

							float direction = 0;
							if (rect.getX() > bullet.hitbox.getCenterX())
								direction = 0.5f;
							else
								direction = -0.5f;

							// a.velX = (float) (Math.cos(Game.getRandom().nextFloat() * Math.PI / 2) *
							// direction);
							a.velX = this.velY * direction;
							a.velY = this.velY;

							game.spawnEntity(a);
						}

					}
					this.alive = false;
				}
			}
		}

	}

	int regenTick;

	@Override
	public void update(float deltaTime)
	{
		super.update(deltaTime);

		if (y >= game.getHeight())
		{
			alive = false;
			return;
		}

		// regenerate();
	}

	public void regenerate()
	{
		// REGENERATION TEST
		if (regenTick-- <= 0)
		{
			regenTick += 10;
			ArrayList<Pixel> pixelDamaged = new ArrayList<Pixel>();
			for (int y = 0; y < pixels.length; y++)
				for (int x = 0; x < pixels[y].length; x++)
				{
					Pixel pixel = pixels[y][x];
					if (pixel != null && pixel.getInitLife() > pixel.getLife())
					{
						pixelDamaged.add(pixel);
					}
				}

			if (!pixelDamaged.isEmpty())
			{
				int id = Game.getRandom().nextInt(pixelDamaged.size());
				pixelDamaged.get(id).addLife();
			}
		}
	}

	private boolean checkPixelCollision(int x, int y, Entity e, float radius)
	{
		if (pixels[y][x] != null && pixels[y][x].life > 0)
		{
			Rectangle2D.Float pxRect = new Rectangle2D.Float();

			pxRect.x = (int) this.x + PIXEL_SIZE * x;
			pxRect.y = (int) this.y + PIXEL_SIZE * y;
			pxRect.width = PIXEL_SIZE;
			pxRect.height = PIXEL_SIZE;

			Rectangle2D.Float rect = e.getHitbox();

			float dist = (float) Math.sqrt(Math.pow(rect.getCenterX() - pxRect.getCenterX(), 2) + Math.pow(rect.getCenterY() - pxRect.getCenterY(), 2));
			if (dist < radius)
			{
				return true;
			}
		}
		return false;
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
					Color pxCol = pixel.getColor();
					if (checkPixelCollision(x, y, game.getPlayer(), game.getPlayer().width * 0.75f))
					{
						if(game.getPlayer().layer == 0)
						{
							// Transparency effect
							int col = pixel.getColor().getRGB() & 0xffffff;
							col |= 0x45 << 24;
							pxCol = new Color(col, true);
							
						}else if(game.getPlayer().layer == 2)
						{
							// Shadow effect
							pxCol = new Color(pxCol.getRed() / 2, pxCol.getGreen() / 2, pxCol.getBlue() / 2);
						}
					}
					
					g.setColor(pxCol);
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

	// Immutable
	private static class Pixel
	{
		public static final Pixel DEFAULT_PIXEL = new Pixel(Pixel.TYPE_PIXEL);

		private boolean dropItem; // Drop item if destroyed

		public static final int DEFAULT_LIFE = 3;

		public static final int TYPE_PIXEL = 1;

		private static final Color DEFAULT_COLOR = new Color(176, 170, 157);

		private Color color, initColor;

		private int life, initLife;

		private int type;

		public Pixel(int type)
		{
			this.type = type;
			this.initColor = DEFAULT_COLOR;
			this.color = DEFAULT_COLOR;
			this.life = DEFAULT_LIFE;
			this.initLife = life;
		}

		public Pixel(Color col, int life, boolean dropItem)
		{
			this.type = TYPE_PIXEL;
			this.initColor = col;
			this.color = col;
			this.life = life;
			this.initLife = life;
			this.dropItem = dropItem;
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

		public void addLife()
		{
			this.life = Math.min(++life, initLife);
			this.color = new Color(life * initColor.getRed() / initLife, life * initColor.getGreen() / initLife, life * initColor.getBlue() / initLife);
		}

		public int getLife()
		{
			return life;
		}

		public int getInitLife()
		{
			return initLife;
		}

		public Color getInitColor()
		{
			return initColor;
		}

		public int getType()
		{
			return type;
		}
	}

//	private class FindSubAsteroidResult
//	{
//		private final int nbrPx;
//		private final ArrayList<Pixel[][]> subAsteroid;
//
//		public FindSubAsteroidResult(int nbrPx, ArrayList<Pixel[][]> subAsteroid)
//		{
//			this.nbrPx = nbrPx;
//			this.subAsteroid = subAsteroid;
//		}
//		
//		public int getNn
//	}

}
