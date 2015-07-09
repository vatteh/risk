package view;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * 
 * The Animation interface defines an animation as a sequence of Images.
 * 
 * @author Phil
 * 
 */
public interface Animation {

	/**
	 * When getting an animation sequence, an Animation retrieves a list of
	 * off-screen BufferedImages representing a complete animation, and returns
	 * that list.
	 * 
	 * @param i
	 *            - an integer used to specify an animation subtype
	 * 
	 * @return - an array of lists of bufferedImages representing the animation. 
	 * Each index of the array contains a piece of the animation. Each piece
	 * of the animation is a list of frames that make up the animation.
	 * 
	 * POSTCONDITION: each element of the array should have the same list size()
	 * 
	 */
	abstract List<BufferedImage>[] getAnimationSequence(int i);

}
