package map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import player.Player;

/**
 * Object that represents the 42 territories on the game board. Territories
 * belong in continents.
 * 
 * @author Duong Pham
 * @version April 11, 2009
 * 
 */
public class Territory implements Serializable{

	private static final long serialVersionUID = -2119152236631190492L;
	private int identificationNum;
	private String name;
	private Player player;
	private Continent continentIn;
	private int troopsOnTerritory;
	private int wikiValue;
	private int timesAttacked;
	private int troopsOnTerritoryAllTime;
	private int timesDefended;
	
	/**
	 * This is the constructor, it needs the continent and its identification number.
	 * 
	 * @param continent
	 * 			the continent it belongs in.
	 * @param identificationNum
	 * 			its unique identification number.
	 */
	public Territory(Continent continent, int identificationNum) {
		this.identificationNum = identificationNum;
		continentIn = continent;
		name = map.Territories.class.getDeclaredFields()[identificationNum - 1]
				.getName();
		troopsOnTerritory = 0;
		troopsOnTerritoryAllTime = 0;
		timesAttacked = 0;
		timesDefended = 0;
	}
	
	/**
	 * The total times attacked over the course of the game.  Used for Statistics class.
	 * 
	 * @return the number of times this territory has attacked another territory.
	 */
	public int getTimesAttacked() {
		return timesAttacked;
	}
	
	/**
	 * Troops on the territory over time.  Used for Statistics class.
	 * 
	 * @return the number of troops that have passed through this territory.
	 */
	public int getTroopsOnTerritoryAllTime() {
		return troopsOnTerritoryAllTime;
	}
	
	/**
	 * Times defended aganist an attack.  Used for Statistics class.
	 * 
	 * @return the number of times it has defended 
	 */
	public int getTimesDefended() {
		return timesDefended;
	}
	
	/**
	 * Increases times attack by 1.  Used for Statistics class.
	 *
	 */
	public void incrementTimesAttacked() {
		timesAttacked++;
	}
	
	/**
	 * Increases times this territory acts as a defender by 1.  Used for Statistics class.
	 *
	 */
	public void incrementTimesDefended() {
		timesDefended++;
	}
	
	
	/**
	 * Returns the wiki value of the territory.
	 * 
	 * @return the number associated with the original Risk game map.
	 */
	public int getWikiValue() {
		return wikiValue;
	}

	/**
	 * Returns the unique identification number of the territory.
	 * 
	 * @return the unique identification number of the territory.
	 */
	public int getIdentificationNum() {
		return identificationNum;
	}

	/**
	 * Returns the owner of the territory.
	 * 
	 * @return the Player who owns the territory.
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Returns the name of the territory.
	 * 
	 * @return the name of the territory.
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
	 * Returns the continent it is in.
	 * 
	 * @return the continent that the territory is in.
	 */
	public Continent getContinentIn() {
		return continentIn;
	}

	/**
	 * Returns the number of troops on the territory.
	 * 
	 * @return the number of troops on the territory.
	 */
	public int getTroopsOnTerritory() {
		return troopsOnTerritory;
	}

	/**
	 * Sets the owner of the territory.
	 * 
	 * @param player
	 *            the new owner of the territory.
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	/**
	 * Sets its unique wiki value that is shown on the original Risk map.
	 * 
	 * @param wikiValue
	 * 			the value as displayed on the map image.
	 */
	public void setWikiValue(int wikiValue) {
		this.wikiValue = wikiValue;
	}

	/**
	 * Increments the number of troops to the territory. You can decrement by
	 * passing a negative value.
	 * 
	 * @param num
	 * 			the amount you wish to increment or decrement by.
	 */
	public void incrementTroops(int num) {
		if (num > 0) {
			troopsOnTerritoryAllTime += num;
		}
		troopsOnTerritory += num;
	}

	/**
	 * Returns if a territory is on the boundary.
	 * 
	 * @param territory
	 *            the territory to check for.
	 * @return true if a boundary, false otherwise.
	 */
	public boolean isBoundary() {
		Graph graph = new Graph();
		ArrayList<Integer> terrAdjNum = graph.getAdjacentList(this.identificationNum);
		for (Integer i : terrAdjNum) {
			/*
			 * Get all adjacent territories. If that territory has a neigboring
			 * territory that is an enemy (not owned by the player) than it is
			 * on the boundary. Or the enemy is wrapped inside friendly
			 * territories.  Just don't think about this.
			 */
			if (!continentIn.getMap().getTerritoryByID(i).getPlayer().equals(this.getPlayer())) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns if the territory is occupied - if it has a player associated with it.
	 * 
	 * @return true if a player owns it, false otherwise.
	 */
	public boolean isOccupied() {
		return getPlayer() != null;
	}
	
}
