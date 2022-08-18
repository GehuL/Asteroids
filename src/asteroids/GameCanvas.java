package asteroids;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.JPanel;

public class GameCanvas extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Image buffer;

	public GameCanvas(Dimension size) {
		setPreferredSize(size);
	}

	/*public void paint(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.BLACK);
		g.clearRect(0, 0, getWidth(), getHeight());
		g.drawImage(buffer, 0, 0, getWidth(), getHeight(), null);
	}*/

	public void setBuffer(Image image) {
		buffer = image;
	}

}
