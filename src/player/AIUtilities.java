package player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import view.DiceRoll;

import base.Card;
import base.CardType;
import base.Game;

import map.Continent;
import map.Map;
import map.Territory;

/**
 * 
 * 
 * A class that implements the utility pattern:
 * 
 * contains only static methods designed to aid the AI by providing key spatial
 * information.
 * 
 * This class also functions as a facade to the Territory and Player.
 * 
 * *
 * 
 * @author Phil
 */

public class AIUtilities implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 933856633950113866L;
	private static Map map;
	private static java.util.Map<Integer, Integer> continentToTerritorySize;
	private static java.util.Map<Strategy, Integer> pregameChooseContinentGreedy;

	public static void setMap(Map m) {
		if (map == null) {
			map = m;
		}
	}

	/**
	 * Returns a list of all adjacent enemy territories for the specified
	 * player. Note that a player's territories are not contiguous.
	 * 
	 * @param p
	 *            - the player who owns the territories to scan
	 * @return a list of adjacent territories for the specified player
	 */
	protected static List<Territory> getAllAdjacentEnemyTerritoriesFor(Player p) {

		List<Territory> result = new ArrayList();

		List<Territory> territoriesOwned = p.getTerritoriesOwned();

		for (Territory owned : territoriesOwned) {
			for (Territory adj : map.getAdjacentTerritories(owned)) {
				if (adj.getPlayer() != p && map.areAdjacent(owned, adj)) {
					result.add(adj);
				}
			}
		}
		return result;
	}

	/**
	 * Returns a list of all 'edge' territories for the specified player. An
	 * edge territory is defined as a territory with one or more enemies
	 * adjacent to it.
	 * 
	 * @param p
	 *            - the player who owns the territories to scan
	 * @return a list of edge territories for the player
	 */
	public static List<Territory> getAllEdgeTerritoriesFor(Player p) {
		List<Territory> result = new ArrayList();

		List<Territory> territoriesOwned = p.getTerritoriesOwned();

		for (Territory owned : territoriesOwned) {
			if (containsAdjacentEnemy(owned, p)) {
				result.add(owned);
			}
		}
		return result;
	}

	/**
	 * Returns a list of adjacent enemy territories based on a seed territory.
	 * This seed is an arbitrary origin. The scan is based on the owner, but
	 * unlike getAllAdjacentEnemyTerritoriesFor(p:Player), this method is region
	 * bound, meaning that it scans locally, not globally.
	 * 
	 * @param t
	 *            - the seed territory
	 * @return a list of adjacent enemy territories that represents a ring of
	 *         enemies outside of a region
	 */
	public static List<Territory> getAllAdjacentEnemyTerritoriesBySeed(
			Territory t) {

		if (t == null) {
			throw new NullPointerException();
		}

		List<Territory> edgeTerritories = getAllEdgeTerritoriesBySeed(t);
		Set<Territory> set = enemiesAdjacentToBoundary(edgeTerritories, t
				.getPlayer());
		List<Territory> result = new ArrayList(set.size());
		result.addAll(set);
		return result;
	}

	/**
	 * Similar to getAllAdjacentEnemyTerritoriesBySeed():
	 * 
	 * returns a list of edge territories in reference to a bounded region at
	 * origin t (the seed Territory): defined as a Territory with at least one
	 * enemy
	 * 
	 * 
	 * @param t
	 *            - the seed territory
	 * @return a list of edge territories that represents the boundaries of a
	 *         region
	 */
	public static List<Territory> getAllEdgeTerritoriesBySeed(Territory t) {
		List<Territory> boundRegion = getBoundRegionBySeed(t);
		return determineEdgesInRegion(boundRegion);

	}

	/**
	 * Uses the same perspective as getAllEdgeTerritoriesBySeed(), except it
	 * applies for non-edges as well;
	 * 
	 * @param t
	 * @return
	 */
	public Collection<Territory> getAllFriendlyTerritoriesBySeed(Territory t) {

		Set<Territory> result = new HashSet<Territory>();
		adjacentFill(t, result, t.getPlayer());

		return result;

	}

	/**
	 * Provides a list of territories owned by specified player whose
	 * territories contain more than the specified number of troops
	 * 
	 * @param p
	 *            - the player
	 * @param specifiedTroops
	 *            - the minimum number of troops - 1
	 * @return a list of territories that fits the specifiedTroops condition
	 */
	public static List<Territory> getAllTerritoriesWithMoreThanSpecifiedFor(
			Player p, int specifiedTroops) {
		List<Territory> result = new ArrayList();
		for (Territory next : p.getTerritoriesOwned()) {
			if (next.getTroopsOnTerritory() > specifiedTroops
					|| specifiedTroops < 0) {
				result.add(next);
			}
		}
		return result;
	}

	/**
	 * Determines whether or not a player can potentially attack another country
	 * 
	 * @param p
	 *            - the player
	 * @return true if the player has at least one available attack and false if
	 *         the player does not.
	 */
	public static boolean canAttack(Player p) {

		int minTerritories = 2;
		for (Territory t : p.getTerritoriesOwned()) {
			if (t.getTroopsOnTerritory() >= minTerritories) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Determines whether or not a player can make a valid fortify move
	 * 
	 * @param p
	 *            - the player
	 * @return true if the player can make a valid fortify move, false if not
	 */
	public static boolean canFortify(Player p) {
		int minTerritories = 2;
		boolean cond1 = false;
		for (Territory t : p.getTerritoriesOwned()) {
			if (t.getTroopsOnTerritory() >= minTerritories) {
				cond1 = true;
			}
		}

		return cond1 && p.getTerritoriesOwned().size() >= 2;
	}

	/**
	 * Gets the territory with the given name
	 * 
	 * @param theGame
	 *            - the instance of Game
	 * @param name
	 *            - the name of the desired territory in lowercase
	 * @return the territory with the given name
	 * @throws IllegalArgumentException
	 */
	public static Territory retrieveTerritoryByName(Game theGame, String name) {
		List<Continent> continents = theGame.getMap().getContinents();

		for (Continent c : continents) {
			for (Territory t : c.getTerritoryList()) {
				if (t.getName().toLowerCase().equals(name.toLowerCase())) {
					return t;
				}
			}
		}
		throw new IllegalArgumentException("INVALID TERRITORY NAME: " + name);
	}

	private static List<Territory> getBoundRegionBySeed(Territory t) {
		Set<Territory> set = new HashSet<Territory>();
		adjacentFill(t, set, t.getPlayer());
		List<Territory> result = new ArrayList(set.size());
		result.addAll(set);
		return result;
	}

	private static void adjacentFill(Territory seed, Set<Territory> result,
			Player p) {
		if (result == null) {
			result = new HashSet<Territory>();
		}
		for (Territory adj : map.getAdjacentTerritories(seed)) {
			boolean unique = false;
			if (adj.getPlayer() == p) {
				unique = result.add(adj);
				if (unique) {
					adjacentFill(adj, result, p);
				}
			}
		}
	}

	private static boolean containsAdjacentEnemy(Territory t, Player p) {
		for (Territory next : map.getAdjacentTerritories(t)) {
			if (next.getPlayer() != p) {
				return true;
			}
		}
		return false;
	}

	private static List<Territory> determineEdgesInRegion(List<Territory> region) {
		List<Territory> result = new ArrayList(region.size());
		for (Territory next : region) {
			Player p = next.getPlayer();
			if (containsAdjacentEnemy(next, p)) {
				result.add(next);
			}
		}
		return result;
	}

	private static Set<Territory> enemiesAdjacentToBoundary(
			List<Territory> boundary, Player p) {
		Set<Territory> result = new HashSet<Territory>();

		for (Territory next : boundary) {
			fillAdjacentEnemies(next, result, p);
		}
		return result;
	}

	private static void fillAdjacentEnemies(Territory seed,
			Set<Territory> result, Player p) {
		for (Territory adj : map.getAdjacentTerritories(seed)) {
			if (seed.getPlayer() != p) {
				result.add(adj);
			}
		}
	}

	/**
	 * Gets a list of territories that are not owned by anyone yet
	 * 
	 * @return A list of territories that do not have a player on them
	 */
	public static List<Territory> getVirginTerritories() {
		List<Territory> result = new ArrayList();
		for (Territory t : map.getTerritories()) {
			if (!t.isOccupied()) {
				result.add(t);
			}
		}
		return result;
	}

	/**
	 * Gets a territory that is owned by the given player, has at least as many
	 * troops as specified, and borders another one of this player's countries
	 * 
	 * @param t
	 *            - The territory that the returned territory must border
	 * @param thePlayer
	 *            - The player whose territory you are looking for
	 * @param minimumTroops
	 *            - The minimum number of troops that the returned territory is
	 *            to have on it
	 * @return a territory that borders the given territory, is owned by the
	 *         given player, and has at least the minimum number of troops on
	 *         it. Returns null if no territory satisfies all of these
	 *         conditions.
	 */
	public static Territory getAnyFriendlyTerritoryAdjacentTo(Territory t,
			Player thePlayer, int minimumTroops) {
		for (Territory own : thePlayer.getTerritoriesOwned()) {
			if (map.areAdjacent(t, own)
					&& own.getTroopsOnTerritory() >= minimumTroops) {
				return own;
			}
		}
		return null;
	}

	private static double averageOf(Collection<Integer> list) {
		double sum = 0;
		for (Integer i : list) {
			sum += i;
		}

		return sum / list.size();

	}

	private static int sumOf(Collection<Integer> list) {
		int sum = 0;
		for (Integer i : list) {
			sum += i;
		}
		return sum;
	}

	/**
	 * 
	 * 
	 * Weight is defined as a function of how many troops are on that territory
	 * and the number of troops on its adjacent territories.
	 * 
	 * W = Troops on territory + average(troops on territory for all its
	 * adjacent territories that are not friendly) + average(troops on territory
	 * for all its adjacent territories that are friendly)/2
	 * 
	 * This method is used by the expert AI, but not the easy AI
	 * 
	 * This is similar to the methodology of Google Pagerank
	 * 
	 * @param t
	 *            - the specified territory
	 * @return the weight of the specified territory
	 */
	public static int calculateWeightOfTerritory(Territory t,
			Enum<EvaluationStrategy> e) {

		if (e == EvaluationStrategy.BELLIGERENT) {
			return weight_attackStrength(t);
		}

		else if (e == EvaluationStrategy.MANIFEST_DESTINY) {
			return weight_manifestDestiny(t);
		} else if (e == EvaluationStrategy.DEFENSE) {
			return weight_vulnerability(t);
		}else if(e==EvaluationStrategy.FORTIFICATION){
			return weight_fortification(t);
		}

		else
			throw new IllegalArgumentException("No such eval strategy: " + e);

	}

	private static int preGameWeight(Territory t, Strategy s) {
		// return inContinent(t, AIUtilities.mostCriticalContinent(null));
		return inContinent(t, AIUtilities.aiChooseTerritoryGreedy(s));
	}

	/**
	 * 
	 * Determines the highest weighted Terrritory.
	 * 
	 * @param s
	 * 			the strategy to work on behalf of.
	 * @return the territory with the highest weight.
	 */
	protected static Territory highestWeightPregame(Strategy s) {
		int highest = 0;
		Territory result = null;
		for (Territory t : getVirginTerritories()) {
			int next = preGameWeight(t, s);
			if (next > highest) {
				highest = next;
				result = t;
			}
		}
		if (highest == 0) {

			AIUtilities.registerGreedyAI(s);
			return highestWeightPregame(s);
		}
		return result;

	}

	/**
	 * 
	 * Returns weight for the manifest destiny strategy.
	 * 
	 * @param t
	 * 			the territory to check for
	 * @return the weight of the territory for a manifest destiny strategy.
	 */
	private static int weight_manifestDestiny(Territory t) {
		Player p = t.getPlayer();
		int continent = AIUtilities.mostCriticalContinent(p);

		int r1 = inContinent(t, continent);
		if (r1 > 0) {
			r1 += AIUtilities.getEnemyTerritoriesAdjacentTo(t).size() > 0 ? 1
					: 0;
		}

		return r1;
	}

	private static int inContinent(Territory t, int continentID) {
		if (t.getContinentIn().getIdentificationNum() == continentID) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * calculates the weight of a territory that factors in
	 * 
	 * two variables:
	 * 
	 * the number of troops on the territory and the average number of troops on
	 * the surrounding friendly territories. Both variables are weighted
	 * equally.
	 * 
	 * Territories that are more fortified have higher weight.
	 * 
	 */
	private static int weight_attackStrength(Territory t) {
		int troops = t.getTroopsOnTerritory();
		Player p = t.getPlayer();
		List<Integer> listEnemy = new ArrayList();
		List<Integer> listFriendly = new ArrayList();

		for (Territory next : map.getAdjacentTerritories(t)) {
			if (next.getPlayer() != p) {
				listEnemy.add(next.getTroopsOnTerritory());
			} else {
				listFriendly.add(next.getTroopsOnTerritory());
			}
		}

		int adjEnemyTroops = (int) sumOf(listEnemy);
		int adjFriendlyTroops = (int) sumOf(listFriendly);

		return troops + ((int) averageOf(listFriendly));
	}

	/**
	 * 
	 * calculates the weight of a territory that factors in two variables:
	 * 
	 * the vulnerability of a territory and the weakness of a territory
	 * 
	 * territories that are vulnerable and weak have higher weight.
	 * 
	 * 
	 */
	private static int weight_vulnerability(Territory t) {
		Player p = t.getPlayer();
		List<Integer> listEnemies = new ArrayList();
		List<Integer> listFriendly = new ArrayList();
		for (Territory next : map.getAdjacentTerritories(t)) {
			if (next.getPlayer() != p) {
				listEnemies.add(next.getTroopsOnTerritory());
			} else {
				listFriendly.add(next.getTroopsOnTerritory());
			}
		}
		return 3 * sumOf(listEnemies);
	}
	
	private static int weight_fortification(Territory t){
		Player p = t.getPlayer();
		List<Integer> listEnemies = new ArrayList();
		List<Integer> listFriendly = new ArrayList();
		for (Territory next : map.getAdjacentTerritories(t)) {
			if (next.getPlayer() != p) {
				listEnemies.add(next.getTroopsOnTerritory());
			} else {
				listFriendly.add(next.getTroopsOnTerritory());
			}
		}
		return sumOf(listEnemies) - t.getTroopsOnTerritory();
	}

	/**
	 * 
	 * @param arg
	 * @param central
	 * @return
	 */
	public static double calcuateWeightDistributionRatio(Territory arg,
			Territory central, Enum<EvaluationStrategy> e) {

		Collection<Territory> adj = AIUtilities
				.getFriendlyTerritoriesAdjacentTo(central);

		int numAdj = adj.size();

		double summation = 0;
		double argVal = 0;

		argVal = calculateWeightOfTerritory(arg, e);

		for (Territory next : adj) {
			summation += calculateWeightOfTerritory(next, e);
		}

		return argVal / summation;

	}

	/**
	 * Finds the Territory of the highest Weight of the given coll of
	 * territories. If there are multiple territories that have equally high
	 * weight, then it returns an arbitrary territory from that group of
	 * territories
	 * 
	 * @param territory
	 * @return
	 */
	public static Territory highestWeight(Collection<Territory> territories,
			Enum<EvaluationStrategy> e) {

		if (territories.size() == 0) {
			throw new IllegalArgumentException("territories coll size==0");
		}

		Territory res = null;
		int max = -1;

		List<Territory> temp = new ArrayList();

		for (Territory next : territories) {
			int val = calculateWeightOfTerritory(next, e);
			if (val >= max) {
				temp.add(next);
				max = val;
			}
		}

		res = temp.get((int) DiceRoll.generateRandomNumber(0, temp.size() - 1));

		return res;
	}

	/**
	 * 
	 * Equivalent to the perspective of highestWeight() except for the rank
	 * parameter, which is an index to find a territory based on its weight rank
	 * 
	 * @param territories
	 * @param rank
	 * @return
	 */
	public static Territory territoryByRank(Collection<Territory> territories,
			int rank, Enum<EvaluationStrategy> e) {
		if (rank > territories.size() || rank <= 0) {
			throw new IllegalArgumentException("rank out of bounds: " + rank);
		}

		int index = territories.size() - rank;
		List<Integer> list = new ArrayList(territories.size());

		HashMap<Integer, Territory> map = new HashMap();

		if (territories instanceof List) {
			Collections.shuffle((List) territories);
		}
		for (Territory t : territories) {
			int val = calculateWeightOfTerritory(t, e);
			list.add(val);
			map.put(val, t);
		}
		Collections.sort(list);

		return map.get(list.get(index));

	}

	/**
	 * Returns the ranked weight of the map.
	 * 
	 * @param territories
	 * 			the territory and it's weight.
	 * @param e
	 * 			the evaluation strategy
	 * @return map of the territory and its weight.
	 */
	public static TreeMap<Integer, Territory> getRankedWeightMap(
			Collection<Territory> territories, Enum<EvaluationStrategy> e) {
		TreeMap<Integer, Territory> map = new TreeMap();

		if (territories instanceof List) {
			Collections.shuffle((List) territories);
		}
		for (Territory t : territories) {
			int val = calculateWeightOfTerritory(t, e);
			map.put(val, t);
		}
		return map;
	}

	/**
	 * Returns collection of adjeacent freidnly territories.
	 * 
	 * @param t
	 * 			the territory to search on behalf.
	 * @return collection of adjacent territories.
	 */
	public static Collection<Territory> getFriendlyTerritoriesAdjacentTo(
			Territory t) {
		Collection<Territory> coll = map.getAdjacentTerritories(t);
		Set<Territory> result = new HashSet();
		Player p = t.getPlayer();
		for (Territory next : coll) {
			if (next.getPlayer() == p) {
				result.add(next);
			}
		}
		return result;
	}

	/**
	 * Returns adjacnet enemy territories.
	 * 
	 * @param t
	 * 			territory to search on behalf of.
	 * @return the colleciton of adjacent enemy territories.
	 */
	public static Collection<Territory> getEnemyTerritoriesAdjacentTo(
			Territory t) {
		Collection<Territory> coll = map.getAdjacentTerritories(t);
		Set<Territory> result = new HashSet();
		Player p = t.getPlayer();
		for (Territory next : coll) {
			if (next.getPlayer() != p) {
				result.add(next);
			}
		}
		return result;
	}

	/**
	 * Determines if map contains unoccupied territory.
	 * 
	 * @param coll
	 * 			the colleciton of territories.
	 * @return true if virgin, false otherwise.
	 */
	public static boolean mapContainsVirginTerritories(
			Collection<Territory> coll) {
		for (Territory t : coll) {
			if (t.getPlayer() == null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * The ratio of the strongest continents territories divided by all the territories.
	 * 
	 * @param p
	 * 			the strategy to look on behalf of.
	 * @return the ratio.
	 */
	public static double calculateRatioOfModeToTerritoriesInContinent(Strategy p) {

		int myContinent = AIUtilities.aiChooseTerritoryGreedy(p);

		int[] modeIndex = modeIndex(getTerritoryIDColl(p));
		double mode = modeIndex[1];
		int index = modeIndex[0];
		return mode / getTerritoriesInContinent(index);
	}

	private static Collection<Integer> getTerritoryIDColl(Strategy p) {
		Collection<Territory> c;
		if (p == null) {
			c = map.getTerritories();
		} else {

			c = ((Player) p).getTerritoriesOwned();
		}

		Collection<Integer> territoryIDColl = new ArrayList(c.size());
		for (Territory t : c) {
			territoryIDColl.add(t.getContinentIn().getIdentificationNum());
		}
		return territoryIDColl;
	}

	private static int getTerritoriesInContinent(int id) {
		AIUtilities.buildContinentIDToTerritorySizeMap();
		return continentToTerritorySize.get(id);
	}

	private static int mostCriticalContinent(Player p) {

		int max = -1;
		int result = 0;
		for (Continent c : map.getContinents()) {
			if (!(AIUtilities.getNumberOfTerritoriesOccupiedIn(c, null) == c
					.getTerritoryList().size())) {
				int nextVal = AIUtilities
						.getNumberOfFriendlyTerritoriesOccupiedIn(c,
								(Strategy) p);
				if (nextVal > max) {
					max = nextVal;
					result = c.getIdentificationNum();
				}
			}
		}
		//int[] modes = modeIndex(getTerritoryIDColl(p));
		return result;
	}

	/**
	 * 
	 * 
	 * @param c
	 * @return an int array: {mode index , mode}
	 */
	private static int[] modeIndex(Collection<Integer> c) {
		java.util.Map<Integer, Integer> modeMap = new HashMap();
		int max = -1;

		int result[] = new int[2];

		for (Integer i : c) {

			Integer get = modeMap.get(i);

			if (get == null) {
				get = 0;
			}

			modeMap.put(i, get + 1);

		}
		for (Integer i : modeMap.keySet()) {
			int next = modeMap.get(i);
			if (next > max) {
				result[0] = i;
				result[1] = next;
				max = next;
			}
		}
		return result;
	}

	private static void buildContinentIDToTerritorySizeMap() {

		if (continentToTerritorySize != null) {
			return;
		}

		continentToTerritorySize = new HashMap();

		for (int i = 1; i <= 6; i++) {
			for (Territory t : map.getTerritories()) {
				if (t.getIdentificationNum() == i) {

					Integer value = continentToTerritorySize.get(i);
					if (value == null) {
						value = 0;
					}
					value += 1;
					continentToTerritorySize.put(i, value);
				}
			}
		}
	}

	/**
	 * Lets the AI choose a territory greedily.
	 * 
	 * @param s
	 * 			the strategy to act on behalf of.
	 * @return continent id number.
	 */
	protected static int aiChooseTerritoryGreedy(Strategy s) {
		if (AIUtilities.pregameChooseContinentGreedy == null) {
			pregameChooseContinentGreedy = new HashMap();
		}

		if (!isRegistered(s)) {
			boolean b = registerGreedyAI(s);
		}

		return pregameChooseContinentGreedy.get(s);

	}

	/**
	 * Registers greedy AI after the pregame stage.
	 * 
	 * @param s
	 * 			the straegy to act on behalf of.
	 */
	protected static void registerGreedyAIAfterPregame(Strategy s) {
		if (AIUtilities.pregameChooseContinentGreedy == null) {
			pregameChooseContinentGreedy = new HashMap();
		}

		Integer oldCID = pregameChooseContinentGreedy.get(s);

		Continent oldC = null;
		Continent newC = null;
		if (oldCID != null) {

			oldC = continentIDToContinent(oldCID);
			newC = AIUtilities.getContinentWithTheMostOccupationBy(s, oldC);

			pregameChooseContinentGreedy.put(s, newC.getIdentificationNum());
		} else {
			newC = AIUtilities.getContinentWithTheMostOccupationBy(s, null);
			pregameChooseContinentGreedy.put(s, newC.getIdentificationNum());
		}

	}

	/**
	 * Registers greedy AI in later stages.
	 * 
	 * @param s
	 * 			the strategy to act on behalf of.
	 * @return true if successful, false otherwise.
	 */
	protected static boolean registerGreedyAI(Strategy s) {
		java.util.Map<Strategy, Integer> m = pregameChooseContinentGreedy;

		Continent newC = preferablyChooseContinentThatIsMostEmpty(s);
		int newValue = 0;

		if (!fullyOccupied(newC)) {
			newValue = newC.getIdentificationNum();
			m.put(s, newValue);
		} else {
			registerHumanOccupiedContinent(s);
		}
		return true;
	}

	private static void registerHumanOccupiedContinent(Strategy s) {
		java.util.Map<Continent, Integer> humans = getContinentsOccupiedByHuman();
		java.util.Map<Strategy, Integer> m = pregameChooseContinentGreedy;

		java.util.Map<Integer, Continent> collapsedReversed = reverseContinentMapping(humans);
		int newValue;
		while (collapsedReversed.size() > 0) {
			Integer maxOccupation = Collections.max(collapsedReversed.keySet());
			Continent get = collapsedReversed.get(maxOccupation);
			if (!fullyOccupied(get)) {
				newValue = get.getIdentificationNum();
				m.put(s, newValue);
				return;
			} else {
				collapsedReversed.remove(maxOccupation);
			}
		}
		m.put(s, getVirginTerritories().get(0).getContinentIn()
				.getIdentificationNum());
	}

	private static java.util.Map<Continent, Integer> getContinentsOccupiedByHuman() {
		HashMap<Continent, Integer> result = new HashMap();
		for (Continent c : AIUtilities.map.getContinents()) {
			for (Territory t : c.getTerritoryList()) {
				if (t.getPlayer() != null && t.getPlayer() instanceof Human) {
					if (result.keySet().contains(c)) {
						result.put(c, result.get(c) + 1);
					} else {
						result.put(c, 1);
					}
				}
			}
		}
		return result;
	}

	/**
	 *permits collapsing of keys
	 */
	private static java.util.Map<Integer, Continent> reverseContinentMapping(
			java.util.Map<Continent, Integer> map) {
		java.util.Map<Integer, Continent> result = new HashMap();
		for (Continent c : map.keySet()) {
			result.put(map.get(c), c);
		}
		return result;
	}

	private static int territoryForGreedyAI() {
		Collection<Integer> values = pregameChooseContinentGreedy.values();
		for (int i = 1; i <= 6; i++) {
			if (!values.contains(i)) {
				return i;
			}
		}
		throw new IllegalStateException(
				"expected registrar to have an available slot for greedy AI but did not");
	}

	private static Continent preferablyChooseContinentThatIsMostEmpty(Strategy s) {
		java.util.Map<Strategy, Integer> m = pregameChooseContinentGreedy;

		return getLeastOccupiedContinent(s);

	}

	private static boolean isRegistered(Strategy ai) {
		return pregameChooseContinentGreedy.keySet().contains(ai);
	}

	private static Continent getLeastOccupiedContinent(Strategy s) {
		int next = -1;
		Continent result = null;
		List<Continent> list = map.getContinents();
		Collections.shuffle(list);
		for (Continent c : list) {
			int territoriesIn = getNumberOfTerritoriesOccupiedIn(c, null);
			if (result == null || territoriesIn < next) {
				next = territoriesIn;
				result = c;
			}
		}
		return result;
	}

	private static int getNumberOfTerritoriesOccupiedIn(Continent c,
			Strategy exception) {
		int number = 0;
		for (Territory t : c.getTerritoryList()) {
			if (t.isOccupied()) {
				if (t.getPlayer() != exception) {
					number++;
				}
			}
		}
		return number;
	}

	/**
	 * Gets the number of freindly territories occupied in the continent.
	 * 
	 * @param c
	 * 			continent to look in.
	 * @param s
	 * 			the strategy to act on behalf of.
	 * @return	number of friendly territories.
	 */
	protected static int getNumberOfFriendlyTerritoriesOccupiedIn(Continent c,
			Strategy s) {
		int number = 0;
		for (Territory t : c.getTerritoryList()) {
			if (t.getPlayer() == s) {
				number++;
			}
		}
		return number;
	}

	/**
	 * Determines if continent if fully occupied.
	 * 
	 * @param c
	 * 			continent to look at.
	 * @return true, false othewise.
	 */
	protected static boolean fullyOccupied(Continent c) {
		return c.getTerritoryList().size() == getNumberOfTerritoriesOccupiedIn(
				c, null);
	}

	/**
	 * Returns the percentage weight using equal averages.
	 * 
	 * @param values
	 * 			the values.
	 * @param weights
	 * 			the weights.
	 * @return	the percentage.
	 */
	protected static int getPercentageUsingEqualAverage(List<Integer> values,
			List<Integer> weights) {

		double sumPossible = sumOf(weights);

		double total = 0;

		for (int i = 0; i < values.size(); i++) {
			int valueNext = values.get(i);
			int weightNext = weights.get(i);
			double nextRatio = calculateRatio(valueNext, weightNext);
			total += (weightNext * nextRatio);

		}

		double percentage = (total / sumPossible) * 100;

		return (int) percentage;

	}

	private static double calculateRatio(int val, int possible) {
		return ((double) val) / possible;
	}

	/**
	 * Returns the continent by its id number.
	 * 
	 * @param id
	 * 			the continent's id number.
	 * @return the continent with that id number.
	 */
	protected static Continent continentIDToContinent(int id) {
		for (Continent c : map.getContinents()) {
			if (c.getIdentificationNum() == id) {
				return c;
			}
		}
		throw new IllegalArgumentException("No such territory ID: " + id);
	}

	/**
	 * Returns the collection of enemy territories in the continent.
	 * 
	 * @param c
	 * 			continent to look in.
	 * @param p
	 * 			player to search for.
	 * @return  collection of enemy territories in the continent.
	 */
	public static Collection<Territory> getEnemyTerritoriesInContinent(
			Continent c, Player p) {
		Set<Territory> coll = new HashSet<Territory>();
		for (Territory next : c.getTerritoryList()) {
			if (next.getPlayer() != p) {
				coll.add(next);
			}
		}
		return coll;
	}

	/**
	 * Returns the collection of friendly territories in the continent.
	 * 
	 * @param c
	 * 			continent to look in.
	 * @param p
	 * 			player to search for.
	 * @return  collection of friendly territories in the continent.
	 */
	public static Collection<Territory> getFriendlyTerritoriesInContinent(
			Continent c, Player p) {
		Set<Territory> coll = new HashSet<Territory>();
		for (Territory next : c.getTerritoryList()) {
			if (next.getPlayer() == p) {
				coll.add(next);
			}
		}
		return coll;
	}

	private static Continent getLeastPopulatedContinent(Continent exception) {

		HashMap<Integer, Continent> mapping = new HashMap();
		for (Continent c : map.getContinents()) {
			int sum = 0;
			if (c != exception) {
				for (Territory t : c.getTerritoryList()) {
					sum += t.getTroopsOnTerritory();
				}
				mapping.put(sum, c);
			}
		}

		return mapping.get(Collections.min(mapping.keySet()));

	}

	private static int totalHumanOccupation(Continent c) {
		int sum = 0;
		for (Territory t : c.getTerritoryList()) {
			if (t.getPlayer() instanceof Human)
				sum += t.getTroopsOnTerritory();
		}
		return sum;
	}

	private static Continent getContinentWithTheMostOccupationBy(Strategy p,
			Continent dontInclude) {

		int mostOccupation = -1;
		Continent result = null;

		for (Continent c : map.getContinents()) {

			int sum = 0;

			if (c != dontInclude) {
				for (Territory t : c.getTerritoryList()) {
					int troops = t.getTroopsOnTerritory();
					if (mostOccupation > troops && t.getPlayer() == p) {
						sum += troops;
					}
				}

				if (sum > mostOccupation) {
					result = c;
					mostOccupation = sum;
				}
			}

		}

		return result;
	}

	/**
	 * Returns the strongest territory in a collection.
	 * 
	 * @param coll
	 * 			collection to look in.
	 * @return the strongest territory in a collection.
	 */
	protected static Territory strongestTerritoryIn(Collection<Territory> coll) {

		int most = -1;
		Territory result = null;
		for (Territory t : coll) {
			int next = t.getTroopsOnTerritory();
			if (next > most) {
				result = t;
			}
		}
		return result;
	}

	/**
	 * Returns the borders territories of a continent.
	 * 
	 * @param c
	 * 			continent to look in.
	 * @return the collection of border territories in a continent.
	 */
	protected static Collection<Territory> getBordersOf(Continent c) {

		Set<Territory> borders = new HashSet();

		for (Territory t : c.getTerritoryList()) {
			for (Territory adj : map.getAdjacentTerritories(t)) {
				if (t.getContinentIn().getIdentificationNum() != c
						.getIdentificationNum()) {

					borders.add(t);
				}
			}
		}

		return borders;
	}

	/**
	 * Get non-border territories of a continent.
	 * 
	 * @param c
	 * 			continent to look in.
	 * @return	collection of innard territories.
	 */
	protected static Collection<Territory> getInnardsOf(Continent c) {

		Set<Territory> innards = new HashSet();

		return c.getTerritoryList();
	}

	/**
	 * Get possible cards that are valid for a trade.
	 * 
	 * @param toTrade
	 * 			possible cards to trade.
	 * @return valid set of cards that you can turn in.
	 */
	protected static List<Card> getPossibleCards(List<Card> toTrade) {

		List<Card> cards = new ArrayList<Card>();

		int soldier = 0;
		int cavalier = 0;
		int cannon = 0;
		int wild = 0;

		for (Card card : toTrade) {
			if (card.getCardType() == CardType.SOLDIER)
				soldier++;
			else if (card.getCardType() == CardType.CAVALIER)
				cavalier++;
			else if (card.getCardType() == CardType.CANNON)
				cannon++;
			else if (card.getCardType() == CardType.WILD) {
				wild++;
			}
		}

		if (soldier >= 3) {
			for (Card card : toTrade) {
				if (card.getCardType() == CardType.SOLDIER) {
					cards.add(card);
					if (cards.size() == 3)
						return cards;
				}
			}
		} else if (cavalier >= 3) {
			for (Card card : toTrade) {
				if (card.getCardType() == CardType.CAVALIER) {
					cards.add(card);
					if (cards.size() == 3)
						return cards;
				}
			}
		} else if (cannon >= 3) {
			for (Card card : toTrade) {
				if (card.getCardType() == CardType.CANNON) {
					cards.add(card);
					if (cards.size() == 3)
						return cards;
				}
			}
		} else if (soldier >= 1 && cavalier >= 1 && cannon >= 1) {
			boolean gotSoldier = false;
			boolean gotCavalier = false;
			boolean gotCannon = false;

			for (Card card : toTrade) {
				if (card.getCardType() == CardType.SOLDIER && !gotSoldier) {
					cards.add(card);
					gotSoldier = true;
				} else if (card.getCardType() == CardType.CAVALIER
						&& !gotCavalier) {
					cards.add(card);
					gotCavalier = true;
				} else if (card.getCardType() == CardType.CANNON && !gotCannon) {
					cards.add(card);
					gotCannon = true;
				}
			}

			return cards;
		} else if (wild >= 1) {

			for (Card card : toTrade) {
				if (card.getCardType() == CardType.WILD) {
					cards.add(card);
				}
			}

			for (Card card : toTrade) {
				cards.add(card);
				if (cards.size() == 3)
					return cards;

			}
		}

		return null;
	}

	private static boolean areTradeable(List<Card> c) {
		Card arg1 = c.get(0);
		Card arg2 = c.get(1);
		Card arg3 = c.get(2);
		return areTradeable(arg1, arg2, arg3);
	}

	private static boolean areTradeable(Card card1, Card card2, Card card3) {
		// Check parameters
		if (card1 == null || card2 == null || card3 == null) {
			return false;
		}
		// Case 1
		if (card2.getCardType() == card1.getCardType()
				&& card3.getCardType() == card1.getCardType()) {

			return true;
		}
		// Case 2
		else if ((card1.getCardType() != card2.getCardType())
				&& (card1.getCardType() != card3.getCardType())
				&& (card2.getCardType() != card3.getCardType())) {

			return true;
		}

		// Case 3
		boolean card1wild = (card1.getCardType() == CardType.WILD);
		boolean card2wild = (card2.getCardType() == CardType.WILD);
		boolean card3wild = (card3.getCardType() == CardType.WILD);

		if ((card1wild && card2wild) || (card1wild && card3wild)
				|| (card2wild && card3wild)) {

			return true;
		}
		// Case 4
		else if ((card1wild && card2.getCardType() == card3.getCardType())
				|| (card2wild && card1.getCardType() == card3.getCardType())
				|| (card3wild && card2.getCardType() == card1.getCardType())) {

			return true;
		}

		else {
			return false;
		}

	}

	private static Set<Card> buildRandomSet(List<Card> possiblities) {

		List<Card> newP = new ArrayList();

		for (Card c : possiblities) {
			newP.add(c);
		}

		Set<Card> result = new HashSet();
		while (result.size() < 3) {
			Card next = newP.get((int) DiceRoll.generateRandomNumber(0, newP
					.size() - 1));
			result.add(next);
			newP.remove(next);
		}

		return result;

	}

	private static int choose(int size) {
		switch (size) {
		case 5:
			return 10;
		case 4:
			return 4;
		case 3:
			return 3;
		}
		throw new IllegalArgumentException();
	}

}
