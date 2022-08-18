package asteroids;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Save implements Serializable
{
	public static final String FILE_NAME = "asteroids.save";

	private static final long serialVersionUID = 1L;

	private int bestScore; // Meilleur score

	private String name; // Personne ayant fait le record
	// Singleton
	public static Save save = new Save(System.getProperty("user.name"), 0);;

	private Save(String name, int best)
	{
		this.bestScore = best;
		this.name = name;
	}

	public static void load()
	{
		ObjectInputStream deserial = null;
		try
		{
			FileInputStream input = new FileInputStream(FILE_NAME);
			deserial = new ObjectInputStream(input);
			save = (Save) deserial.readObject();
			System.out.println("Fichier sauvegarde chargé.");
		} catch (ClassNotFoundException | IOException e)
		{
			System.err.println("Impossible de charger la sauvegarde:" + e.getMessage());
		} finally
		{
			try
			{
				if (deserial != null)
					deserial.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void save()
	{
		ObjectOutputStream ser = null;
		try
		{
			FileOutputStream output = new FileOutputStream(FILE_NAME);
			ser = new ObjectOutputStream(output);
			ser.writeObject(save);
			System.out.println("Fichier de sauvegarde enregistré.");
		} catch (FileNotFoundException e)
		{
			System.err.println("Impossible de sauvegarder:" + e.getMessage());
		} catch (IOException e)
		{
			System.err.println("Impossible de sauvegarder:" + e.getMessage());
		} finally
		{
			try
			{
				if (ser != null)
					ser.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public int getBestScore()
	{
		return bestScore;
	}

	public void setBestScore(int bestScore)
	{
		this.bestScore = bestScore;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
