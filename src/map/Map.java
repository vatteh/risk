package map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import player.Player;

/**
 * Represents the entire game board. It is composed of all the continents and
 * territories.
 * 
 * @author Duong Pham
 * @version April 11, 2009
 * 
 */
public class Map implements Serializable{

	private static Map mapInstance;
	private static final long serialVersionUID = -7568651219828724710L;
	private static final Graph GRAPH = new Graph();
	private ArrayList<Continent> continentsList;
	private ArrayList<Territory> territoryList;
	
	/**
	 * This is the constructor. Make continents (add the territories to the
	 * continents) and has Map carry an instance of it.
	 * 
	 */
	private Map() {
		continentsList = new ArrayList<Continent>();
		territoryList = new ArrayList<Territory>();
		
		Continent northAmerica = new Continent(Continents.NORTH_AMERICA, 5,this);
		Continent southAmerica = new Continent(Continents.SOUTH_AMERICA, 2,this);
		Continent europe = new Continent(Continents.EUROPE, 5,this);
		Continent africa = new Continent(Continents.AFRICA, 3,this);
		Continent asia = new Continent(Continents.ASIA, 7,this);
		Continent oceania = new Continent(Continents.OCEANIA, 2,this);

		// add territories to NORTH_AMERICA
		for (int i = 1; i < 10; i++) {
			Territory t = new Territory(northAmerica, i);
			northAmerica.addTerritory(t);
			territoryList.add(t);
		}

		// add territories to SOUTH_AMERICA
		for (int i = 10; i < 14; i++) {
			Territory t = new Territory(southAmerica, i);
			southAmerica.addTerritory(t);
			territoryList.add(t);
			t.setWikiValue(i%9);
		}

		// add territories to EUROPE
		for (int i = 14; i < 21; i++) {
			Territory t = new Territory(europe, i);
			europe.addTerritory(t);
			territoryList.add(t);
			t.setWikiValue(i%13);
		}

		// add territories to AFRICA
		for (int i = 21; i < 27; i++) {
			Territory t = new Territory(africa, i);
			africa.addTerritory(t);
			territoryList.add(t);
			t.setWikiValue(i%20);
		}

		// add territories to ASIA
		for (int i = 27; i < 39; i++) {
			Territory t = new Territory(asia, i);
			asia.addTerritory(t);
			territoryList.add(t);
			t.setWikiValue(i%26);
		}

		// add territories to OCEANIA
		for (int i = 39; i < 43; i++) {
			Territory t = new Territory(oceania, i);
			oceania.addTerritory(t);
			territoryList.add(t);
			t.setWikiValue(i%38);
		}

		continentsList.add(northAmerica);
		continentsList.add(southAmerica);
		continentsList.add(europe);
		continentsList.add(africa);
		continentsList.add(asia);
		continentsList.add(oceania);
	}
	
	public static Map getInstance() {
		if (mapInstance == null) {
			mapInstance = new Map();
		}
		return mapInstance;
	}

	/**
	 * Returns the list of continents.
	 * 
	 * @return the list of continents.
	 */
	public ArrayList<Continent> getContinents() {
		return continentsList;
	}

	/**
	 * Returns all territories.
	 * 
	 * @return the list of all territories.
	 */
	public ArrayList<Territory> getTerritories() {
		return territoryList;
	}
	
	/**
	 * Returns Continent of that name.
	 * 
	 * @param name
	 * 			the name you give.
	 * @return the Continent with that name.
	 */
	public Continent getContinentByName(String name) {
		for (Continent c : continentsList) {
			if (c.getName().equalsIgnoreCase(name)) {
				return c;
			}
		}
		return null;
	}
	
	/**
	 * Returns Territory of that name.
	 * 
	 * @param name
	 * 			the name you give.
	 * @return the Territory with that name.
	 */
	public Territory getTerritoryByName(String name) {
		for (Territory t : territoryList) {
			if (t.getName().equalsIgnoreCase(name)) {
				return t;
			}
		}
		throw new IllegalArgumentException("NO SUCH NAME: " + name);
	}
	
	/**
	 * Returns the Continent if you know it's identification number.
	 * 
	 * @param continentIDNum
	 * 			the Continent's number.
	 * @return the Continent with that number.
	 */
	public Continent getContinentByID(int continentIDNum) {
		// Subtract by 1 because continents are indexed at 1
		return continentsList.get(continentIDNum-1);
	}
	
