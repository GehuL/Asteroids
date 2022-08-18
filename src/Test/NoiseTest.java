package Test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import asteroids.GameUtils;
import asteroids.NoiseGenerator;

import java.io.*;

public class NoiseTest
{
	private static final int WIDTH = 512;
	private static final int HEIGHT = 512;

	public static void main(String[] args)
			throws IOException
	{

		NoiseGenerator noise = new NoiseGenerator();
		BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setColor(Color.YELLOW);
		g.fillRect(0, 0, WIDTH, HEIGHT);

		double radius = 8;
		int resolution = 50;
		double noiseMax = 10.;

		for (float angle = 0; angle <= Math.PI * 2; angle += Math.PI * 2 / resolution)
		{
			double nx = GameUtils.map(Math.cos(angle), -1., 1., 0., noiseMax);
			double ny = GameUtils.map(Math.sin(angle), -1., 1., 0., noiseMax);

			double nVar = GameUtils.map(noise.noise(nx, ny), -1., 1., 4., radius);

			int x = (int) (Math.cos(angle) * nVar) + WIDTH / 2;
			int y = (int) (Math.sin(angle) * nVar) + HEIGHT / 2;

			image.setRGB(x, y, Color.black.getRGB());
		}
		ImageIO.write(image, "png", new File("noise.png"));
	}
}