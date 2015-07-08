package view;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * A factory that makes buffered images
 * 
 * @author Phil
 * 
 */
public class BufferedImageFactory {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a buffered image.
	 * 
	 * @param im
	 *            - the image to make a BufferedImage with
	 * @param comp
	 *            - the component that tracks the images
	 * @return - a new BufferedImage
	 */
	public static BufferedImage createBufferedImage(Image im, Component comp) {
		MediaTracker mt = new MediaTracker(comp);
		mt.addImage(im, 0);

		try {
			mt.waitForID(0);
		} catch (InterruptedException ie) {
		}
		BufferedImage bufferedImageOut = new BufferedImage(im.getWidth(null),
				im.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics g = bufferedImageOut.getGraphics();
		g.drawImage(im, 0, 0, null);

		return bufferedImageOut;
	}

	/**
	 * Clones a buffered image.
	 * 
	 * @param i
	 * 			the buffer image to copy.
	 * @return  a copy of the image.
	 */
	public static BufferedImage cloneImage(BufferedImage i) {
		BufferedImage newIm = new BufferedImage(i.getWidth(), i.getHeight(), i
				.getType());
		java.awt.Graphics2D g = newIm.createGraphics();
		g.drawImage(i, 0, 0, null);
		g.dispose();
		return newIm;
	}

}
