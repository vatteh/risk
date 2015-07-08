package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import map.Map;

import player.Human;
import player.Player;

/**
 * Creates a dialog that shows a gif animation for two seconds.
 * 
 * @author Ross, Victor
 * 
 */
public class GifDialog extends JDialog {

	/**
	 * Creates a new dialog box that shows the given gif animation for 2 seconds
	 * 
	 * @param The
	 *            location of the image
	 */
	public GifDialog(String fileName) {
		if (fileName.equals("animations/gifs/youwin.gif")) {
			this.setTitle("Winner!");
		} else if (fileName.equals("animations/gifs/youlose.gif")) {
			this.setTitle("You lose");
		} else if (fileName.equals("animations/gifs/cards.gif")) {
			this.setTitle("Trading in cards");
		}

		JLabel pic = new JLabel(new ImageIcon(fileName));
		getContentPane().add(pic, BorderLayout.CENTER);
		// Position in center of screen
		this.setLocationRelativeTo(null);
		this.setSize(360, 200);
		this.setLocation((int) (this.getX() - (this.getSize().getWidth() / 2)),
				(int) (this.getY() - (this.getSize().getHeight() / 2)));

		pack();
		setVisible(true);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.dispose();
	}
	
	public GifDialog(Player player) {
		this.setTitle(player.getName() + " defeated");
		
		Image theImage = ImageReader.readImage("animations/gifs/defeated.gif");
		
		Graphics g = theImage.getGraphics();
		
		g.setColor(player.getColor());
		g.setFont(new Font("Arial",Font.BOLD,20));
		g.drawString(player.getName(),150,100);
		
		
		
		g.dispose();
		
		JLabel pic = new JLabel(new ImageIcon(theImage));
		//JLabel playerLabel = new JLabel(player.getName());
		this.add(pic);
		//this.add(playerLabel);
		//playerLabel.setLocation(150, 100);
		

		getContentPane().add(pic, BorderLayout.CENTER);
		// Position in center of screen
		this.setLocationRelativeTo(null);
		this.setSize(360, 200);
		this.setLocation((int) (this.getX() - (this.getSize().getWidth() / 2)),
				(int) (this.getY() - (this.getSize().getHeight() / 2)));

		pack();
		setVisible(true);

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.dispose();
	}

}
