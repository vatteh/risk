package player;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import base.Card;
import base.CardType;

import java.util.HashMap;

import view.ConsoleUpdate;
import view.DiceRoll;

import map.Continent;
import map.Map;
import map.Territory;

/**
 * An Risk AI that plays tough
 * 
 * @author Phil
 * 
 */
public class ExpertAIStrategy extends Player implements Strategy, Serializable {

	private static HashMap<Territory, Integer> cachedTerritoryToWeightMapping;

	private static final double probabilityBelligerent = .1;
	private static final double probabilityManifestDestiny = .75;
	private static final double probabilityDefensive = .05;

	public ExpertAIStrategy(String name, Color color, map.Map map) {
		super(name, color, map);
		if (cachedTerritoryToWeightMapping != null) {
			cachedTerritoryToWeightMapping = new HashMap();
		}
	}

	/**
	 * AI decides which reinforcement.
	 * 
	 * @return the reinforcement.
	 */
	@Override
	public Reinforcement decideReinforcement() {

		AIUtilities.setMap(map);

		Enum<EvaluationStrategy> strategy = decideEvaluationStrategy();

		if (strategy == EvaluationStrategy.DEFENSE) {
			return decideReinforcement_Defense();
		} else if (strategy == EvaluationStrategy.BELLIGERENT) {
			return this.decideReinforcement_Belligerent();

		} else if (strategy == EvaluationStrategy.MANIFEST_DESTINY) {
			return this.decideReinforcement_Manifest_Destiny();
		}

		return this.decideReinforcement_Manifest_Destiny();
	}

	private Reinforcement decideReinforcement_Defense() {
		Reinforcement result = null;
		Collection<Territory> possibleTargets = null;
		Collection<Territory> possibleBeneficiaries = null;
		Collection<Territory> optimalBeneficiaries = null;

		TreeMap<Integer, Continent> strongest = this
				.determineStrongestContinentsNotCaptured();

		Continent strongestContinent = (this
				.getContinentsInDescendingOrder(strongest)).get(0);

		possibleTargets = this.getEnemyTargetsDefensive();
		possibleBeneficiaries = this
				.getFriendlyTerritoriesAdjacentTo(possibleTargets);
		optimalBeneficiaries = this.getWithoutTerritoriesNotContainedIn(
				possibleBeneficiaries, strongestContinent);

		Collection<Territory> toConsider = possibleTargets;

		Territory weakest = getMostVulnerableTerritoryIn(possibleBeneficiaries);

		if (possibleBeneficiaries.size() == 0 || weakest == null) {
			return this.decideReinforcement_Manifest_Destiny();
		}

		result = new Reinforcement(weakest, 1);
		return result;
	}

	private Reinforcement decideReinforcement_Manifest_Destiny() {
		Reinforcement result = null;
		Collection<Territory> possibleTargets = null;
		Collection<Territory> possibleBeneficiaries = null;
		Collection<Territory> optimalBeneficiaries = null;

		TreeMap<Integer, Continent> strongest = this
				.determineStrongestContinentsNotCaptured();

		Continent strongestContinent = (this
				.getContinentsInDescendingOrder(strongest)).get(0);

		possibleTargets = this.getEnemyTargetsManifestDestiny();
		possibleBeneficiaries = this
				.getFriendlyTerritoriesAdjacentTo(possibleTargets);

		Territory weakest = getMostVulnerableTerritoryIn(possibleBeneficiaries);

		if (possibleBeneficiaries.size() == 0 || weakest == null) {
			strongest = this.determineStrongestContinents();
			strongestContinent = (this
					.getContinentsInDescendingOrder(strongest)).get(0);
			Collection<Territory> strongList = AIUtilities
					.getFriendlyTerritoriesInContinent(strongestContinent, this);
			weakest = this.getMostVulnerableTerritoryIn(strongList);

		}

		return new Reinforcement(weakest, 1);

	}

	private Reinforcement decideReinforcement_Belligerent() {
		Reinforcement result = null;
		Collection<Territory> possibleTargets = null;
		Collection<Territory> possibleBeneficiaries = null;

		TreeMap<Integer, Continent> strongest = this
				.determineStrongestContinents();

		possibleTargets = this.getEnemyTargetsBelligerent();
		possibleBeneficiaries = this
				.getFriendlyTerritoriesAdjacentTo(possibleTargets);

		Territory weakest = getMostVulnerableTerritoryIn(possibleBeneficiaries);

		if (possibleBeneficiaries.size() == 0 || weakest == null) {
			return this.decideReinforcement_Manifest_Destiny();
		}

		return new Reinforcement(weakest, 1);
	}

