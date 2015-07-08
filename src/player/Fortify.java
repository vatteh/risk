package player;

import java.io.Serializable;

import map.Territory;

/**
 * A class that bundles together all of the info needed to make a fortify move,
 * so that it can be passed around as a cohesive unit. This class is not
 * responsible for checking to make sure it is a valid move.
 * 
 * @author Ross
 * 
 */

public class Fortify implements Serializable{

	private static final long serialVersionUID = -244761174970391046L;
	private Territory fortifyFrom;
	private Territory fortifyTo;
	private int amountToMove;

	/**
	 * Constructs a new fortify object
	 * 
	 * @param from
	 *            , the territory where troops will be moved from
	 * @param to
	 *            , the territory where troops will be moved to
	 * @param amount
	 *            , the amount of troops to be moved from one territory to the
	 *            other
	 */
	public Fortify(Territory from, Territory to, int amount) {
		fortifyFrom = from;
		fortifyTo = to;
		amountToMove = amount;
	}

	/**
	 * Gets the territory where troops will be moved from
	 * 
	 * @return the territory where troops will be moved from
	 */
	public Territory getFrom() {
		return fortifyFrom;
	}

	/**
	 * Gets the territory where troops will be moved to
	 * 
	 * @return the territory where troops will be moved to
	 */
	public Territory getTo() {
		return fortifyTo;
	}

	/**
	 * Gets the number of troops to be moved from one territory to the other
	 * 
	 * @return the number of troops to be moved from one territory to the other
	 */
	public int getAmount() {
		return amountToMove;
	}
}
