package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.LineBorder;

import base.Card;
import base.CardType;
import base.TerritoryType;

/**
 * JCardButton Class
 * 
 * This will represent one card on the card panel that the human player owns.
 * 
 * @author Victor
 */
public class JCardButton extends JButton implements ActionListener{

	private Card card;
	private CardPanel panel;
	private boolean beenSelected;
	private ImageIcon icon;
	
	/**
	 * Makes the button to represent the given card and picks the correct picture.
	 * @param card - to represent
	 * @param panel - the card panel
	 */
	public JCardButton(Card card, CardPanel panel) {
		this.card = card;
		this.panel = panel;
		beenSelected = false;
		if (card != null && card.getCardType() != null && card.getTerritoryType() != null)
			this.setToolTipText(card.getCardType().toString() + " : " + card.getTerritoryType().toString());
		
		this.setNormalIcon();
		addActionListener(this); 
		this.setPreferredSize(new Dimension(91, 125));
	}
	
	private void setNormalIcon() {
		if (card == null) {
			icon = new ImageIcon("card_pics/none.png");
			this.setEnabled(false);
		}
		else if (card.getCardType() == CardType.FLIPPED) {
			icon = new ImageIcon("card_pics/back.png");
		}
		else if (card.getCardType() == CardType.SOLDIER) {
			if (card.getTerritoryType() == TerritoryType.AFGHANISTAN)
				icon = new ImageIcon("card_pics/soldier_cards/afghanistan_soldier.png");
			else if (card.getTerritoryType() == TerritoryType.ALASKA)
				icon = new ImageIcon("card_pics/soldier_cards/alaska_soldier.png");
			else if (card.getTerritoryType() == TerritoryType.ALBERTA)
				icon = new ImageIcon("card_pics/soldier_cards/alberta_soldier.png");
			else if (card.getTerritoryType() == TerritoryType.ARGENTINA)
				icon = new ImageIcon("card_pics/soldier_cards/argentina_soldier.png");
			else if (card.getTerritoryType() == TerritoryType.BRAZIL)
				icon = new ImageIcon("card_pics/soldier_cards/brazil_soldier.png");
			else if (card.getTerritoryType() == TerritoryType.CENTRAL_AFRICA)
				icon = new ImageIcon("card_pics/soldier_cards/central_africa_soldier.png");
			else if (card.getTerritoryType() == TerritoryType.CENTRAL_AMERICA)
				icon = new ImageIcon("card_pics/soldier_cards/central_america_soldier.png");
			else if (card.getTerritoryType() == TerritoryType.CHINA)
				icon = new ImageIcon("card_pics/soldier_cards/china_soldier.png");
			else if (card.getTerritoryType() == TerritoryType.EAST_AFRICA)
				icon = new ImageIcon("card_pics/soldier_cards/east_africa_soldier.png");
			else if (card.getTerritoryType() == TerritoryType.EASTERN_AUSTRALIA)
				icon = new ImageIcon("card_pics/soldier_cards/eastern_australia_soldier.png");
			else if (card.getTerritoryType() == TerritoryType.EASTERN_UNITED_STATES)
				icon = new ImageIcon("card_pics/soldier_cards/eastern_united_states_soldier.png");
			else if (card.getTerritoryType() == TerritoryType.EGYPT)
				icon = new ImageIcon("card_pics/soldier_cards/egypt_soldier.png");
			else if (card.getTerritoryType() == TerritoryType.GREAT_BRITAIN)
				icon = new ImageIcon("card_pics/soldier_cards/great_britain_soldier.png");
			else
				icon = new ImageIcon("card_pics/soldier_cards/greenland_soldier.png");
		}
		else if (card.getCardType() == CardType.CAVALIER) {
			if (card.getTerritoryType() == TerritoryType.PERU)
				icon = new ImageIcon("card_pics/cavalier_cards/peru_cavalier.png");
			else if (card.getTerritoryType() == TerritoryType.QUEBEC)
				icon = new ImageIcon("card_pics/cavalier_cards/quebec_cavalier.png");
			else if (card.getTerritoryType() == TerritoryType.SCANDINAVIA)
				icon = new ImageIcon("card_pics/cavalier_cards/scandinavia_cavalier.png");
			else if (card.getTerritoryType() == TerritoryType.SIAM)
				icon = new ImageIcon("card_pics/cavalier_cards/siam_cavalier.png");
			else if (card.getTerritoryType() == TerritoryType.SIBERIA)
				icon = new ImageIcon("card_pics/cavalier_cards/siberia_cavalier.png");
			else if (card.getTerritoryType() == TerritoryType.SOUTH_AFRICA)
				icon = new ImageIcon("card_pics/cavalier_cards/south_africa_cavalier.png");
			else if (card.getTerritoryType() == TerritoryType.SOUTHERN_EUROPE)
				icon = new ImageIcon("card_pics/cavalier_cards/southern_europe_cavalier.png");
			else if (card.getTerritoryType() == TerritoryType.UKRAINE)
				icon = new ImageIcon("card_pics/cavalier_cards/ukraine_cavalier.png");
			else if (card.getTerritoryType() == TerritoryType.URAL)
				icon = new ImageIcon("card_pics/cavalier_cards/ural_cavalier.png");
			else if (card.getTerritoryType() == TerritoryType.VENEZUELA)
				icon = new ImageIcon("card_pics/cavalier_cards/venezuela_cavalier.png");
			else if (card.getTerritoryType() == TerritoryType.WESTERN_AUSTRALIA)
				icon = new ImageIcon("card_pics/cavalier_cards/western_australia_cavalier.png");
			else if (card.getTerritoryType() == TerritoryType.WESTERN_EUROPE)
				icon = new ImageIcon("card_pics/cavalier_cards/western_europe_cavalier.png");
			else if (card.getTerritoryType() == TerritoryType.WESTERN_UNITED_STATES)
				icon = new ImageIcon("card_pics/cavalier_cards/western_united_states_cavalier.png");
			else
				icon = new ImageIcon("card_pics/cavalier_cards/yakutsk_cavalier.png");
		}
		else if (card.getCardType() == CardType.CANNON) {
			if (card.getTerritoryType() == TerritoryType.ICELAND)
				icon = new ImageIcon("card_pics/cannon_cards/iceland_cannon.png");
			else if (card.getTerritoryType() == TerritoryType.INDIA)
				icon = new ImageIcon("card_pics/cannon_cards/india_cannon.png");
			else if (card.getTerritoryType() == TerritoryType.INDONESIA)
				icon = new ImageIcon("card_pics/cannon_cards/indonenia_cannon.png");
			else if (card.getTerritoryType() == TerritoryType.IRKUTSK)
				icon = new ImageIcon("card_pics/cannon_cards/irkutsk_cannon.png");
			else if (card.getTerritoryType() == TerritoryType.JAPAN)
				icon = new ImageIcon("card_pics/cannon_cards/japan_cannon.png");
			else if (card.getTerritoryType() == TerritoryType.KAMCHATKA)
				icon = new ImageIcon("card_pics/cannon_cards/kamchatka_cannon.png");
			else if (card.getTerritoryType() == TerritoryType.MADAGASCAR)
				icon = new ImageIcon("card_pics/cannon_cards/madagascar_cannon.png");
			else if (card.getTerritoryType() == TerritoryType.MIDDLE_EAST)
				icon = new ImageIcon("card_pics/cannon_cards/middle_east_cannon.png");
			else if (card.getTerritoryType() == TerritoryType.MONGOLIA)
				icon = new ImageIcon("card_pics/cannon_cards/mongolia_cannon.png");
			else if (card.getTerritoryType() == TerritoryType.NEW_GUINEA)
				icon = new ImageIcon("card_pics/cannon_cards/new_guinea_cannon.png");
			else if (card.getTerritoryType() == TerritoryType.NORTH_AFRICA)
				icon = new ImageIcon("card_pics/cannon_cards/north_africa_cannon.png");
			else if (card.getTerritoryType() == TerritoryType.NORTHERN_EUROPE)
				icon = new ImageIcon("card_pics/cannon_cards/northern_europe_cannon.png");
			else if (card.getTerritoryType() == TerritoryType.NORTHWEST_TERRITORY)
				icon = new ImageIcon("card_pics/cannon_cards/northwest_territory_cannon.png");
			else 
				icon = new ImageIcon("card_pics/cannon_cards/ontario_cannon.png");
		}
		else {
			icon = new ImageIcon("card_pics/wild.png");
		}
		
		this.setIcon(icon);
	}
	
	/**
	 * Returns the card.
	 * 
	 * @return the card.
	 */
	public Card getCard() {
		return card;
	}
	
	/**
	 * Returns whether the button has been selected.
	 * 
	 * @return whether the button has been selected
	 */
	public boolean beenSelected() {
		return beenSelected;
	}
	
	/**
	 * Resets the been selected variable to false.
	 */
	public void resetSelected() {
		beenSelected = false;
		this.setText(null);
	}

	public void actionPerformed(ActionEvent arg0) {
		if (beenSelected) {
			beenSelected = false;
			this.setText(null);
			this.setBorder(null);
		}
		else {
			beenSelected = true;
				
			this.setBorder(new LineBorder(Color.black, 2));
			this.setVerticalTextPosition(AbstractButton.BOTTOM);
		    this.setHorizontalTextPosition(AbstractButton.CENTER);

		}
	}
	
}
