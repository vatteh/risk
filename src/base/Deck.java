package base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Deck Class Will represent a deck of cards in the game.
 * 
 * @author Victor
 */
public class Deck implements Serializable {

	private static final long serialVersionUID = 1540145732142233539L;
	private List<Card> deck;

	/**
	 * Creates a new Deck with 44 cards 14 of each type plus 2 wild cards.
	 */
	public Deck() {
		this.deck = new ArrayList<Card>(44);

		this.deck.add(new Card(CardType.SOLDIER, TerritoryType.AFGHANISTAN));
		this.deck.add(new Card(CardType.SOLDIER, TerritoryType.ALASKA));
		this.deck.add(new Card(CardType.SOLDIER, TerritoryType.ALBERTA));
		this.deck.add(new Card(CardType.SOLDIER, TerritoryType.ARGENTINA));
		this.deck.add(new Card(CardType.SOLDIER, TerritoryType.BRAZIL));
		this.deck.add(new Card(CardType.SOLDIER, TerritoryType.CENTRAL_AFRICA));
		this.deck
				.add(new Card(CardType.SOLDIER, TerritoryType.CENTRAL_AMERICA));
		this.deck.add(new Card(CardType.SOLDIER, TerritoryType.CHINA));
		this.deck.add(new Card(CardType.SOLDIER, TerritoryType.EAST_AFRICA));
		this.deck.add(new Card(CardType.SOLDIER,
				TerritoryType.EASTERN_AUSTRALIA));
		this.deck.add(new Card(CardType.SOLDIER,
				TerritoryType.EASTERN_UNITED_STATES));
		this.deck.add(new Card(CardType.SOLDIER, TerritoryType.EGYPT));
		this.deck.add(new Card(CardType.SOLDIER, TerritoryType.GREAT_BRITAIN));
		this.deck.add(new Card(CardType.SOLDIER, TerritoryType.GREENLAND));

		this.deck.add(new Card(CardType.CAVALIER, TerritoryType.PERU));
		this.deck.add(new Card(CardType.CAVALIER, TerritoryType.QUEBEC));
		this.deck.add(new Card(CardType.CAVALIER, TerritoryType.SCANDINAVIA));
		this.deck.add(new Card(CardType.CAVALIER, TerritoryType.SIAM));
		this.deck.add(new Card(CardType.CAVALIER, TerritoryType.SIBERIA));
		this.deck.add(new Card(CardType.CAVALIER, TerritoryType.SOUTH_AFRICA));
		this.deck
				.add(new Card(CardType.CAVALIER, TerritoryType.SOUTHERN_EUROPE));
		this.deck.add(new Card(CardType.CAVALIER, TerritoryType.UKRAINE));
		this.deck.add(new Card(CardType.CAVALIER, TerritoryType.URAL));
		this.deck.add(new Card(CardType.CAVALIER, TerritoryType.VENEZUELA));
		this.deck.add(new Card(CardType.CAVALIER,
				TerritoryType.WESTERN_AUSTRALIA));
		this.deck
				.add(new Card(CardType.CAVALIER, TerritoryType.WESTERN_EUROPE));
		this.deck.add(new Card(CardType.CAVALIER,
				TerritoryType.WESTERN_UNITED_STATES));
		this.deck.add(new Card(CardType.CAVALIER, TerritoryType.YAKUTSK));

		this.deck.add(new Card(CardType.CANNON, TerritoryType.ICELAND));
		this.deck.add(new Card(CardType.CANNON, TerritoryType.INDIA));
		this.deck.add(new Card(CardType.CANNON, TerritoryType.INDONESIA));
		this.deck.add(new Card(CardType.CANNON, TerritoryType.IRKUTSK));
		this.deck.add(new Card(CardType.CANNON, TerritoryType.JAPAN));
		this.deck.add(new Card(CardType.CANNON, TerritoryType.KAMCHATKA));
		this.deck.add(new Card(CardType.CANNON, TerritoryType.MADAGASCAR));
		this.deck.add(new Card(CardType.CANNON, TerritoryType.MIDDLE_EAST));
		this.deck.add(new Card(CardType.CANNON, TerritoryType.MONGOLIA));
		this.deck.add(new Card(CardType.CANNON, TerritoryType.NEW_GUINEA));
		this.deck.add(new Card(CardType.CANNON, TerritoryType.NORTH_AFRICA));
		this.deck.add(new Card(CardType.CANNON, TerritoryType.NORTHERN_EUROPE));
		this.deck.add(new Card(CardType.CANNON,
				TerritoryType.NORTHWEST_TERRITORY));
		this.deck.add(new Card(CardType.CANNON, TerritoryType.ONTARIO));

		this.deck.add(new Card(CardType.WILD, TerritoryType.NONE));
		this.deck.add(new Card(CardType.WILD, TerritoryType.NONE));

		Collections.shuffle((List<Card>) this.deck);
	}

	/**
	 * Will Return the card in index 0 and then remove the card from the list.
	 * If the list is already empty, a new deck will be created and the first
	 * will be removed from that one.
	 * 
	 * @return the next card in the list
	 */
	public Card getNextCard() {

		if (!deck.isEmpty()) {
			return deck.remove(0);
		} else {
			System.out.println("For some Reason we ran out of cards!");
			return null;
		}
	}

	/**
	 * After a player has successfully traded a set of cards, they need to be
	 * placed back to the deck. This method will do that in the main loop.
	 * 
	 * @param card1
	 *            - the first card to be placed back to the list
	 * @param card2
	 *            - the second card to be placed back to the list
	 * @param card3
	 *            - the third card to be placed back to the list.
	 * @return - whether the cards were placed back successfully.
	 */
	public boolean addTradedCards(Card card1, Card card2, Card card3) {
		if (card1 == null || card2 == null || card3 == null) {
			return false;
		}

		deck.add(card1);
		deck.add(card2);
		deck.add(card3);
		return true;
	}

	/**
	 * Returns all the cards in the deck
	 * 
	 * @return - the deck list
	 */
	public List<Card> getAllCards() {
		return deck;
	}

	/**
	 * Shuffles the deck using Collections.shuffle()
	 */
	public void shuffle() {
		Collections.shuffle(deck);
	}
}
