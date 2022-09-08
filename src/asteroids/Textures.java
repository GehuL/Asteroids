package asteroids;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public enum Textures
{
	VAISSEAU("vaisseau.png"),
	ASTEROID("asteroid.png"),
	BACKGROUND(background()),
	EXPLOSION("explosion.png"),
	EARTH("earth.png"),
	EARTH_LIFE("earthLife.png"),
	UPGRADE("bonus.png"),
	BOMBE_ITEM(bombeBullet()),
	LASER_ITEM("laser_item.png"),
	BULLET("bullet.png");

	private final String path = "rsc/images/";

	private BufferedImage image;

	private Textures(String file)
	{
		try
		{
			image = ImageIO.read(new File(path + file));
			System.out.println(path + file + " chargé");
		} catch (IOException e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Fichier " + file + " manquant.", "Asteroids game error",
					JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
	}

	private Textures(BufferedImage image)
	{
		this.image = image;
	}

	public BufferedImage getImage()
	{
		return image;
	}

	private static BufferedImage background()
	{
		BufferedImage bg = new BufferedImage(800, 600, BufferedImage.TYPE_3BYTE_BGR);
		bg.setAccelerationPriority(1);
		Graphics2D g = (Graphics2D) bg.getGraphics();
		g.setColor(new Color(255, 255, 100));
		for (int i = 0; i < 200; i++)
		{
			int width = Game.getRandom().nextInt(1) + 2;
			g.fillOval(Game.getRandom().nextInt(800), Game.getRandom().nextInt(600), width - 1, width);
		}
		return bg;
	}

	private static BufferedImage bombeBullet()
	{
		BufferedImage b = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) b.getGraphics();
		for (int i = 0; i < 10; i++)
		{
			g.setColor(Color.getHSBColor(i * 0.1f, 1, 1));
			g.fillOval(0 + i * 10, 0 + i * 10, 50 - i * 10, 50 - i * 10);
		}
		return b;
	}

	private static BufferedImage laserItem()
	{
		BufferedImage b = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) b.getGraphics();
		g.setColor(Color.PINK);
		g.fillRoundRect(9, 9, 40, 40, 30, 30);
		return b;
	}
}
