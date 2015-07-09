package view;

/**
 * 
 * An AnimationThread is an object that takes a canvas to draw on, an animation,
 * and a delay, and causes the animation to perform when it starts. Since this
 * is a subclass of thread, it should run concurrently with the game, without
 * interfering the game.
 * 
 * @author phlee
 * 
 */
public abstract class AnimationThread implements Runnable {

	protected AnimationCanvas canvas;
	protected long millisDelay;
	protected Animation animation;

	/**
	 * 
	 * @param canvas
	 *            - the canvas
	 * @param millisDelay
	 *            - the delay between each frame in milliseconds
	 * @param animation
	 *            - the Animation to show
	 */
	public AnimationThread(AnimationCanvas canvas, Animation animation,
			long millisDelay) {
		super();
		this.canvas = canvas;
		this.animation = animation;
		this.millisDelay = millisDelay;
	}

}