	/**
	 * AI decides if it wants to attack and returns the attack command.
	 * 
	 * @return the attack.
	 */
	@Override
	public Attack decideAttack() {

		AIUtilities.setMap(map);

		Enum<EvaluationStrategy> strategy = decideEvaluationStrategy();

		if (strategy == EvaluationStrategy.DEFENSE) {
			return decideAttack_Defense();
		} else if (strategy == EvaluationStrategy.MANIFEST_DESTINY) {
			return decideAttack_Manifest_Destiny();
		} else if (strategy == EvaluationStrategy.BELLIGERENT) {
			return decideAttack_Belligerent();
		}

		return null;
	}

	private Attack decideAttack_Defense() {

		TreeMap<Integer, Continent> strongest = this
				.determineStrongestContinentsNotCaptured();

		Continent strongestContinent = (this
				.getContinentsInDescendingOrder(strongest)).get(0);

		Attack result = null;
		Collection<Territory> possibleTargets = null;
		Collection<Territory> possibleFronts = null;
		Collection<Territory> optimalFronts = null;
		Collection<Territory> adjacentTargets = null;
		Territory toAttack = null;

		possibleTargets = this.getEnemyTargetsDefensive();
		possibleFronts = this.getFriendlyTerritoriesAdjacentTo(possibleTargets);
		optimalFronts = this.getWithoutTerritoriesNotContainedIn(
				possibleFronts, strongestContinent);

		Collection<Territory> frontToConsider = possibleFronts;

		Territory frontToUse = this
				.getMostVulnerableTerritoryIn(frontToConsider);

		while (frontToConsider.size() > 0) {
			if (frontToUse != null) {
				break;
			}

			else {
				frontToUse = this.getMostVulnerableTerritoryIn(frontToConsider);
			}
			frontToConsider.remove(frontToUse);
		}

		if (frontToUse == null || frontToUse.getTroopsOnTerritory() <= 1
				|| frontToConsider == null) {
			return this.getLastResortAttack();
		}

		adjacentTargets = AIUtilities.getEnemyTerritoriesAdjacentTo(frontToUse);

		result = determineOptimalDefensiveAttack(frontToUse, adjacentTargets);

		for (Territory t : possibleTargets) {

			possibleFronts = this.getFriendlyTerritoriesAdjacentTo(t);

			while (result == null) {

				if (possibleFronts.size() < 1) {
					break;
				}

				frontToUse = this.getMostVulnerableTerritoryIn(possibleFronts);

				if (frontToUse == null) {
					break;
				}

				adjacentTargets = AIUtilities
						.getEnemyTerritoriesAdjacentTo(frontToUse);

				if (adjacentTargets.size() < 1) {
					break;
				}

				result = determineOptimalDefensiveAttack(frontToUse,
						adjacentTargets);
				possibleFronts.remove(frontToUse);

			}
		}

		if (result != null) {
			return result;
		} else {
			return this.getLastResortAttack();
		}

	}

	private Attack decideAttack_Manifest_Destiny() {
		Attack result = null;
		Collection<Territory> possibleTargets = null;
		Collection<Territory> possibleFronts = null;
		Collection<Territory> adjacentTargets = null;

		TreeMap<Integer, Continent> strongest = this
				.determineStrongestContinentsNotCaptured();

		possibleTargets = this.getEnemyTargetsManifestDestiny();
		possibleFronts = this.getFriendlyTerritoriesAdjacentTo(possibleTargets);

		if (possibleFronts.size() == 0) {
			return decideAttack_Defense();
		}

		Territory frontToUse = this
				.getMostVulnerableTerritoryIn(possibleFronts);

		if (frontToUse == null || frontToUse.getTroopsOnTerritory() <= 1) {
			return decideAttack_Defense();
		}

		adjacentTargets = AIUtilities.getEnemyTerritoriesAdjacentTo(frontToUse);

		result = determineOptimalDefensiveAttack(frontToUse, adjacentTargets);

		while (result == null) {
			if (possibleFronts.size() < 1) {
				return decideAttack_Defense();
			}
			frontToUse = this.getLeastVulnerableTerritoryIn(possibleFronts);

			if (frontToUse == null) {
				return this.decideAttack_Defense();
			}

			adjacentTargets = AIUtilities
					.getEnemyTerritoriesAdjacentTo(frontToUse);

			if (adjacentTargets.size() < 1) {
				return this.decideAttack_Defense();
			}

			result = determineOptimalDefensiveAttack(frontToUse,
					adjacentTargets);
			possibleFronts.remove(frontToUse);

		}
		if (result != null) {
			return result;
		} else {
			return this.decideAttack_Defense();
		}

	}