	/**
	 * Returns the Territory if you know it's idnetification number.
	 * 
	 * @param territoryIDNum
	 * 			the Territory's number.
	 * @return the Territory with that number.
	 */
	public Territory getTerritoryByID(int territoryIDNum) {
		// Subtract by 1 because continents are indexed at 1
		return territoryList.get(territoryIDNum-1);
	}
	
	/**
	 * Returns a list of all conquered Continents under the players possesion.
	 * 
	 * @param player
	 * 			the player to search on behalf of.
	 * @return the list of conquered continents.
	 */
	public ArrayList<Continent> getConqueredContinents(Player player) {
		ArrayList<Continent> conqueredContinentList = new ArrayList<Continent>();
		for (Continent c : continentsList) {
			if (c.isConquered() && c.getConqueredBy().equals(player)) {
				conqueredContinentList.add(c);
			}
		}
		return conqueredContinentList;
	}
	
	/**
	 * Returns a list of all conquered Continents. Non-conquered Continents are
	 * still up for grabs.
	 * 
	 * @return the list of all conquered continents.
	 */
	public ArrayList<Continent> getConqueredContinents() {
		ArrayList<Continent> conqueredList = new ArrayList<Continent>();
		for (Continent c : continentsList) {
			if (c.isConquered()) {
				conqueredList.add(c);
			}
		}
		return conqueredList;
	}

	/**
	 * Return the list of friendly territories.
	 * 
	 * @param player
	 *            the player to look on behalf of.
	 * @return a list of friendly territories.
	 */
	public ArrayList<Territory> getFriendlyTerritories(Player player) {
		ArrayList<Territory> friendlyList = new ArrayList<Territory>();
		for (Continent c : continentsList) {
			
			for (Territory t : c.getTerritoryList()) {
				if (t.getPlayer()==(player))
					friendlyList.add(t);
			}
		}
		return friendlyList;
	}
	
	/**
	 * Returns adjacent friendly territories.
	 * 
	 * @param territory
	 * 			the one to check for.
	 * @return the list of adjacent friendly territories.
	 */
	public ArrayList<Territory> getAdjacentFriendlyTerritories(Territory territory) {
		ArrayList<Territory> terr = getEnemyTerritories(territory.getPlayer());
		for (Territory t : terr) {
			if (!areAdjacent(territory, t)) {
				terr.remove(t);
			}
		}
		return terr;

	}
	
	/**
	 * Returns adjacent enemy territories.
	 * 
	 * @param territory
	 * 			the one to check for.
	 * @return the list of adjancent enemy territories
	 */
	public ArrayList<Territory> getAdjacentEnemyTerritories(Territory territory) {
		ArrayList<Territory> terr = getFriendlyTerritories(territory.getPlayer());
		// dont remove in a enhanced for loop
		for (int i = 0; i < terr.size(); i++) {
			if (!areAdjacent(territory, terr.get(i))) {
				terr.remove(terr.get(i));
			}
		}
		return terr;
	}

	/**
	 * Return the list of enemy territories.
	 * 
	 * @param player
	 *            the player to look on behalf of.
	 * @return a list of enemy territories.
	 */
	public ArrayList<Territory> getEnemyTerritories(Player player) {
		ArrayList<Territory> enemyList = new ArrayList<Territory>();
		for (Continent c : continentsList) {
			for (Territory t : c.getTerritoryList()) {
				if (!t.getPlayer().equals(player))
					enemyList.add(t);
			}
		}
		return enemyList;
	}

	/**
	 * Returns a list of all adjacent territories. Adjacent territories are ones
	 * that you can reach from that territory.
	 * 
	 * @param territory
	 *            the territory you are asking on behalf of.
	 * @return list of all adjacent territories.
	 */
	public ArrayList<Territory> getAdjacentTerritories(Territory territory) {
		ArrayList<Integer> adjacentTerritoriesNum = new ArrayList<Integer>();
		ArrayList<Territory> adjacentTerritories = new ArrayList<Territory>();
		int territoryNum = territory.getIdentificationNum();
		adjacentTerritoriesNum = GRAPH.getAdjacentList(territoryNum);
		for (Integer i : adjacentTerritoriesNum) {
			for (Territory t : territoryList) {
				if (t.getIdentificationNum() == i) {
					adjacentTerritories.add(t);
				}
			}
		}
		return adjacentTerritories;
	}
	
