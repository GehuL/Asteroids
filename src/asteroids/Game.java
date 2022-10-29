package asteroids;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;

import entity.Entity;
import entity.AsteroidSpawner;
import entity.Asteroidv2;
import entity.Earth;
import entity.Player;

public class Game extends JFrame implements ComponentListener
{
	private static final Random random;

	private static final long serialVersionUID = 1L;

	private static final String FONT_PATH = "rsc/";

	private static final String FONT_NAME = "m6x11ed";

	private static final String FONT_EXTENSION = ".ttf";

	static final String[] pauseText = { "GAME PAUSED", "[PRESS KEY P]", };

	static final String[] gameoverText = { "GAME OVER", "[PRESS ENTER]" };

	public static final int FPS_60 = 60;

	public static final int FPS_120 = 120;

	public static final boolean DEBUG = !true;

	private boolean run;

	private boolean pause = false;

	private boolean isFullScreen;

	private GameCanvas canvas;

	private Player player;

	private Earth earth;

	private BufferedImage buffer;

	private Graphics2D bufferGraphic, canvasGraphic;

	private int width;

	private int height;

	private ArrayList<Entity> entities = new ArrayList<>();

	private ArrayList<Entity> entitiesIncoming = new ArrayList<>();

	public KeyboardInput input = new KeyboardInput();

	public static int score = 0;

	private long target_time;

	private static final Color game_pink = new Color(250, 60, 30);

	private Clip gameMusic;

	private static final short seed;

	static
	{
		// Generate seed to be print for the user
		seed = (short) (new Random().nextInt(Short.MIN_VALUE, Short.MAX_VALUE) & 0xffff); // Using short to get a shorter
																							// hex string
		// seed = Integer.parseInt("40c9", 16);
		random = new Random(seed);
		System.out.println("Game seed:" + Integer.toHexString(seed & 0xffff));
	}

	public Game(int width, int height, int fps)
	{

		this.width = width;
		this.height = height;

		this.target_time = fps;

		setTitle("Asteroids");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		canvas = new GameCanvas(new Dimension(width, height));
		canvas.addComponentListener(this);
		add(canvas);

		pack();

		setLocationRelativeTo(null);

		setVisible(true);

		requestFocusInWindow();

		try
		{
			setIconImage(ImageIO.read(new File("rsc/images/asteroid.png")));
		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		earth = new Earth(this);
		player = new Player(this, earth, 50, 170, 50, 50);

		addKeyListener(input);

		buffer = new BufferedImage(width, height, 1);
		bufferGraphic = (Graphics2D) buffer.getGraphics();

		try
		{
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(FONT_PATH + FONT_NAME + FONT_EXTENSION)));
			bufferGraphic.setFont(new Font(FONT_NAME, Font.PLAIN, 18));
		} catch (FontFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		canvas.setBuffer(buffer);
		canvasGraphic = (Graphics2D) getGraphics();

		gameMusic = Sound.createSound(Sound.path + "game_music.wav");
		gameMusic.loop(Clip.LOOP_CONTINUOUSLY);
		gameMusic.start();
	}

	public void applyRendering(Graphics2D g)
	{
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
	}

	public static Random getRandom()
	{
		return Game.random;
	}

	public int width()
	{
		return width;
	}

	public int height()
	{
		return height;
	}

	public Player getPlayer()
	{
		return player;
	}

	long updateDelta = System.currentTimeMillis();