	private Attack decideAttack_Belligerent() {
		Attack result = null;
		Collection<Territory> possibleTargets = null;
		Collection<Territory> possibleFronts = null;
		Collection<Territory> adjacentTargets = null;

		possibleTargets = this.getEnemyTargetsBelligerent();
		possibleFronts = this.getFriendlyTerritoriesAdjacentTo(possibleTargets);

		if (possibleFronts.size() == 0) {
			this.decideAttack_Defense();
		}

		Territory frontToUse = this
				.getMostVulnerableTerritoryIn(possibleFronts);

		if (frontToUse == null || frontToUse.getTroopsOnTerritory() <= 1) {
			return this.decideAttack_Manifest_Destiny();
		}

		adjacentTargets = AIUtilities.getEnemyTerritoriesAdjacentTo(frontToUse);

		result = this.determineOptimalOffensiveAttack(frontToUse,
				adjacentTargets);

		while (result == null) {

			possibleFronts.remove(frontToUse);

			frontToUse = this.getMostVulnerableTerritoryIn(possibleFronts);

			if (frontToUse == null) {
				return this.decideAttack_Defense();
			}

			adjacentTargets = AIUtilities
					.getEnemyTerritoriesAdjacentTo(frontToUse);

			if (adjacentTargets.size() < 1) {
				return this.decideAttack_Defense();
			}

			result = determineOptimalOffensiveAttack(frontToUse,
					adjacentTargets);

			possibleFronts.remove(frontToUse);
		}
		if (result != null) {
			return result;
		} else {
			return this.decideAttack_Defense();
		}
	}

	private Attack determineOptimalDefensiveAttack(Territory frontToUse,
			Collection<Territory> adjacentTargets) {
		Territory toAttack = null;
		Attack result = null;
		boolean optimal = false;

		if (adjacentTargets.size() == 0) {
			return null;
		}

		toAttack = getLeastVulnerableTerritoryIn(adjacentTargets);

		while (!optimal) {
			if (adjacentTargets.size() == 0) {
				return null;
			}

			toAttack = getLeastVulnerableTerritoryIn(adjacentTargets);

			result = new Attack(frontToUse, toAttack);

			adjacentTargets.remove(toAttack);
			optimal = this.isOptimalAttack(result);

		}

		return result;

	}

	private Attack determineOptimalOffensiveAttack(Territory frontToUse,
			Collection<Territory> adjacentTargets) {
		Territory toAttack = null;
		Attack result = null;
		boolean optimal = false;

		if (adjacentTargets.size() == 0) {
			return null;
		}

		toAttack = this.getMostVulnerableTerritoryIn(adjacentTargets);

		while (!optimal) {
			if (adjacentTargets.size() == 0) {
				return null;
			}
			result = new Attack(frontToUse, toAttack);

			toAttack = this.getMostVulnerableTerritoryIn(adjacentTargets);

			result = new Attack(frontToUse, toAttack);

			adjacentTargets.remove(toAttack);
			optimal = this.isOptimalAttack(result);

		}

		return result;

	}

	private boolean isOptimalAttack(Attack a) {

		double tolerance = .5;

		int attacking = a.getAttackingTerritory().getTroopsOnTerritory();
		int defending = a.getDefendingTerritory().getTroopsOnTerritory();

		return attacking > 1 && (attacking >= defending * tolerance);
	}