	/**
	 * Determines if two territories are adjacent.
	 * 
	 * @param territory1
	 * 			The first territory.
	 * @param territory2
	 * 			The second territory.
	 * @return true if adjacent, false otherwise.
	 */
	public boolean areAdjacent(Territory territory1, Territory territory2) {
		return (getAdjacentTerritories(territory1).contains(territory2));
	}

	/**
	 * Returns if the player has been defeated.  If the player has 0 friendly territories.
	 * 
	 * @param player
	 * 			the player to check for.
	 * @return true if can attack, false otherwise.
	 */
	public boolean isDefeated(Player player) {
		return getFriendlyTerritories(player).size() == 0;
	}

	
	/**
	 * Returns if the player can attack.  If they have a territory with at least 2 troops.
	 * 
	 * @param player
	 * 			the player to check for.
	 * @return true if can attack, false otherwise.
	 */
	public boolean canAttack(Player player) {
		if (enoughTroopsToAttack(player) == false) {
			return false;
		}
		if (canAttackInsurroundedSituation(player) == false) {
			return false;
		}
		return true;
	}
	
	/**
	 * Returns if all the territories are occupied.
	 * 
	 * @return true if all territories are occupied, false otherwise.
	 */
	public boolean areAllOccupied() {
		for (Territory t : territoryList) {
			if (!t.isOccupied()) {
				return false;
			}
		}
		return true;	
	}
	
	/**
	 * Returns if can attack in a surrounded situation.
	 * 
	 * @param player
	 * 			the plauer to check on behalf of.
	 * @return true if found a possible attack, false otherwise.
	 */
	public boolean canAttackInsurroundedSituation(Player player) {
		for (Territory t1 : player.getTerritoriesOwned()) {
			ArrayList<Territory> adjEnemies = getAdjacentEnemyTerritories(t1);
			if (adjEnemies.size() >= 1 && t1.getTroopsOnTerritory() > 1) {
				return true; // possible attack available
			}
		}
		return false;
	}
	
	/**
	 * Determines if there are enough troops for the player to attack.
	 * 
	 * @param player
	 * 			the one to search on behalf of.
	 * @return true if can attack, false otherwise.
	 */
	public boolean enoughTroopsToAttack(Player player) {
		int count = 0;
		for(Territory territory : getFriendlyTerritories(player)) {
			count += territory.getTroopsOnTerritory();
		}
		if (count <= getFriendlyTerritories(player).size()) {
			return false;
		}
		return true;
	}
	
	/**
	 * Get most foritified territory over the course of the game.  Used for Statistics class.
	 * 
	 * @return string of the list the of most fortified territories of all time.
	 */
	public String getMostFortifiedTAllTime() {
		Territory most = territoryList.get(0);
		for (Territory t : territoryList) {
			if (t.getTroopsOnTerritoryAllTime() > most.getTroopsOnTerritoryAllTime())
				most = t;
		}
		String result = "";
		for (Territory t : territoryList) {
			if (t.getTroopsOnTerritoryAllTime() == most.getTroopsOnTerritoryAllTime()) {
				result += t.getFormattedName() + ", ";
			}
		}
		return most.getTroopsOnTerritoryAllTime() + " troops on " + result.substring(0,result.length()-2);
	}
	
	
	/**
	 * Get the least fortified territory over time.  Used for Statistics class.
	 * 
	 * @return string of the list of the least fortified territories of all time.
	 */
	public String getLeastFortifiedTAllTime() {
		Territory least = territoryList.get(0);
		for (Territory t : territoryList) {
			if (t.getTroopsOnTerritoryAllTime() < least.getTroopsOnTerritoryAllTime())
				least = t;
		}
		String result = "";
		for (Territory t : territoryList) {
			if (t.getTroopsOnTerritoryAllTime() == least.getTroopsOnTerritoryAllTime()) {
				result += t.getFormattedName() + ", ";
			}
		}
		return least.getTroopsOnTerritoryAllTime() + " troops on " + result.substring(0,result.length()-2);
	}
	
	/**
	 * Get the most fortified territory of all time.  Used for Statistics class.
	 * 
	 * @return string of the most fortified territory owned by the human player.
	 */
	public String getCurrentMostFortified() {
		Territory maxFortifiedT = territoryList.get(0);
		for (Territory t : territoryList) {
			if (t.getTroopsOnTerritory() > maxFortifiedT.getTroopsOnTerritory())
				maxFortifiedT = t;
		}
		String result = "";
		for (Territory t : territoryList) {
			if (t.getTroopsOnTerritory() == maxFortifiedT.getTroopsOnTerritory()) {
				result += t.getFormattedName() + ", ";
			}
		}
		return result.substring(0,result.length()-2) + " fortified " + maxFortifiedT.getTroopsOnTerritory() + " times";
	}
	
