package view;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * The thread that paints the map panel.
 * 
 * @author Phil
 *
 */
public class MapPanelAnimator extends AnimationThread {

	/**
	 * Constructor for the MapPanelAnimator.
	 * 
	 * @param canvas
	 * 			the animation canvas.
	 * @param animation
	 * 			the animation.
	 * @param millisDelay
	 * 			the delays between animations.
	 */
	public MapPanelAnimator(AnimationCanvas canvas, Animation animation,
			long millisDelay) {
		super(canvas, animation, millisDelay);
	}

	@Override
	public void run() {

			List<BufferedImage>[] next = animation.getAnimationSequence(0);

			BufferedImage clone = BufferedImageFactory
					.cloneImage(((GameMapPanel) canvas).cachedAnimationCanvas);

			List<BufferedImage> singleton = next[0];
			List<BufferedImage> nextList = new java.util.ArrayList(1);
			BufferedImage nextIm = null;
			for (int i = 0; i < singleton.size(); i++) {
				nextIm = singleton.get(i);
				nextList.add(nextIm);
				canvas.updateFrame(nextList);
				nextList.clear();
				sleep();
			}
			singleton.clear();
			((GameMapPanel) canvas).endAnimation(clone);
	}

	private void sleep() {
		try {
			Thread.sleep(millisDelay);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
