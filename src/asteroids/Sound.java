package asteroids;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;

public enum Sound implements LineListener {

	EXPLOSION("explosion.wav"),
	GUNSHOT("gunshot.wav"),
	LASER("laser.wav"),
	RELOAD("reload.wav"),
	HEAL("heal.wav");
	
	public static final long SOUND_DELAY = 100;

	public final static String path = "rsc/audio/";

	private String file;

	private long lastTimePlayed;

	private Sound(String file) {
		this.file = file;
//		JOptionPane.showMessageDialog(null, "Fichier " + file + " manquant.", "Asteroids game error",
//				JOptionPane.ERROR_MESSAGE);
//		System.exit(-1);
	}

	// Charge un fichier audio, lorsque le clip est joué il sera détruit automatiquement quand il sera fini.
	public void play() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastTimePlayed > SOUND_DELAY) {
			Clip clip = createSound(path + file);
			if (clip != null)
			{
				clip.addLineListener(this);
				clip.start();
			}
			lastTimePlayed = currentTime;
		}
	}


	public static Clip createSound(String path) {
		AudioInputStream audioStream = null;
		Clip clip = null;
		try {
			audioStream = AudioSystem.getAudioInputStream(new File(path));
			clip = AudioSystem.getClip();
			clip.open(audioStream);
			clip.setFramePosition(0);
//			System.out.println("Sound created");
		} catch (Exception e) 
		{
			e.printStackTrace();
			if (audioStream != null) {
				try {
					audioStream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return clip;
	}

	@Override
	public void update(LineEvent event) {
		if (event.getType() == Type.STOP) {
			if (event.getSource() instanceof Clip) {
				((Clip) event.getSource()).close();
//				System.out.println("Sound deleted");
			}
		}
	}

}