	/**
	 * AI determines if it wishes to fortify and if so return the fortify
	 * object.
	 * 
	 * @return fortify object.
	 */
	@Override
	public Fortify decideFortify() {

		AIUtilities.setMap(map);

		Enum<EvaluationStrategy> strategy = decideEvaluationStrategy();

		if (strategy == EvaluationStrategy.DEFENSE) {
			return decideFortify_Defense();
		} else if (strategy == EvaluationStrategy.MANIFEST_DESTINY) {
			return decideFortify_Manifest_Destiny();
		} else if (strategy == EvaluationStrategy.BELLIGERENT) {
			return this.decideFortify_Belligerent();
		}

		return null;
	}

	private Fortify decideFortify_Defense() {
		Attack result = null;
		Collection<Territory> possibleTargets = null;
		Collection<Territory> possibleBeneficiaries = null;
		Collection<Territory> optimalBeneficiaries = null;

		TreeMap<Integer, Continent> strongest = this
				.determineStrongestContinentsNotCaptured();

		Continent strongestContinent = (this
				.getContinentsInDescendingOrder(strongest)).get(0);

		possibleTargets = this.getEnemyTargetsDefensive();
		possibleBeneficiaries = this
				.getFriendlyTerritoriesAdjacentTo(possibleTargets);
		optimalBeneficiaries = this.getWithoutTerritoriesNotContainedIn(
				possibleBeneficiaries, strongestContinent);

		Collection<Territory> frontToConsider = optimalBeneficiaries.size() > 0 ? optimalBeneficiaries
				: possibleBeneficiaries;

		Territory beneficiary = this
				.getMostVulnerableTerritoryIn(frontToConsider);

		if (beneficiary == null) {
			return this.decideFortifyUsingCentrifugalSpread();
		}

		Territory benefactor = this.getLeastVulnerableTerritoryIn(AIUtilities
				.getFriendlyTerritoriesAdjacentTo(beneficiary));

		if (benefactor == null) {
			return this.decideFortifyUsingCentrifugalSpread();
		}

		int toMove = ((int) AIUtilities.calcuateWeightDistributionRatio(
				beneficiary, benefactor, EvaluationStrategy.DEFENSE) * beneficiary
				.getTroopsOnTerritory());
		toMove = this.processFortifyContribution(0, benefactor
				.getTroopsOnTerritory() - 1, toMove);

		if (toMove == 0) {
			return this.decideFortifyUsingCentrifugalSpread();
		}

		return new Fortify(benefactor, beneficiary, toMove);

	}

	private Fortify decideFortify_Manifest_Destiny() {
		Collection<Territory> possibleTargets = null;
		Collection<Territory> possibleBeneficiaries = null;

		TreeMap<Integer, Continent> strongest = this
				.determineStrongestContinentsNotCaptured();

		Continent strongestContinent = (this
				.getContinentsInDescendingOrder(strongest)).get(0);

		Attack result = null;

		possibleTargets = this.getEnemyTargetsManifestDestiny();
		possibleBeneficiaries = this
				.getFriendlyTerritoriesAdjacentTo(possibleTargets);

		Territory beneficiary = this
				.getMostVulnerableTerritoryIn(possibleBeneficiaries);

		if (beneficiary == null) {

			return this.decideFortify_Defense();
		}

		Territory benefactor = this.getLeastVulnerableTerritoryIn(AIUtilities
				.getFriendlyTerritoriesAdjacentTo(beneficiary));

		if (benefactor == null) {
			return this.decideFortify_Defense();
		}

		int toMove = ((int) AIUtilities.calcuateWeightDistributionRatio(
				beneficiary, benefactor, EvaluationStrategy.DEFENSE) * benefactor
				.getTroopsOnTerritory());

		toMove = this.processFortifyContribution(0, benefactor
				.getTroopsOnTerritory() - 1, toMove);

		System.out.println(beneficiary.getName() + " " + benefactor.getName()
				+ " " + toMove);

		if (toMove == 0) {
			return this.decideFortify_Defense();
		}

		return new Fortify(benefactor, beneficiary, toMove);
	}

