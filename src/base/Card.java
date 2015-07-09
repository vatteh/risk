package base;

import java.io.Serializable;

/**
 * Card Class
 * 
 * Will represent a card in the deck with a certain type.
 * 
 * @author Victor
 * 
 */

public class Card implements Serializable, Comparable {

	private static final long serialVersionUID = -6680781501272944082L;
	private CardType cardType;
	private TerritoryType territoryType;

	/**
	 * Constructs the card with the given card type.
	 * 
	 * @param cardType
	 *            - the type of the card
	 * @param territoryType
	 *            - the type of the territory
	 */
	public Card(CardType cardType, TerritoryType territoryType) {
		this.cardType = cardType;
		this.territoryType = territoryType;
	}

	/**
	 * Returns the type of the card.
	 * 
	 * @return - the type of the card
	 */
	public CardType getCardType() {
		return cardType;
	}

	/**
	 * 
	 * @return - the type of the territory
	 */
	public TerritoryType getTerritoryType() {
		return territoryType;
	}

	/**
	 * Compares a card to another card.
	 * 
	 * @return 0 if the cards are the same, 1 if they are different
	 */
	public int compareTo(Object arg0) {
		if (cardType == ((Card) arg0).getCardType()) {
			return 0;
		} else {
			return 1;
		}
	}

}
