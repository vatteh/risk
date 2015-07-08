package player;

import java.io.Serializable;

import map.Territory;

/**
 * A class that bundles together all of the info concerning an attack move. This
 * class is not responsible for validating this move
 * 
 * @author Ross
 * 
 */
public class Attack implements Serializable{

	private static final long serialVersionUID = -5702905772231535410L;
	private Territory attackingTerritory;
	private Territory defendingTerritory;
	private int value;
	private int easiness;
	private int weight;

	/**
	 * Constructs a new attack object
	 * 
	 * @param attackingTerritory
	 * @param defendingTerritory
	 */
	public Attack(Territory attackingTerritory, Territory defendingTerritory) {
		this.attackingTerritory = attackingTerritory;
		this.defendingTerritory = defendingTerritory;
		value = 0;
		easiness = 0;
		weight = 0;
	}

	/**
	 * Gets the territory to be attacking from
	 * 
	 * @return the territory to be attacking from
	 */
	public Territory getAttackingTerritory() {
		return attackingTerritory;
	}

	/**
	 * Gets the territory that is being attacked
	 * 
	 * @return the territory to be attacked
	 */
	public Territory getDefendingTerritory() {
		return defendingTerritory;
	}

	/**
	 * Gets a number representing how valuable winning this territory would be
	 * 
	 * @return the value associated with this attack
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Gets a number representing how easily this attack will be successfully
	 * completed
	 * 
	 * @return the easiness associated with this attack
	 */
	public int getEasiness() {
		return easiness;
	}

	/**
	 * Gets a number representing good of an attack choice this is.
	 * 
	 * @return the weight associated with this attack
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * Sets a new value for this attack
	 * 
	 * @param newValue
	 *            , the new value for this attack
	 */
	public void setValue(int newValue) {
		value = newValue;
	}

	/**
	 * Sets a new easiness for this attack
	 * 
	 * @param newEasiness
	 *            , the new easiness associated with this attack
	 */
	public void setEasiness(int newEasiness) {
		easiness = newEasiness;
	}

	/**
	 * Sets a new weight for this attack, after being calculated in another
	 * class
	 * 
	 * @param newWeight
	 *            , the new weight associated with this attack
	 */
	public void setWeight(int newWeight) {
		weight = newWeight;
	}
}