	private Fortify decideFortify_Belligerent() {
		Collection<Territory> possibleTargets = null;
		Collection<Territory> possibleBeneficiaries = null;

		TreeMap<Integer, Continent> strongest = this
				.determineStrongestContinents();

		Continent strongestContinent = (this
				.getContinentsInDescendingOrder(strongest)).get(0);

		Attack result = null;

		possibleTargets = this.getEnemyTargetsBelligerent();
		possibleBeneficiaries = this
				.getFriendlyTerritoriesAdjacentTo(possibleTargets);

		Territory beneficiary = this
				.getMostVulnerableTerritoryIn(possibleBeneficiaries);

		if (beneficiary == null) {

			return this.decideFortify_Defense();
		}

		Territory benefactor = this.getLeastVulnerableTerritoryIn(AIUtilities
				.getFriendlyTerritoriesAdjacentTo(beneficiary));

		if (benefactor == null) {
			return this.decideFortify_Defense();
		}

		int toMove = ((int) AIUtilities.calcuateWeightDistributionRatio(
				beneficiary, benefactor, EvaluationStrategy.DEFENSE) * benefactor
				.getTroopsOnTerritory());

		toMove = this.processFortifyContribution(0, benefactor
				.getTroopsOnTerritory() - 1, toMove);

		if (toMove == 0) {
			return this.decideFortify_Defense();
		}

		return new Fortify(benefactor, beneficiary, toMove);
	}

	/**
	 * AI decides if it wants to fortify after an attack.
	 * 
	 * @return the number to fortify after an attack.
	 */
	@Override
	public int decideFortifyAfterAttack(Territory attackingTerritory,
			Territory defendingTerritory) {

		double ratio = AIUtilities.calcuateWeightDistributionRatio(
				defendingTerritory, attackingTerritory,
				EvaluationStrategy.DEFENSE);

		int minTroops = determineMinTroopsToFortify(attackingTerritory
				.getTroopsOnTerritory());

		int toGive;
		toGive = (int) (attackingTerritory.getTroopsOnTerritory() * ratio);

		toGive = this.processFortifyContribution(minTroops, attackingTerritory
				.getTroopsOnTerritory() - 1, toGive);

		return toGive;
	}

	private int processFortifyContribution(int minTroops, int maxTroops,
			int wantingToGive) {

		if (wantingToGive < minTroops) {
			wantingToGive = minTroops;
		} else if (wantingToGive > maxTroops) {
			wantingToGive = maxTroops;
		}

		return wantingToGive;
	}

	private Fortify decideFortifyUsingCentrifugalSpread() {

		TreeMap<Integer, Continent> strongest = determineStrongestContinents();
		Integer i = null;

		while (strongest.size() > 0) {
			i = Collections.max(strongest.keySet());
			Continent strongestC = strongest.get(Collections.max(strongest
					.keySet()));

			Collection<Territory> mine = AIUtilities
					.getFriendlyTerritoriesInContinent(strongestC, this);
			List<Territory> descending = this
					.getTerritoriesInDescendingOrder(AIUtilities
							.getRankedWeightMap(mine,
									EvaluationStrategy.FORTIFICATION));

			for (Territory next : descending) {
				Territory leastVulnerable = this
						.getLeastVulnerableTerritoryIn(AIUtilities
								.getFriendlyTerritoriesAdjacentTo(next));
				if (leastVulnerable != null) {
					double ratio = AIUtilities.calcuateWeightDistributionRatio(
							leastVulnerable, next,
							EvaluationStrategy.FORTIFICATION);

					int toGive = 0;
					if (leastVulnerable.getTroopsOnTerritory() > 1) {
						toGive = (int) (leastVulnerable.getTroopsOnTerritory() * ratio);

						toGive = this.processFortifyContribution(1,
								leastVulnerable.getTroopsOnTerritory() - 1,
								toGive);
					}
					if (toGive > 0) {
						return new Fortify(leastVulnerable, next, toGive);
					}
				}

			}
			strongest.remove(i);
		}

		return null;

	}

	private int determineMinTroopsToFortify(int attackSize) {

		if (attackSize >= 3) {
			return 2;
		} else if (attackSize == 2) {
			return 1;
		}

		return 0;
	}

	/**
	 * Returns the list of traded cards.
	 * 
	 * @return list of tradeable cards.
	 */
	@Override
	public List<Card> getTradeCards() {
		if (this.getCards().size() >= 5) {
			return AIUtilities.getPossibleCards(this.getCards());
		} else {
			return null;
		}
	}

	/**
	 * AI determines if it wants to trade.
	 * 
	 * @return true if it wants to trade, false otherwise.
	 */
	@Override
	public boolean wantToTrade() {
		return (getTradeCards() != null);
	}

