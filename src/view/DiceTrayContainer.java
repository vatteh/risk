package view;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;

public class DiceTrayContainer extends JPanel{

	DiceTray attacker;
	DiceTray defender;
	private static final int DICE_TRAY_MILLIS_DELAY = 10;
	
	/**
	 * Constructor for the DiceTrayContainer.
	 * 
	 */
	public DiceTrayContainer(){
		setLayout(new GridLayout(1,2));
		attacker = new DiceTray(DiceTray.ATTACKER_DICE_TRAY);
		defender = new DiceTray(DiceTray.DEFENDER_DICE_TRAY);
		setPreferredSize(new Dimension(DiceTray.WIDTH*2,DiceTray.HEIGHT));
		setMaximumSize(getPreferredSize());
		add(attacker);
		add(defender);
	}
	
	/**
	 * Takes a view.DiceRoll and fires a DiceRollAnimation on a space in this
	 * panel reserved for dice.
	 * 
	 * @param attacker
	 *            - the Attacker's DiceRoll
	 * @param defender
	 *            - the Defender's DiceRoll
	 */
	public void fireDiceRollAnimation(DiceRoll roll) {
		DiceRollAnimation anim1 = new DiceRollAnimation(roll);
		Thread d1 = new Thread(new DiceRollAnimator(this.attacker, anim1,
				DICE_TRAY_MILLIS_DELAY));

		DiceRollAnimation anim2 = new DiceRollAnimation(roll);
		Thread d2 = new Thread(new DiceRollAnimator(this.defender, anim2,
				DICE_TRAY_MILLIS_DELAY));
		d1.start();
		d2.start();

	}
	
	
	
}
