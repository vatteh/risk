package view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;

/**
 * The BSOD splash screen used in the middle of the game at random intervals.
 * Throws up the blue screen to ruin your day.
 * 
 * @author Duong Pham
 *
 */
public class SplashScreenWindow extends JWindow {
	
	/**
	 * Constructor.
	 *
	 */
	public SplashScreenWindow() {
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		double screenMaxW = ge.getMaximumWindowBounds().getWidth();
		double screenMaxH = ge.getMaximumWindowBounds().getHeight();
		
		Container sPane = this.getContentPane();
		sPane.setLayout(new BorderLayout());
		
		ImageIcon bsodIcon = new ImageIcon("splash/vista_bsod.png");
		
		int wBound = (int)screenMaxW;
		int hBound = (int)screenMaxH;
		Image scaledImage = bsodIcon.getImage().getScaledInstance((int)screenMaxW, (int)screenMaxH, Image.SCALE_DEFAULT);
		ImageIcon scaledBSODIcon = new ImageIcon(scaledImage);
		JLabel scaledBSODIconLabel = new JLabel(scaledBSODIcon);
		
		sPane.add(scaledBSODIconLabel, BorderLayout.CENTER);
		this.setBounds(0,0,wBound,hBound);
		
		Dimension dim = this.getToolkit().getScreenSize();
		this.setLocation(dim.width/2 - this.getSize().width/2, dim.height/2 - this.getSize().height/2);
								
		this.setFocusable(true);
		this.requestFocus();
		this.addMouseListener(new CMouseListener());
		
		showSplashScreen(10);
	}
	
	/**
	 * Shows the window.
	 * 
	 * @param seconds
	 * 			the time in seconds you want it to appear for.
	 */
	public void showSplashScreen(int seconds) {
		this.setVisible(true);
		try {
			java.awt.Toolkit.getDefaultToolkit().beep();
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.setVisible(false);
		this.dispose();	
	}
	
	private class CMouseListener implements MouseListener {

		public void mouseClicked(MouseEvent arg0) {
			
		}

		public void mouseEntered(MouseEvent arg0) {
			
		}

		public void mouseExited(MouseEvent arg0) {
			
		}

		public void mousePressed(MouseEvent arg0) {
			setVisible(false);
			dispose();	
		}

		public void mouseReleased(MouseEvent arg0) {
			
		}

	}

}
