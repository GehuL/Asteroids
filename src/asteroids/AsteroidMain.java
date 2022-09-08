package asteroids;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AsteroidMain
{

	private static final String LOG_FILE = "asteroids.log";

	public static void main(String[] args)
	{
//		if (!Game.DEBUG)
//		{
//			try
//			{
//				PrintStream logStream = new PrintStream(
//						new FileOutputStream(LOG_FILE, true));
//				System.setErr(logStream);
//
//				SimpleDateFormat date = new SimpleDateFormat();
//				
//				System.err.println(String.format("[%s]", date.format(new Date())));
//				System.out.println("Error redirected to " + LOG_FILE);
//
//			} catch (FileNotFoundException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}

		Save.load();

		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
				Game game = new Game(dim.width / 2, dim.height / 2, Game.FPS_60);
				game.start();
			}
		});

		Runtime.getRuntime().addShutdownHook(new Thread(() ->
		{
			Save.save();
			System.out.println("Exit normally");
		}));
	}

}
