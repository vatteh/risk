package view;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * 
 * @author phlee
 * 
 *         An AnimationCanvas should be a graphical component that changes its
 *         view quickly and accurately when updateFrame() replaces the image
 *         that the canvas shows.
 * 
 */
public interface AnimationCanvas{

	/**
	 * Updates the current frame with the specified image and redraws the frame.
	 * 
	 * @param im
	 *            - the image (or a set of images) to replace the frame with.
	 */
	void updateFrame(List<BufferedImage> im);

}
