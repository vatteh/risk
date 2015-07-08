package view;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * The diceroll animation thread.
 * 
 * @author Phil
 *
 */
public class DiceRollAnimator extends AnimationThread {

	private static final int ANIMATION_LOOPS = 10;

	private static final int MILLIS_TO_CLEAR = 5000;
	
	/**
	 * 
	 * @param diceTray
	 *            - The Dice Tray, an AnimationCanvas
	 * @param animation
	 *            - The DiceRollAnimation
	 * @param millisDelay
	 *            - The delay in milliseconds
	 */
	public DiceRollAnimator(DiceTray diceTray, DiceRollAnimation animation,
			long millisDelay) {
		super(diceTray, animation, millisDelay);
	}

	public void run() {

		
		List<BufferedImage>[] animSeq = null;
		if (((DiceTray) canvas).getDiceTrayType() == DiceTray.ATTACKER_DICE_TRAY) {
			animSeq = animation
					.getAnimationSequence(DiceRollAnimation.ATTACKER_DICE_ROLL_ANIMATION);
		} else {
			animSeq = animation
					.getAnimationSequence(DiceRollAnimation.DEFENDER_DICE_ROLL_ANIMATION);
		}

		if (animSeq.length == 0) {
			return;
		}

		for (int k = 0; k < ANIMATION_LOOPS; k++) {

			for (int i = 0; i < animSeq[0].size(); i++) {

				List<BufferedImage> nextSetOfFrames = new ArrayList(
						animSeq.length);

				for (int j = 0; j < animSeq.length; j++) {
					nextSetOfFrames.add(animSeq[j].get(i));
				}

				canvas.updateFrame(nextSetOfFrames);

				try {
					Thread.sleep(millisDelay);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		
		waitAndClearDiceTray();
		
		
		
	}
	
	private void waitAndClearDiceTray(){
		try {
			Thread.sleep(MILLIS_TO_CLEAR);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		((DiceTray)canvas).clearDiceTray();
		
	}

}
