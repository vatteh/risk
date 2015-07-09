package player;

import java.awt.Color;
import java.io.Serializable;
import java.util.List;

import view.DiceRoll;

import base.Card;
import base.Game;

import map.Map;
import map.Territory;

/**
 * 
 * An AI that uses bad strategy
 * 
 * @author Phil
 * 
 */
public class SimpleAIStrategy extends Player implements Strategy, Serializable{

	private static final long serialVersionUID = -7537713282785709743L;
	private static final int ATTACKS_PER_MOVE = 3;
	private static final int FORTIFIES_PER_MOVE = 3;
	private int attacksMade = 0;
	private int fortifiesMade = 0;

	/**
	 * Constructor for the simple AI strategy.
	 * 
	 * @param name
	 * 			name of the simple AI.
	 * @param color
	 * 			color of the AI.
	 * @param map
	 * 			the game board.
	 */
	public SimpleAIStrategy(String name, Color color, Map map) {
		super(name, color, map);
		AIUtilities.setMap(map);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Decides if the strategy wants to attack.
	 * 
	 * @return the attacking object.
	 */
	public Attack decideAttack() {

		fortifiesMade = 0;
		
		if (!AIUtilities.canAttack(this)) {
			return null;
		}

		if (attacksMade >= ATTACKS_PER_MOVE) {
			return null;
		}

		Territory defending = getRandomAdjacentEnemyTerritory();

		Territory attacking = AIUtilities.getAnyFriendlyTerritoryAdjacentTo(
				defending, this,2);
		
		if(attacking==null){
			return null;
		}
		
		Attack theAttack = new Attack(attacking, defending);

		attacksMade++;
		return theAttack;
	}

	/**
	 * Decides if the AI wants to fortify and where.
	 * 
	 * @return the fortify object.
	 */
	public Fortify decideFortify() {

		attacksMade = 0;

		if (!AIUtilities.canFortify(this)) {
			return null;
		}

		if (fortifiesMade >= FORTIFIES_PER_MOVE) {
			return null;
		}

		Territory toMobilize = pickRandomTerritoryFrom(AIUtilities
				.getAllTerritoriesWithMoreThanSpecifiedFor(this, 1));
		Territory randomFriendlyTerritory = pickARandomFriendlyTerritory();

		if (getTerritoriesOwned().size() <= 1) {
			return null;
		}

		while (randomFriendlyTerritory != toMobilize) {
			randomFriendlyTerritory = pickARandomFriendlyTerritory();
		}

		fortifiesMade++;

		return new Fortify(toMobilize, randomFriendlyTerritory, 1);

	}

	/**
	 * Decides if the AI wants to reinforce.
	 * 
	 * @return the reinforcement object.
	 */
	@Override
	public Reinforcement decideReinforcement() {
		Reinforcement r = new Reinforcement(pickARandomFriendlyTerritory(), 1);
		return r;
	}

	private Game getGame() {
		// TODO Auto-generated method stub
		return null;
	}

	private Territory getRandomAdjacentEnemyTerritory() {
		List<Territory> adjacentEnemyTerritories = AIUtilities
				.getAllAdjacentEnemyTerritoriesFor(this);
		return pickRandomTerritoryFrom(adjacentEnemyTerritories);
	}

	private static Territory pickRandomTerritoryFrom(List<Territory> list) {
		if (list.isEmpty())
			return null;
		int randomIndex = (int) DiceRoll.generateRandomNumber(0,
				list.size() - 1);
		return list.get(randomIndex);
	}

	private Territory pickARandomFriendlyTerritory() {
		return pickRandomTerritoryFrom(this.getTerritoriesOwned());
	}

	private Territory getAnyAdjacentFriendlyTerritory(Territory enemy) {
		List<Territory> l = AIUtilities
				.getAllTerritoriesWithMoreThanSpecifiedFor(this, -1);
		return l.get((int) DiceRoll.generateRandomNumber(0, l.size() - 1));

	}

	/**
	 * Returns the list of tradeable cards in the AI's possesion.
	 * 
	 * @return the list of cards to trade.
	 */
	@Override
	public List<Card> getTradeCards() {
		if (this.getCards().size() >= 3) {
			return AIUtilities.getPossibleCards(this.getCards());
		}
		else {
			return null;
		}
	}

	/**
	 * Decides if it wants to trade cards or not.
	 * 
	 * @return true if it wants to trade, false otherwise.
	 */
	@Override
	public boolean wantToTrade() {
		return getTradeCards()!=null;
	}

	/**
	 * Decides if it wants to fortify after an attack.
	 * 
	 * @return the number to fortify to attack.
	 */
	@Override
	public int decideFortifyAfterAttack(Territory attackingTerritory,
			Territory defendingTerritory) {
		// TODO
		return 1;
	}

	/**
	 * Returns the pre-game territory the AI wishes to obtain.
	 * 
	 * @return the Territory it the AI wants to get in the pre-game stage.
	 */
	@Override
	public Territory getPregameTerritory() {
		Territory t = random();
		while(t.getPlayer()!=null){
			t = random();
		}
		return t;
	}
	
	/**
	 * Returns a random territory.  The heart of the SimpleAIStrategy.
	 * 
	 * @return a random territory.
	 */
	private Territory random(){
		return map.getTerritories().get((int)DiceRoll.generateRandomNumber(0, map.getTerritories().size()-1));
	}

}