	/**
	 * Get the least fortified territory over the course of the game.  Used for Statistics class.
	 * 
	 * @return string of the least fortified territory owned by the human player.
	 */
	public String getCurrentLeastFortified() {
		Territory minFortifiedT = territoryList.get(0);
		for (Territory t : territoryList) {
			if (t.getTroopsOnTerritory() < minFortifiedT.getTroopsOnTerritory())
				minFortifiedT = t;
		}
		String result = "";
		for (Territory t : territoryList) {
			if (t.getTroopsOnTerritory() == minFortifiedT.getTroopsOnTerritory()) {
				result += t.getFormattedName() + ", ";
			}
		}
		return result.substring(0,result.length()-2) + " only fortified " + minFortifiedT.getTroopsOnTerritory() + " times";
	}
	
	/**
	 * Return the territory that launches the most number of attacks.  Used for Statistics class.
	 * 
	 * @return string of the most attacked territory overall.
	 */
	public String getMostAttackedTerritory() {
		Territory mostAttackedT = territoryList.get(0);
		for (Territory t : territoryList) {
			if (t.getTimesAttacked() > mostAttackedT.getTimesAttacked())
				mostAttackedT = t;
		}
		String result = "";
		for (Territory t : territoryList) {
			if (t.getTimesAttacked() == mostAttackedT.getTimesAttacked()) {
				result += t.getFormattedName() + ", ";
			}
		}
		return result.substring(0,result.length()-2) + " launched " + mostAttackedT.getTimesAttacked() + " attacks";
	}
	
	/**
	 * Return the territory that launches the most number of attacks.  Used for Statistics class.
	 * 
	 * @return string of the most attacked territory overall.
	 */
	public String getLeastAttackedTerritory() {
		Territory leastAttackingT = territoryList.get(0);
		for (Territory t : territoryList) {
			if (t.getTimesAttacked() < leastAttackingT.getTimesAttacked())
				leastAttackingT  = t;
		}
		String result = "";
		for (Territory t : territoryList) {
			if (t.getTimesAttacked() == leastAttackingT .getTimesAttacked()) {
				result += t.getFormattedName() + ", ";
			}
		}
		return result.substring(0,result.length()-2) + " launched " + leastAttackingT.getTimesAttacked() + " attacks";
	}
	
	/**
	 * Return the most defefended territory.  Used for Statistics class.
	 * 
	 * @return string of the most defended territory overall.
	 */
	public String getMostDefendedTerritory() {
		Territory mostDefendedT = territoryList.get(0);
		for (Territory t : territoryList) {
			if (t.getTimesDefended() > mostDefendedT.getTimesDefended())
				mostDefendedT = t;
		}
		String result = "";
		for (Territory t : territoryList) {
			if (t.getTimesDefended() == mostDefendedT.getTimesDefended()) {
				result += t.getFormattedName() + ", ";
			}
		}
		return result.substring(0,result.length()-2) + " defended itself " + mostDefendedT.getTimesDefended() + " times";
	}
	
	/**
	 * Get the territory owned by the player as text.  Used for Statistics class.
	 * 
	 * @param player
	 * 			one to search on behalf of.
	 * @return string of all the territories owned by the player.
	 */
	public String getTerritoryOwnedText(Player player) {
		if (player.getTerritoriesOwned().size() > 0) {
		String result = "";
		for (Territory t : this.getFriendlyTerritories(player)) {
			result+= t.getFormattedName() + ", ";
		}
		return result.substring(0, result.length()-2);
		}
		return "";
	}
	
	/**
	 * Get continents owned by a player as text.  Used for Statistics class.
	 * 
	 * @param player
	 * 			the one to search on behalf of.
	 * @return string of all the continents owned by the player.
	 */
	public String getContinentsOwnedText(Player player) {
		if (this.getConqueredContinents(player).size() > 0) {
		String result = "";
		for (Continent c : getConqueredContinents(player)) {
			result += c.getFormattedName() + ", ";
		}
		return result.substring(0, result.length()-2);
		}
		return "";
	}
	
}
