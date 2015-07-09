package map;

import java.io.Serializable;
import java.util.ArrayList;

import player.Player;

/**
 * 
 * This class represents a collection of Territory objects. Each Territory is
 * associated with a continent, and each continent can be associated with more
 * than one Territory. We use this to help us determine if a Continent is
 * conquered or not, for the purpose of distributing bonuses if true. Continents
 * are stored in the Map class.
 * 
 * @author Duong Pham
 * @version April 11, 2009
 * 
 */
public class Continent implements Serializable{

	private String name;
	private int identificationNum;
	private ArrayList<Territory> territories;
	private int value;
	private Map map;

	/**
	 * This is the constructor.
	 * 
	 * @param territories
	 *            territories that make up the continent.
	 * @param name
	 *            the name of the continent.
	 * @param identificationNum
	 *            the unique identification number.
	 */
	public Continent(int identificationNum, int value, Map map) {
		this.identificationNum = identificationNum;
		this.value = value;
		name = map.Continents.class.getDeclaredFields()[identificationNum - 1]
				.getName();
		territories = new ArrayList<Territory>();
		this.map = map;
	}
	
	public Map getMap() {
		return map;
	}

	/**
	 * Returns the name of the continent.
	 * 
	 * @return the name of the continent.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the name of the territory with only the first letter of each word capitalized.
	 * 
	 * @return the name of the territory in a nice looking format.
	 */
	public String getFormattedName() {
		String result = name.replace("_", " ");
		result = result.toLowerCase();
		String[] words = result.split(" ");

		result = "";
		for (int i = 0; i < words.length; i++) {
			String firstLetter = "" + words[i].charAt(0);
			firstLetter = firstLetter.toUpperCase();
			words[i] = firstLetter + words[i].substring(1);

			result += words[i] + " ";
		}
		result = result.trim();
		return result;
	}

	/**
	 * Returns the unique identification number of the continent.
	 * 
	 * @return the identification number.
	 */
	public int getIdentificationNum() {
		return identificationNum;
	}

	/**
	 * Returns the value.  Some are worth more than others.
	 * 
	 * @return value of the continent.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Returns the list of territories associated with the continent.
	 * 
	 * @return the list of territories.
	 */
	public ArrayList<Territory> getTerritoryList() {
		return territories;
	}

	/**
	 * Adds a territory to the continent.
	 * 
	 * @param territory
	 *            the territory to add.
	 * @return true if successful, false otherwise.
	 */
	public boolean addTerritory(Territory territory) {
		return territories.add(territory);
	}
	
	/**
	 * Determines if a continent has been conquered or not.
	 * 
	 * @return true if conquered, false otherwise.
	 */
	public boolean isConquered() {
		if (this.getConqueredBy() == null) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the player who has conquered the continent. A player conquers it
	 * if it owns every territory in the continent, else nobody has conquered
	 * it.
	 * 
	 * @return the Player who has conquered the continent.
	 */
	public Player getConqueredBy() {
		Player possibleConquerer = territories.get(0).getPlayer();
		for (Territory t : territories) {
			if (t.getPlayer() == null)
				return null;
			if (!t.getPlayer().equals(possibleConquerer)) {
				return null;
			}
		}
		return possibleConquerer;
	}

}
