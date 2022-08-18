package asteroids;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Sprite {

	private BufferedImage image;

	public final int imgCount;

	public int index = -1;

	private int subWidth; // Sub image width

	boolean play;

	private long time;

	private long dt;

	public Sprite(BufferedImage img, int subimg) {
		this.image = img;
		this.imgCount = subimg;
		this.subWidth = image.getWidth() / imgCount;
		setFPS(30);
	}

	public void setFPS(int FPS) {
		this.dt = 1000 / FPS;
	}

	public void update() {
		if (System.currentTimeMillis() - time > dt) {
			index = index + 1 % imgCount;
			time = System.currentTimeMillis();
		}
	}

	public void draw(Graphics2D g, float fx, float fy, int width, int height) {
		final int subX = index * this.subWidth;

		int x = Math.round(fx);
		int y = Math.round(fy);

		g.drawImage(image, Math.round(x), Math.round(y), x + width, y + height, subX, 0, subX + this.subWidth,
				image.getHeight(), null);
	}

}