	private TreeMap<Integer, Continent> determineStrongestContinents() {

		int max = 0;
		TreeMap<Integer, Continent> tree = new TreeMap();
		for (Continent c : map.getContinents()) {
			int sum = 0;

			int myTroops = 0;
			int possibleTroops = 0;

			for (Territory t : c.getTerritoryList()) {
				if (t.getPlayer() == this) {
					myTroops += t.getTroopsOnTerritory();
					sum++;
				}
				possibleTroops += t.getTroopsOnTerritory();

			}

			double ratio = ((double) sum) / c.getTerritoryList().size();
			ratio *= 100;

			double ratio2 = ((double) myTroops) / possibleTroops;
			ratio2 *= 100;

			tree.put((int) ratio + (int) ratio2, c);
		}

		return tree;
	}

	private TreeMap<Integer, Continent> determineStrongestContinentsNotCaptured() {

		TreeMap<Integer, Continent> exclusiveMap = new TreeMap();

		TreeMap<Integer, Continent> strongest = determineStrongestContinents();
		for (Integer i : strongest.keySet()) {
			Continent c = strongest.get(i);

			Collection<Territory> friendlyColl = AIUtilities
					.getFriendlyTerritoriesInContinent(c, this);

			if (friendlyColl.size() < c.getTerritoryList().size()) {
				exclusiveMap.put(i, c);
			}
		}
		return exclusiveMap;
	}

	private TreeMap<Integer, Territory> getTerritoriesByStrength(
			Collection<Territory> args) {
		TreeMap<Integer, Territory> tree = new TreeMap();
		for (Territory t : args) {
			tree.put(t.getTroopsOnTerritory(), t);
		}
		return tree;
	}

	/**
	 * 
	 * If belligerent: AI will hunt for weak territories outside of the
	 * strongest continent
	 * 
	 * If defensive: AI will hunt for strong territories bordering or inside the
	 * strongest continent
	 * 
	 * If manifest destiny: AI will look for remaining enemy territories inside
	 * the strongest continent
	 * 
	 * @return
	 */
	private Enum<EvaluationStrategy> decideEvaluationStrategy() {

		List<Integer> outcomeValues = new ArrayList();
		for (int i = 0; i < 3; i++) {
			outcomeValues.add(i);
		}
		List<Double> probabilities = new ArrayList();
		probabilities.add(ExpertAIStrategy.probabilityManifestDestiny);
		probabilities.add(ExpertAIStrategy.probabilityBelligerent);
		probabilities.add(ExpertAIStrategy.probabilityDefensive);

		List<Integer> possibleResults = ExpertAIStrategy.determineOutcomes(
				probabilities, outcomeValues);
		int randomResult = possibleResults.get((int) DiceRoll
				.generateRandomNumber(0, possibleResults.size() - 1));

		switch (randomResult) {
		case 0:
			return EvaluationStrategy.MANIFEST_DESTINY;
		case 1:
			return EvaluationStrategy.BELLIGERENT;
		case 2:
			return EvaluationStrategy.DEFENSE;
		}

		return EvaluationStrategy.BELLIGERENT;

	}

	/**
	 * 
	 * 
	 * 
	 * PostCondition: returns a collection of enemy targets of size>0
	 * 
	 */

	private Collection<Territory> getEnemyTargetsDefensive() {
		Collection<Territory> possibilities = new HashSet();

		possibilities.addAll(getOuterEnemyTargets());
		possibilities.addAll(getInnerEnemyTargets());

		TreeMap<Integer, Territory> weightMap = AIUtilities.getRankedWeightMap(
				possibilities, EvaluationStrategy.BELLIGERENT);
		// Belligerent signifies the most heavily fortified enemies

		return this.getTerritoriesInDescendingOrder(weightMap);

	}

	/**
	 * Note this method is considered to be a subset of
	 * getEnemyTargetsDefensive()
	 */
	private Collection<Territory> getEnemyTargetsManifestDestiny() {
		Collection<Territory> possibilities = new HashSet();
		possibilities.addAll(getInnerEnemyTargets());
		TreeMap<Integer, Territory> weightMap = AIUtilities.getRankedWeightMap(
				possibilities, EvaluationStrategy.BELLIGERENT);
		return this.getTerritoriesInDescendingOrder(weightMap);
	}

