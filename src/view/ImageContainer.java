package view;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

/**
 * Used as an alternative Container to JButtons to store and display an Image
 * 
 * @author phlee
 */
public class ImageContainer extends JPanel {

	BufferedImage canvasImage;
	BufferedImage offscreenImage;
	private static final int gap = 10;
	private static final Color bgColor = GameMapPanel.OCEAN_COLOR;

	/**
	 * Constuctor for the ImageContainer.
	 * 
	 * @param theImage - the offscreen image
	 */
	public ImageContainer(BufferedImage theImage) {
		canvasImage = new BufferedImage(100,100,BufferedImage.TYPE_INT_ARGB);
		setBorder(new LineBorder(Color.DARK_GRAY, 4));
		setImage(theImage);
	}

	/**
	 * Sets the Image and repaints immediately.
	 * If the specified image is null then this will
	 * display a filled background only.
	 * @param theImage
	 * 			the image to set.
	 */
	public void setImage(BufferedImage theImage) {
		this.offscreenImage = theImage;
		if (getGraphics() != null) {
			paintComponent(getGraphics());
		}
	}

	public void paintComponent(Graphics g) {

		if(canvasImage.getWidth()!=this.getWidth() || canvasImage.getHeight()!=this.getHeight()){
			canvasImage = new BufferedImage(this.getWidth(),this.getHeight(),BufferedImage.TYPE_INT_RGB);
		}
		
		Graphics2D g2 = canvasImage.createGraphics();
		g2.setColor(bgColor);	
		g2.fillRect(0, 0, getWidth(), getHeight());
		
		
		if (offscreenImage != null) {
			g2.drawImage(offscreenImage, gap, gap, canvasImage.getWidth()-2*gap, canvasImage.getHeight()-2*gap, this);
		}
		g2.dispose();
		g.drawImage(canvasImage,0,0,getWidth(),getHeight(),this);
		g.dispose();
	}

}