	public void start()
	{
		if (run)
			return;

		run = true;

		loadRessources();

		entities.add(new AsteroidSpawner(this));

		// GAME LOOP
		new Thread(() ->
		{
//			long wait = 16;
//			while (run)
//			{
//
//				long start = System.nanoTime();
//
//				input.poll();
//				updateAll(wait / 1000f); // (float) deltaTime / 1000000000.f);
//				drawAll();
//
//				deltaTime = System.nanoTime() - start;
//				wait = target_time - deltaTime / 1000000;
//				if (wait < 0)
//					wait = target_time;
//
//				try
//				{
//					Thread.sleep(wait);
//				} catch (Exception e)
//				{
//					e.printStackTrace();
//				}
//				fps = 1000000000 / (System.nanoTime() - start);
//			}

			long lastime = System.nanoTime();
			double ns = 1000000000 / target_time;
			double delta = 0;
			int frames = 0;
			double time = System.currentTimeMillis();

			while (run)
			{
				long now = System.nanoTime();
				delta += (now - lastime) / ns;
				lastime = now;

				if (delta >= 1)
				{
					input.poll();
					updateAll((float) delta);
					drawAll();
					delta = 0;
					frames++;
					if (System.currentTimeMillis() - time >= 1000)
					{
						time += 1000;
						fps = frames; // To be rendered in drawAll()
						frames = 0;
					}

				}
			}

		}, "gameloop-thread").start();
	}

	int fps;

	private void loadRessources()
	{
		System.out.println("Loading ressouces...");
		// The JVM will call every constructor once from the enum
		Sound.values();
		Textures.values();
	}

	public void stop()
	{
		run = false;
	}

	// Entities will spawn in next frame
	public void spawnEntity(Entity entity)
	{
		entitiesIncoming.add(entity);
		// System.out.println(entity.getClass() + " will spawn");
	}

	public ArrayList<Entity> getEntities()
	{
		return entities;
	}

	public <T extends Entity> ArrayList<T> getEntitiesOf(Class<T> e)
	{
		ArrayList<T> a = new ArrayList<T>();
		for (Entity en : getEntities())
		{
			if (en.getClass() == e)
				a.add((T) en);
		}
		return a;
	}

	// intersection avec au moins une autre entit�
	public boolean isIntersecting(Entity entity)
	{
		for (Entity en : entities)
		{
			if (en != entity && entity.hitbox.intersects(en.hitbox))
				return true;
		}
		return false;
	}

	// intersection avec au moins une autre entité
	public boolean isIntersecting(Entity entity, Rectangle2D.Float rect)
	{
		for (Entity en : entities)
		{
			if (en != entity && en.isCollisionEnable() && en.hitbox.intersects(rect))
				return true;
		}
		return false;
	}

