package soundscapes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;

import sun.audio.AudioData;
import sun.audio.AudioDataStream;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import view.RiskFrame;

public class MusicPlayer implements Serializable{

	private static boolean musicDisabled;
	private static Set<InputStream> streamingColl = new HashSet();

	public static void playAudio(final String filepath) {

		Runnable thread = new Runnable() {
			public void run() {
				try {
					FileInputStream input = new FileInputStream(filepath);
					AudioStream stream = new AudioStream(input);
					streamingColl.add(stream);
					AudioPlayer.player.start(stream);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		};
		RiskFrame.dispatchThread(thread);
	}

	public static void disableSounds() {
		musicDisabled = true;
		for (InputStream stream : streamingColl) {
			AudioPlayer.player.stop(stream);
		}
	}

	public static void enableSounds() {
		musicDisabled = false;
		for (InputStream stream : streamingColl) {
			AudioPlayer.player.start(stream);
		}
	}

	public static void main(String[] args) {

		playAudio("Dancing_Queen.mid");

		javax.swing.JFrame jf = new javax.swing.JFrame();
		JButton b = new JButton();
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (musicDisabled) {
					enableSounds();
				} else {
					disableSounds();
				}
			}

		});
		jf.add(b);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.pack();
		jf.setVisible(true);
	}

	private static void pause(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
