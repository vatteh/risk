package player;

import java.util.List;

import base.Card;

/**
 * Allows the AI to trade cards and decide if it wants to trade.
 * 
 * @author Victor
 *
 */
public interface Strategy{

	/**
	 * Allows the AI to decide if it wants to trade cards or not.
	 * 
	 * @return
	 */
	public boolean wantToTrade();
	
	/**
	 * Returns the list of cards to trade.
	 * 
	 * @return valid list of tradeable cards.
	 */
	public List<Card> getTradeCards();
}