	public void centerWindow()
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screenSize.width / 2 - getWidth() / 2, screenSize.height / 2 - getHeight() / 2);
	}

	private void updateAll(float deltaTime)
	{
		if (input.keyDownOnce(KeyEvent.VK_UP))
		{
			Entity.GAME_SPEED += 0.1;
			player.setLayer(player.layer + 1);
		}

		if (input.keyDownOnce(KeyEvent.VK_DOWN))
		{
			Entity.GAME_SPEED -= 0.1;
			player.setLayer(player.layer - 1);
		}

		if (input.keyDown(KeyEvent.VK_ESCAPE))
			System.exit(0);
		else if (input.keyDownOnce(KeyEvent.VK_F12))
		{

			dispose();
			if (isFullScreen)
			{
				setUndecorated(false);
				pack();
				setLocationRelativeTo(null);
			} else
			{
				setExtendedState(JFrame.MAXIMIZED_BOTH);
				setUndecorated(true);
			}
			setVisible(true);

			requestFocus();
			isFullScreen = !isFullScreen;
		}

		if (!isGameOver())
		{ // IN GAME

			if (input.keyDownOnce(KeyEvent.VK_P))
				pause = !pause;

			if (!pause)
			{
				for (int i = 0; i < entities.size(); i++)
				{
					Entity en = entities.get(i);
					en.update(deltaTime);
					if (!en.alive)
					{
						if (entities.remove(en))
						{
//							System.out.println(en.getClass() + " killed");
							i--;
						}
					}
				}

				player.update(deltaTime);
				earth.update(deltaTime);

//				if (entitiesIncoming.size() > 0)
//					System.out.println("Entites added: " + entitiesIncoming.size());

				for (int i = 0; i < entitiesIncoming.size(); i++)
				{
					entities.add(entitiesIncoming.get(i));
					entitiesIncoming.remove(i);
					i--;
				}
			}
		}

		// if (isGameOver())
		if (input.keyDownOnce(KeyEvent.VK_ENTER))
		{
			restart();

		}
	}

	public void restart()
	{

		System.gc();

		player.reset();
		earth.reset();
		entities.clear();

		pause = false;

		if (score > Save.save.getBestScore())
		{
			Save.save.setBestScore(score);
			Save.save.setName(System.getProperty("user.name"));
		}

		spawnEntity(new AsteroidSpawner(this));
		score = 0;
	}

	public boolean isGameOver()
	{
		return !player.alive || earth.isDead();
	}

	private synchronized void drawAll()
	{

		bufferGraphic.setColor(Color.BLACK);
		bufferGraphic.drawImage(Textures.BACKGROUND.getImage(), 0, 0, width, height, null);

		////////////////////////// ENTITIES ////////////////////////////

		earth.draw(bufferGraphic);

		if (player.layer == 0)
			player.draw(bufferGraphic);

		for (int i = 0; i < entities.size(); i++)
		{
			entities.get(i).draw(bufferGraphic);
		}

		if (player.layer > 0)
			player.draw(bufferGraphic);

		////////////////////////// TEXT ////////////////////////////
		bufferGraphic.setColor(game_pink);

		if (pause)
		{
			bufferGraphic.setColor(new Color(50, 50, 255, 100));
			bufferGraphic.fillRect(width / 2 - 200, height / 2 - 100, 400, 200);
			bufferGraphic.setFont(bufferGraphic.getFont().deriveFont(50.f));
			bufferGraphic.setColor(game_pink);
			drawCenteredString(pauseText);
			bufferGraphic.setFont(bufferGraphic.getFont().deriveFont(18.f));
		}

		bufferGraphic.setColor(game_pink);
		if (isGameOver())
		{
			bufferGraphic.setFont(bufferGraphic.getFont().deriveFont(50.f));
			drawCenteredString(gameoverText);
			bufferGraphic.setFont(bufferGraphic.getFont().deriveFont(18.f));
		}

		bufferGraphic.setColor(Color.RED);
		bufferGraphic.drawString("Seed:" + Integer.toHexString(seed & 0xffff), 0, 20);
		bufferGraphic.drawString("Score:" + score, 0, 50);
		bufferGraphic.drawString("Entity:" + entities.size(), 0, 70);

		// int fps = (int) (1000 / deltaTime);
		bufferGraphic.drawString("FPS:" + fps, 0, 100);

		// bufferGraphic.drawString("Cr�� par Lauric Gehu", width - 150, 20);
		bufferGraphic.drawString("Record précédent:" + Save.save.getBestScore() + " par " + Save.save.getName(), 0, 35);

		canvasGraphic.drawImage(buffer, 0, 0, canvas.getWidth(), canvas.getHeight(), null);
	}

	public void drawCenteredString(String[] str)
	{
		for (int i = 0; i < str.length; i++)
		{
			Rectangle2D bnd = bufferGraphic.getFontMetrics().getStringBounds(str[i], bufferGraphic);

			int y = (int) (height / 2.f - (bnd.getHeight() * (str.length / 2 - i - 1)));
			bufferGraphic.drawString(str[i], width / 2 - (int) bnd.getWidth() / 2, y);
		}
	}

	public void drawString(String str, int x, int y)
	{
		for (String line : str.split("\n"))
			bufferGraphic.drawString(line, x, y += bufferGraphic.getFontMetrics().getHeight());
	}

	public void drawImage(Image img, float x, float y, int width, int height)
	{
		bufferGraphic.drawImage(img, Math.round(x), Math.round(y), width, height, null);
	}

	@Override
	public synchronized void componentResized(ComponentEvent e)
	{
		canvasGraphic = (Graphics2D) getGraphics();
//		applyRendering(canvasGraphic);
	}

	@Override
	public void componentMoved(ComponentEvent e)
	{ // TODO Auto-generated method stub
	}

	@Override
	public void componentShown(ComponentEvent e)
	{ // TODO Auto-generated method stub
	}

	@Override
	public void componentHidden(ComponentEvent e)
	{ // TODO Auto-generated method stub
	}

}