	private Collection<Territory> getEnemyTargetsBelligerent() {
		Collection<Territory> possibilities = new HashSet();
		possibilities.addAll(this.getOuterEnemyTargets());
		possibilities.addAll(this.getInnerEnemyTargets());
		TreeMap<Integer, Territory> weightMap = AIUtilities.getRankedWeightMap(
				possibilities, EvaluationStrategy.BELLIGERENT);
		return this.getTerritoriesInDescendingOrder(weightMap);
	}

	private Collection<Territory> getOuterEnemyTargets() {
		TreeMap<Integer, Continent> strongestContinents = this
				.determineStrongestContinents();

		Collection<Territory> possibilities = new HashSet();

		List<Continent> mostImportant = getContinentsInDescendingOrder(strongestContinents);

		final int depth = 3;
		int curDepth = 0;
		for (Continent c : mostImportant) {
			curDepth++;
			Collection<Territory> border = AIUtilities.getBordersOf(c);

			for (Territory t : border) {
				if (t.getPlayer() != this) {
					possibilities.add(t);
				}
			}

			if (possibilities.size() > 0) {
				return possibilities;
			}

		}
		return possibilities;
	}

	private Collection<Territory> getInnerEnemyTargets() {
		TreeMap<Integer, Continent> strongestContinents = this
				.determineStrongestContinentsNotCaptured();
		Collection<Territory> possibilities = new HashSet();

		List<Continent> mostImportantFirst = getContinentsInDescendingOrder(strongestContinents);

		for (Continent c : mostImportantFirst) {

			Collection<Territory> innards = AIUtilities.getInnardsOf(c);

			for (Territory t : innards) {
				if (t.getPlayer() != this) {
					possibilities.add(t);
				}
			}
			if (possibilities.size() > 0) {
				return possibilities;
			}

		}

		return possibilities;

	}

	private List<Integer> reverseKeyset(TreeMap<Integer, ?> k) {
		List<Integer> keys = new ArrayList(k.size());
		keys.addAll(k.keySet());
		Collections.reverse(keys);
		return keys;
	}

	private List<Continent> getContinentsInDescendingOrder(
			TreeMap<Integer, Continent> tree) {
		List<Continent> result = new ArrayList(tree.size());

		List<Integer> keysInReverse = reverseKeyset(tree);

		for (Integer i : keysInReverse) {
			result.add(tree.get(i));
		}

		return result;

	}

	private List<Territory> getTerritoriesInDescendingOrder(
			TreeMap<Integer, Territory> tree) {
		List<Territory> result = new ArrayList(tree.size());

		List<Integer> keysInReverse = reverseKeyset(tree);

		for (Integer i : keysInReverse) {
			result.add(tree.get(i));
		}

		return result;
	}

	private Collection<Territory> getFriendlyTerritoriesAdjacentTo(
			Collection<Territory> group) {

		Set<Territory> set = new HashSet();

		for (Territory of : group) {
			Collection<Territory> adjGroup = map.getAdjacentTerritories(of);

			for (Territory adj : adjGroup) {
				if (adj.getPlayer() == this) {
					set.add(adj);
				}
			}
		}
		return set;

	}

	private Territory getMostVulnerableTerritoryIn(Collection<Territory> group) {

		if (group.isEmpty()) {
			return null;
		}

		int min = -1;
		Territory result = null;

		List<Territory> possibleResults = new ArrayList();

		TreeMap<Integer, Territory> tree = AIUtilities.getRankedWeightMap(
				group, EvaluationStrategy.DEFENSE);

		return tree.get(Collections.max(tree.keySet()));

		/*
		 * for (Territory t : group) { int troops = t.getTroopsOnTerritory(); if
		 * (troops <= min || min < 0) { possibleResults.add(t); } } return
		 * possibleResults.get((int) DiceRoll.generateRandomNumber(0,
		 * possibleResults.size() - 1));
		 */
	}

	private Territory getLeastVulnerableTerritoryIn(Collection<Territory> group) {
		TreeMap<Integer, Territory> tree = AIUtilities.getRankedWeightMap(
				group, EvaluationStrategy.BELLIGERENT);

		if (group.size() == 0) {
			return null;
		}

		return tree.get(Collections.max(tree.keySet()));
	}

