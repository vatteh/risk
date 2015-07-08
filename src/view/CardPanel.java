/**
 * The panel located in the lower right corner of the Risk frame. This holds all the human's 
 * player's cards while holding the options for dice rolled and whether to turn on animation and sound.
 * 
 * @author Victor
 */

package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import player.Human;
import player.Player;
import player.Strategy;

import base.Card;
import base.CardType;
import base.Game;
import base.GameSettings;
import base.Stage;

public class CardPanel extends JPanel implements Observer, ActionListener {

	private JPanel panelOfCards;
	private JPanel panelOfSettings;
	private List<JCardButton> buttons;
	private Game game;
	private JButton turnInCards;
	private JButton skipStage;
	private JPanel statsPanel;

	/**
	 * Sets up the panel
	 * 
	 * @param game
	 *            - the game to look at
	 */
	public CardPanel(Game game) {
		this.buttons = new ArrayList<JCardButton>();
		this.game = game;

		game.addObserver(this);

		this.setLayout(new FlowLayout());

		panelOfCards = new JPanel();
		panelOfCards.setLayout(new FlowLayout());

		panelOfSettings = new JPanel();
		panelOfSettings.setLayout(new FlowLayout());
		panelOfSettings.setPreferredSize(new Dimension(400, 25));

		turnInCards = new JButton("Turn In Cards");
		turnInCards.setPreferredSize(new Dimension(120, 20));
		turnInCards.addActionListener(this);
		panelOfSettings.add(turnInCards);

		skipStage = new JButton("Skip Stage!");
		skipStage.setPreferredSize(new Dimension(120, 20));
		
		if (game.getAllPlayers().get(0) instanceof Human) {
			skipStage.addActionListener((ActionListener) game.getAllPlayers().get(0));
		}
		
		panelOfSettings.add(skipStage);

		panelOfCards.setBackground(Color.LIGHT_GRAY);
		panelOfSettings.setBackground(Color.LIGHT_GRAY);
		this.setBackground(Color.LIGHT_GRAY);

		statsPanel = new StatsPanel(game);

		JScrollPane scroll = new JScrollPane(panelOfCards);
		scroll.setPreferredSize(new Dimension(500, 150));
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scroll
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		this.add(scroll);
		this.add(panelOfSettings);
		this.add(statsPanel);
		this.setPreferredSize(new Dimension(500, 200));
		showCards(game.getCurrentPlayer().getCards());

	}

	/**
	 * Called when the current player's turn is the human player. This will show
	 * all of his/her cards, set the turn in cards button on and display their
	 * cards.
	 * 
	 * @param list
	 *            - the human's players list of cards
	 */
	private void showCards(List<Card> list) {

		panelOfCards.removeAll();
		buttons = new ArrayList<JCardButton>();
		turnInCards.setEnabled(true);

		for (int i = 0; i < list.size(); i++) {
			JCardButton playerButton = new JCardButton(list.get(i), this);
			panelOfCards.add(playerButton);
			buttons.add(playerButton);
		}

		for (int i = list.size(); i < 5; i++) {
			JCardButton playerButton = new JCardButton(null, this);
			panelOfCards.add(playerButton);
			buttons.add(playerButton);
		}

	}

	/**
	 * If the current player is not a human player, then remove all cards from
	 * the panel.
	 * 
	 * @param list
	 */
	private void hideCards(List<Card> list) {
		panelOfCards.removeAll();
		buttons = new ArrayList<JCardButton>();

		for (int i = 0; i < list.size(); i++) {
			JCardButton playerButton = new JCardButton(new Card(
					CardType.FLIPPED, null), this);
			panelOfCards.add(playerButton);
			buttons.add(playerButton);
		}

		for (int i = list.size(); i < 5; i++) {
			JCardButton playerButton = new JCardButton(null, this);
			panelOfCards.add(playerButton);
			buttons.add(playerButton);
		}

		turnInCards.setEnabled(false);
	}

	/**
	 * After the player has selected some cards and they were not a match, all
	 * cards need to be reset back to unselected to allow the player to select
	 * them again.
	 */
	private void setAllToUnselected() {
		for (JCardButton button : buttons) {
			button.resetSelected();
		}
	}

	/**
	 * Is called after the player has selected three cards and presses the
	 * "Turn In Cards" button. This will check if only three have been selected
	 * and if they are a match, this will remove them from the panel. Else it
	 * will set all to unselected.
	 */
	public void checkForThree() {
		List<JCardButton> selectedButtons = new ArrayList<JCardButton>();

		for (JCardButton button : buttons) {
			if (button.beenSelected()) {
				selectedButtons.add(button);
			}
		}

		if (selectedButtons.size() == 3) {
			if (game.tradeCards(selectedButtons.get(0).getCard(),selectedButtons.get(1).getCard(), selectedButtons.get(2).getCard())) {
				panelOfCards.remove(selectedButtons.get(0));
				panelOfCards.remove(selectedButtons.get(1));
				panelOfCards.remove(selectedButtons.get(2));
				showCards(game.getCurrentPlayer().getCards());
				revalidate();
			} else {
				setAllToUnselected();
			}
		}
	}

	/**
	 * Is called every time when its a new player's turn and will only show
	 * cards if the new player is a human player.
	 */
	public void update(Observable arg0, Object arg1) {
		if (game.getCurrentPlayer() != null) {
			if (game.getCurrentPlayer() instanceof Human && game.getCurrentStage() != Stage.PREGAME) {
				showCards(game.getCurrentPlayer().getCards());
				if ((game.getCurrentStage() != Stage.PREGAME) && (game.getCurrentStage() != Stage.REINFORCEMENTS)) {
					skipStage.setEnabled(true);
				}
			} else if (game.getCurrentPlayer() instanceof Strategy) {
				hideCards(game.getCurrentPlayer().getCards());
				skipStage.setEnabled(false);
			}
		}

		revalidate();
		statsPanel.repaint();
	}

	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == turnInCards) {
			checkForThree();
		}
	}

}
