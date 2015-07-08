package base;

import java.io.Serializable;
import java.util.Observable;

/**
 * GameSettings Class
 * 
 * Will keep track of the two general setting changeable during the
 * game: Sounds and Animations. 
 * @author Victor
 *
 */
public class GameSettings extends Observable implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3272407787617662847L;
	private boolean playSounds;
	private boolean playAnimations;
	
	/**
	 * Creates GameSettings and sets both to true
	 */
	public GameSettings() {
		playSounds = true;
		playAnimations = true;
	}
	
	/**
	 * Changes whether you want to play sounds or not.
	 * 
	 * @param play - whether to play sounds
	 */
	public void togglePlaySounds(boolean play) {
		playSounds = play;
	}
	
	/**
	 * Changes whether you want to play animations
	 * 
	 * @param play - whether to play animations.
	 */
	public void togglePlayAnimations(boolean play) {
		playAnimations = play;
	}
	
	/**
	 * Returns whether sounds are on or off.
	 * 
	 * @return whether sounds are off or on.
	 */
	public boolean getPlaySounds() {
		return playSounds;
	}
	
	/**
	 * Returns whether animations are on or off.
	 * 
	 * @return whether animations are on or off.
	 */
	public boolean getPlayAnimations() {
		return playAnimations;
	}
}
