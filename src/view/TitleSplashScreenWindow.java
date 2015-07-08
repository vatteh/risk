package view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;

/**
 * The title splash screen.  This is the splash shown at the very beginning.  It can be
 * bypassed by clicking on the window.
 * 
 * @author Duong Pham
 *
 */
public class TitleSplashScreenWindow extends JWindow {
	
	/**
	 * This is the constructor.
	 *
	 */
	public TitleSplashScreenWindow() {
		
		Container sPane = this.getContentPane();
		sPane.setLayout(new BorderLayout());
		
		ImageIcon titleIcon = new ImageIcon("splash/title-splash.gif");
		JLabel titleIconLabel = new JLabel(titleIcon);
		
		sPane.add(titleIconLabel, BorderLayout.CENTER);
		this.setBounds(0,0,700,461);
		
        Dimension dim = this.getToolkit().getScreenSize();
		this.setLocation(dim.width/2 - this.getSize().width/2, dim.height/2 - this.getSize().height/2);
								
		this.setFocusable(true);
		this.requestFocus();
		
		this.addMouseListener(new CMouseListener());
	}
	
	/**
	 * Shows the splash screen for the specified amount of time.
	 * 
	 * @param seconds
	 * 			seconds to show.
	 */
	public void showSplashScreen(int seconds) {
		this.setVisible(true);
	}
	/**
	 * Listens for when the user clicks on the splash screen.
	 * 
	 * @author dthpham
	 *
	 */
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
