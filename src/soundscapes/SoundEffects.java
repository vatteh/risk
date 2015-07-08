package soundscapes;

/**
 *  Plays all the sound effects in the game.
 * 
 * 
 * @author Duong Pham and Victor Atteh
 */
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import view.RiskFrame;

public class SoundEffects {

	private static ArrayList<String> attackSounds;
	private static ArrayList<String> takeoverSounds;
	private static ArrayList<String> marchingSounds;
	
	/**
	 * This is the constructor.
	 *
	 */
	public SoundEffects() {
		attackSounds = new ArrayList<String>();
		takeoverSounds = new ArrayList<String>();
		marchingSounds = new ArrayList<String>();
		
		attackSounds.add("edited-sfx/gunshot1.wav");
		attackSounds.add("edited-sfx/gunshot2.wav");
		attackSounds.add("edited-sfx/gunshot3.wav");
		
		takeoverSounds.add("edited-sfx/boom1.wav");
		takeoverSounds.add("edited-sfx/boom2.wav");
		takeoverSounds.add("edited-sfx/boom4.wav");
		
		marchingSounds.add("edited-sfx/marching.wav");
	}

	/**
	 * Play an attack sound 1 time.
	 *
	 */
	public static void playAttackSound() {
		Runnable thread = new Runnable() {
			public void run() {
				try {
					String filepath = chooseRandomFile(attackSounds);
					FileInputStream input = new FileInputStream(filepath);
					AudioStream stream = new AudioStream(input);
					AudioPlayer.player.start(stream);
					Thread.sleep(2000);
					stream.close();
					input.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		};
		RiskFrame.dispatchThread(thread);
	}
	
	
	/**
	 * Play an attack sound 1 time.
	 *
	 */
	public static void playTakeoverSound() {
		Runnable thread = new Runnable() {
			public void run() {
				try {
					String filepath = chooseRandomFile(takeoverSounds);
					FileInputStream input = new FileInputStream(filepath);
					AudioStream stream = new AudioStream(input);
					AudioPlayer.player.start(stream);
					Thread.sleep(2000);
					stream.close();
					input.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		};
		RiskFrame.dispatchThread(thread);
	}
	
	/**
	 * Play a marching sound 1 time.  Used for reinforcement.
	 *
	 */
	public static void playMarchingSound() {
		Runnable thread = new Runnable() {
			public void run() {
				try {
					String filepath = chooseRandomFile(marchingSounds);
					FileInputStream input = new FileInputStream(filepath);
					AudioStream stream = new AudioStream(input);
					AudioPlayer.player.start(stream);
					Thread.sleep(2000);
					stream.close();
					input.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		};
		RiskFrame.dispatchThread(thread);
	}
	
	/**
	 * Chooses a random sound file to play.
	 * 
	 * @param soundList
	 * 			the list of available sound files.
	 * @return the filepath.
	 */
	private static String chooseRandomFile(ArrayList<String> soundList) {
		Collections.shuffle(soundList);
		return soundList.get(0);
	}
	
	
}