	/**
	 * Returns the pregame Territory it wishes to obtain.
	 * 
	 * @return the territory in the pregame stage the AI wishes to obtain.
	 */
	public Territory getPregameTerritory() {

		int i = 1;
		Territory t = getPregameTerritory(i);
		while (t.getPlayer() != null) {
			t = getPregameTerritory(i + 1);
		}

		return t;

	}

	private Territory getPregameTerritory(int rank) {
		AIUtilities.setMap(map);
		Territory pregame = AIUtilities.highestWeightPregame(this);
		return pregame;
	}

	private Collection<Territory> getWithoutTerritoriesNotContainedIn(
			Collection<Territory> args, Continent c) {

		Set<Territory> set = new HashSet();

		for (Territory t : args) {
			if (t.getContinentIn() == c) {
				set.add(t);
			}
		}
		return set;
	}

	private static List<Integer> determineOutcomes(List<Double> probabilities,
			List<Integer> outcomeValues) {
		List<Integer> outcomeDistribution = new ArrayList(100);

		int prevValue = 0;
		for (int i = 0; i < probabilities.size(); i++) {
			double p = probabilities.get(i);

			int amountToFill = (int) (p * 100);
			int outcome = outcomeValues.get(i);
			for (int j = prevValue; j < amountToFill && j < 100; j++) {
				outcomeDistribution.add(outcome);
			}

		}
		return outcomeDistribution;
	}

	public static void main(String[] args) {
		List<Integer> outcomeValues = new ArrayList();
		for (int i = 0; i < 3; i++) {
			outcomeValues.add(i);
		}
		List<Double> probabilities = new ArrayList();
		probabilities.add(.50);
		probabilities.add(.25);
		probabilities.add(.25);
		System.out.println(determineOutcomes(probabilities, outcomeValues));

	}

	private Collection<Territory> getFriendlyTerritoriesAdjacentTo(Territory t) {
		Collection<Territory> coll = new HashSet<Territory>();
		for (Territory next : map.getAdjacentTerritories(t)) {
			if (next.getPlayer() == this) {
				coll.add(next);
			}
		}
		return coll;
	}

	private Attack getLastResortAttack() {
		Collection<Territory> enemy = AIUtilities
				.getAllAdjacentEnemyTerritoriesFor(this);

		double rand = Math.random();
		Attack result = null;
		if (rand > .5) {
			result = this.getLastResortBelligerent(enemy);
			if (result != null) {
				return result;
			} else {
				return this.getLastResortDefensive(enemy);
			}
		} else {
			result = this.getLastResortDefensive(enemy);
			return result;
		}
	}

	private Attack getLastResortBelligerent(Collection<Territory> enemy) {
		TreeMap<Integer, Territory> mostThreatening = AIUtilities
				.getRankedWeightMap(enemy, EvaluationStrategy.BELLIGERENT);
		for (Integer i : mostThreatening.keySet()) {
			Territory threatening = mostThreatening.get(i);
			Collection<Territory> mine = this
					.getFriendlyTerritoriesAdjacentTo(threatening);
			int max = 0;
			Territory maxT = null;
			for (Territory t : mine) {
				if (t.getTroopsOnTerritory() > max) {
					max = t.getTroopsOnTerritory();
					maxT = t;
				}

			}
			if (maxT != null) {
				Attack a = new Attack(maxT, threatening);
				if (this.isOptimalAttack(a)) {
					return a;
				}
			}
		}

		return null;

	}

	private Attack getLastResortDefensive(Collection<Territory> enemy) {
		TreeMap<Integer, Territory> mostThreatening = AIUtilities
				.getRankedWeightMap(enemy, EvaluationStrategy.DEFENSE);
		for (Integer i : mostThreatening.keySet()) {
			Territory threatening = mostThreatening.get(i);
			Collection<Territory> mine = this
					.getFriendlyTerritoriesAdjacentTo(threatening);
			int max = 0;
			Territory maxT = null;
			for (Territory t : mine) {
				if (t.getTroopsOnTerritory() > max) {
					max = t.getTroopsOnTerritory();
					maxT = t;
				}

			}
			if (maxT != null) {
				Attack a = new Attack(maxT, threatening);
				if (this.isOptimalAttack(a)) {
					return a;
				}
			}
		}

		return null;

	}

}
