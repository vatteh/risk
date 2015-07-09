package player;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import view.DiceRoll;

import base.Card;
import map.Territory;
import map.Map;

/**
 * An abstract class representing a generic player
 * 
 * @author Ross
 * @extends Observable
 */
public abstract class Player extends Observable implements Serializable {

	private static final long serialVersionUID = 8027227376156968261L;
	private String name;
	private Color color;
	protected Map map;
	private List<Card> cards;
	protected int numAttackingDice = 3;
	protected int numDefendingDice = 2;
	private int troopsToGive;

	/**
	 * Constructs a generic player object
	 * 
	 * @param name
	 *            , the name of this player
	 * @param color
	 *            , the player's color
	 * @param map
	 *            , a reference to the map instance
	 */
	public Player(String name, Color color, Map map) {
		this.name = name;
		this.color = color;
		this.map = map;
		cards = new ArrayList<Card>();
	}

	/**
	 * Gets the name of this player
	 * 
	 * @return the name of this player
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets this player's color
	 * 
	 * @return this player's color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Gets a list of this player's cards.
	 * 
	 * @return a list of this player's cards.
	 */
	public List<Card> getCards() {
		return cards;
	}

	/**
	 * Removes a card from this player's list of cards and notifies its
	 * observers
	 * 
	 * @param toRemove
	 *            , the card that is being removed
	 * @return true if the card was successfully removed, false if it was not
	 */
	public boolean removeCard(Card toRemove) {
		boolean result = cards.remove(toRemove);
		this.setChanged();
		this.notifyObservers();
		return result;
	}

	/**
	 * Gets a list of all the territories owner by this player. For now, this is
	 * an alias for the getPlayerTerritories() method in the Map class. So using
	 * that method is preferred.
	 * 
	 * @return a list of all of the territories currently owned by this player
	 */
	public List<Territory> getTerritoriesOwned() {
		return map.getFriendlyTerritories(this);
	}

	/**
	 * Adds a card to this player's list of cards and notifies this player's
	 * observers
	 * 
	 * @param toGive
	 *            , card to be added to this player's list of cards.
	 */
	public void giveCard(Card toGive) {
		cards.add(toGive);
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * Gets this player's preferred number of dice to use while attacking
	 * 
	 * @return this player's preferred number of dice to use while attacking
	 */
	public int getNumAttackingDice() {
		return numAttackingDice;
	}

	/**
	 * Sets a new preferred number of dice for this player to use while
	 * attacking
	 * 
	 * @param newNum
	 *            , this player's new number of attacking dice. Will be adjusted
	 *            to be a number between 1 and 3, inclusive, if it does not fall
	 *            into this range
	 */
	public void setNumAttackingDice(int newNum) {
		if (newNum > 3)
			numAttackingDice = 3;
		else if (newNum < 1)
			numAttackingDice = 1;
		else
			numAttackingDice = newNum;
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * Gets this player's preferred number of dice to use while defending
	 * 
	 * @return this player's preferred number of dice to use while defending
	 */
	public int getNumDefendingDice() {
		return numDefendingDice;
	}

	/**
	 * Sets a new preferred number of dice for this player to use while
	 * defending
	 * 
	 * @param newNum
	 *            , this player's new number of defending dice. Must be either 1
	 *            or 2, and will be adjusted to the closer of these numbers if
	 *            it is not already one of them.
	 */
	public void setNumDefendingDice(int newNum) {
		if (newNum > 2)
			numDefendingDice = 2;
		else if (newNum < 1)
			numDefendingDice = 1;
		else
			numDefendingDice = newNum;
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * Chooses which country to reinforce. This should be called until the
	 * player has no more unassigned troops.
	 * 
	 * @return a Reinforcement object with all necessary information
	 */
	public abstract Reinforcement decideReinforcement();

	/**
	 * Decides on an attack to make.
	 * 
	 * @return an Attack object if the player wants to make another attack, or
	 *         null if the player is done attacking
	 */
	public abstract Attack decideAttack();

	/**
	 * Decides on a fortify move.
	 * 
	 * @return a Fortify object with all necessary information, or null if the
	 *         player decides not to fortify
	 */
	public abstract Fortify decideFortify();

	/**
	 * Determines how many troops the player wants to move from the territory
	 * they attacked from to the territory that they just conquered
	 * 
	 * @param attackingTerritory
	 *            - the territory that they attacked from
	 * @param defendingTerritory
	 *            - the territory that they just conquered
	 * @return An int representing the number of troops to move. Will be greater
	 *         than or equal to the number of attacking dice the player rolled
	 *         and will be less than the number of troops on the attacking
	 *         territory
	 */
	public abstract int decideFortifyAfterAttack(Territory attackingTerritory,
			Territory defendingTerritory);

	/**
	 * Increments the number of troops that the player can place
	 * 
	 * @param turnInValue
	 *            - the number of additional troops that the player has to place
	 */
	public void incrementTroopsToGive(int turnInValue) {
		troopsToGive += turnInValue;

	}

	/**
	 * Gets the number of troops that the player still has to place on
	 * territories
	 * 
	 * @return the number of troops that the player still has to place on
	 *         territories
	 */
	public int getTroopsToGive() {
		return troopsToGive;
	}

	/**
	 * Returns the number of troops that they player wants to move onto the
	 * territory that they just captured
	 * 
	 * @param attackingTerritory
	 *            - The territory that the player attacked from
	 * @param defendingTerritory
	 *            - The territory that the player just captured
	 * @return the number of troops to move onto the territory that was just
	 *         captured
	 */
	public int fortifyAfterAttack(Territory attackingTerritory,
			Territory defendingTerritory) {
		return 0;
	}

	/**
	 * Allows the player to choose a territory to occupy. Used in pregame stage
	 * when the user has elected to choose territories.
	 * 
	 * @return the territory that they user wants to occupy
	 */
	public abstract Territory getPregameTerritory();

	/**
	 * Gets the name of this player
	 * 
	 * @return the name of the player
	 */
	public String toString() {
		return name;
	}

}