package player;

import java.io.Serializable;

import map.Territory;

/**
 * Bundles all info related to a reinforcement move so that the information can
 * easily be passed around and accessed
 * 
 * @author Ross
 * 
 */
public class Reinforcement implements Serializable {

	private static final long serialVersionUID = -1564479380035007507L;
	private Territory territory;
	private int amount;

	/**
	 * Instantiates a new Reinforcement move
	 * 
	 * @param territory
	 *            , the territory to add troops to
	 * @param amount
	 *            , the amount of troops to add to that territory
	 */
	public Reinforcement(Territory territory, int amount) {
		this.territory = territory;
		this.amount = amount;
	}

	/**
	 * Gets the territory associated with this move
	 * 
	 * @return the territory associated with this move
	 */
	public Territory getTerritory() {
		return territory;
	}

	/**
	 * Gets the amount of troops associated with this move
	 * 
	 * @return the amount of troops associated with this move
	 */
	public int getAmount() {
		return amount;
	}
}
