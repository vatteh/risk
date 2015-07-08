/**
 * Makes the panel in the lower left corner of the the Risk frame. Will hold current stats and will 
 * hold the console to keep the human player up to date. Will also have the troop panel to allow the 
 * human player to visually see how many troops he has left.
 * 
 * @author Victor
 */

package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.ProgressBarUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

import map.Territory;

import player.Attack;
import player.Fortify;
import player.Human;
import player.Player;

import base.Game;
import base.Stage;

public class ConsolePanel extends JPanel implements Observer {

	private Game game;
	private JTextArea console;
	private JProgressBar progressBar;
	private JPanel playerPanel;
	private JScrollPane scrollPane;

	/**
	 * Constructor for the panel.
	 * 
	 * @param game
	 *            - the current game being played.
	 */
	public ConsolePanel(Game game) {
		this.game = game;
		game.addObserver(this);
		this.setLayout(new FlowLayout());

		// look at all human players for the console to prompt when to attack of
		// fortify
		for (Player player : game.getAllPlayers()) {
			if (player instanceof Human) {
				player.addObserver(this);
			}
		}

		progressBar = new JProgressBar();
		progressBar.setString("Troops left to Place");
		progressBar.setMinimum(0);
		progressBar.setValue(50);
		progressBar.setPreferredSize(new Dimension(320, 20));
		progressBar.setForeground(Color.green);
		// Any text added after this point will be painted on top of the bar
		progressBar.setStringPainted(true);
		progressBar.setString("" + game.getCurrentPlayer().getTroopsToGive());

		progressBar.setBackground(new Color(200,200,200));
		JPanel progressBarPanel = new JPanel();
		progressBarPanel.setLayout(new FlowLayout());
		progressBarPanel.add(new JLabel("Troops left to Place"));
		progressBarPanel.add(progressBar);
		progressBarPanel.setBackground(Color.LIGHT_GRAY);
		this.add(progressBarPanel);
		
		this.add(progressBarPanel, BorderLayout.NORTH);

		// console.setCaretPosition(console.getText().length());

		playerPanel = new PlayerPanel(game);
		this.add(playerPanel);

		console = new JTextArea("Welcome to the game of RISK! \n", 7, 40);
		console.setEditable(false);

		TitledBorder consoleTitle = BorderFactory
				.createTitledBorder(" Game Console ");
		consoleTitle.setTitleJustification(TitledBorder.CENTER);
		consoleTitle.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		//console.setBorder(consoleTitle);

		this.setBackground(Color.LIGHT_GRAY);
		scrollPane = new JScrollPane(console);
		scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.add(scrollPane, BorderLayout.SOUTH);
		this.setPreferredSize(new Dimension(500, 270));
		this.setMaximumSize(getPreferredSize());

	}

	public void update(Observable arg0, Object arg1) {
		if (arg1 == ConsoleUpdate.NEW_STAGE) {
			console.append("Current Stage: " + game.getCurrentStage().toString() + ".\n");
		} else if (arg1 == ConsoleUpdate.NEW_PLAYER && game.getCurrentStage() != Stage.PREGAME) {
			console.append("Player's Turn: " + game.getCurrentPlayer().getName() + ".\n");
		} else if (arg1 == ConsoleUpdate.GOOD_TRADE) {
			console.append(game.getCurrentPlayer().getName()
					+ " has successfully traded a matching set for "
					+ game.getTurnInValue() + " extra armies.\n");
		} else if (arg1 == ConsoleUpdate.BAD_TRADE) {
			console.append("Not a matching set! \n");
		} else if (arg1 == ConsoleUpdate.TERRITORY_BONUS) {
			console.append("Plus two added your territory for trading a card with a territory you own. \n");
		} else if (arg1 == ConsoleUpdate.TROOPS_THIS_TURN) {
			console.append(game.getCurrentPlayer().getName()
					+ " has been given "
					+ game.getCurrentPlayer().getTroopsToGive()
					+ " troops this turn.\n");
		} else if (arg1 == ConsoleUpdate.STATE_ATTACK) {
			console.append("Please Choose the attacking and defending Territory. \n");
		}  else if (arg1 == ConsoleUpdate.STATE_FORTIFY) {
			console.append("Please Choose the Fortifing territories. \n");
		}  else if (arg1 instanceof Territory) {
			if (game.getCurrentStage() == Stage.REINFORCEMENTS)
				console.append("Troop placed on " + ((Territory) arg1).getName() + "\n");
			else
				console.append(((Territory) arg1).getName() + "\n");
		} else if (arg1 instanceof Attack) {
			console.append(((Attack) arg1).getAttackingTerritory().getName()
							+ " is attacking "
							+ ((Attack) arg1).getDefendingTerritory().getName()
							+ ".\n");
		} else if (arg1 instanceof Fortify) {
			console.append(((Fortify) arg1).getAmount() + " troops from "
					+ ((Fortify) arg1).getFrom().getName() + " to "
					+ ((Fortify) arg1).getTo().getName() + ".\n");
		} else if (arg1 == ConsoleUpdate.PROGRESS_START) {
			progressBar.setMaximum(game.getCurrentPlayer().getTroopsToGive());
			progressBar.setValue(game.getCurrentPlayer().getTroopsToGive());
			progressBar.setString(""
					+ game.getCurrentPlayer().getTroopsToGive());
		} else if (arg1 == ConsoleUpdate.PROGRESS_BAR_UPDATE) {
			progressBar.setValue(game.getCurrentPlayer().getTroopsToGive());
			progressBar.setString(""
					+ game.getCurrentPlayer().getTroopsToGive());
		} else if (arg1 == ConsoleUpdate.UPDATE_STATS) {
			playerPanel.repaint();
		} else if (arg1 == ConsoleUpdate.PLAYER_DEFEATED) {
			console.append(game.getCurrentPlayer().getName() + " has just defeated a Player.\n\tCards have been transferred. \n");
		}else if (arg1 instanceof List && ((List)arg1).size() == 2) {
			console.append(game.getCurrentPlayer().getName() + " lost " + ((List)arg1).get(0) + ", They lost " + ((List)arg1).get(1) + "\n");
		}
		try {
			scrollPane.getVerticalScrollBar().setValue(scrollPane.getViewport().getView().getHeight());
		} catch (Exception e) {
		}
		playerPanel.repaint();
	}

}
