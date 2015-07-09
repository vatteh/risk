package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import player.ExpertAIStrategy;
import player.Human;
import player.Player;
import player.SimpleAIStrategy;

import base.Game;

public class PreGameInputDialog extends JFrame {
	private JFrame frame;
	private Game game;

	private JComboBox[] playerType;
	private JComboBox[] playerColor;
	private JTextField[] playerName;
	private JRadioButton chooseTerritories;
	private JRadioButton randomizeTerritories;

	private ImageIcon iconBlue = new ImageIcon("color_icons/blue.png");
	private ImageIcon iconBrown = new ImageIcon("color_icons/brown.png");
	private ImageIcon iconGreen = new ImageIcon("color_icons/green.png");
	private ImageIcon iconPurple = new ImageIcon("color_icons/purple.png");
	private ImageIcon iconRed = new ImageIcon("color_icons/red.png");
	private ImageIcon iconTeal = new ImageIcon("color_icons/teal.png");

	// public static void main(String[] args) {
	// new PreGameInputDialog(new Game());
	// }

	public PreGameInputDialog(Game newGame) {
		super("Choose your settings");
		frame = this;

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(new Dimension(440, 370));
		this.setResizable(false);

		// Position in center of screen
		frame.setLocationRelativeTo(null);

		game = newGame;

		JPanel playersPanel = getPlayersPanel();
		this.add(playersPanel, BorderLayout.NORTH);

		JPanel optionsPanel = getOptionsPanel();
		this.add(optionsPanel, BorderLayout.CENTER);

		JPanel goPanel = getGoPanel();
		this.add(goPanel, BorderLayout.SOUTH);
		this.setAlwaysOnTop(true);
		this.pack();
		this.setVisible(true);
	}

	private JPanel getPlayersPanel() {
		JPanel playersPanel = new JPanel();
		playersPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15)); // no
		// border
		// on
		// bottom
		playersPanel.setPreferredSize(new Dimension(440, 240));
		playersPanel.setLayout(new GridLayout(7, 3, 20, 10)); // rows, cols,
		// hgap, vgap

		// Add in all of the stuff
		playerType = new JComboBox[6];
		playerColor = new JComboBox[6];
		playerName = new JTextField[6];

		for (int i = 0; i < 6; i++) {
			playerType[i] = new JComboBox();
			playerColor[i] = new JComboBox();
			playerName[i] = new JTextField();

			playersPanel.add(playerType[i]);
			if (i == 0) {
				playerType[i].addItem("Human");
				playerType[i].addItem("Expert Computer");
				playerType[i].addItem("Easy Computer");
			} else {
				playerType[i].addItem("Expert Computer");
				playerType[i].addItem("Easy Computer");
				playerType[i].addItem("Closed");
			}

			if (i > 2) {
				playerType[i].setSelectedItem("Closed");
				playerColor[i].setEnabled(false);
				playerName[i].setEnabled(false);
			} else if (i == 1 || i == 2) {
				playerType[i].setSelectedItem("Easy Computer");
			}

			playersPanel.add(playerColor[i]);
			playerColor[i].addItem(iconBlue);
			playerColor[i].addItem(iconRed);
			playerColor[i].addItem(iconGreen);
			playerColor[i].addItem(iconPurple);
			playerColor[i].addItem(iconBrown);
			playerColor[i].addItem(iconTeal);
			playerColor[i].setSelectedIndex(i);

			playersPanel.add(playerName[i]);
		}

		// Add Listeners at the way end. This way the comboboxes will already be
		// set up and ComboChangeListener will not throw NullPointerExceptions
		for (int i = 0; i < 6; i++) {
			playerType[i]
					.addActionListener((ActionListener) new ComboChangeListener());
		}

		updatePlayerNames();

		return playersPanel;
	}

	private JPanel getOptionsPanel() {
		JPanel optionsPanel = new JPanel();
		optionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15)); // no
		// border
		// on
		// top
		optionsPanel.setPreferredSize(new Dimension(440, 60));
		optionsPanel.setLayout(new GridLayout(2, 2, 20, 10)); // rows, cols,
		// hgap, vgap

		ButtonGroup buttons = new ButtonGroup();
		chooseTerritories = new JRadioButton("Choose initial territories");
		randomizeTerritories = new JRadioButton("Randomize initial territories");
		buttons.add(chooseTerritories);
		buttons.add(randomizeTerritories);
		optionsPanel.add(chooseTerritories);
		optionsPanel.add(randomizeTerritories);
		chooseTerritories.setSelected(true);

		return optionsPanel;
	}

	private JPanel getGoPanel() {
		JPanel goPanel = new JPanel();
		goPanel.setLayout(new FlowLayout());
		JButton submitButton = new JButton("GO!");
		submitButton.addActionListener(new FormSubmitListener());
		goPanel.add(submitButton);
		this.add(goPanel, BorderLayout.SOUTH);

		return goPanel;
	}

	private class FormSubmitListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {

			Set<String> errors = new HashSet<String>();
			Set<ImageIcon> playerColors = new HashSet<ImageIcon>();
			int numPlayers = 0;

			for (int i = 0; i < 6; i++) {
				String selectedPlayerType = (String) ((JComboBox) playerType[i])
						.getSelectedItem();

				// No two players can have the same color
				if (selectedPlayerType != "Closed") {
					boolean checkImages = playerColors
							.add((ImageIcon) ((JComboBox) playerColor[i])
									.getSelectedItem());
					if (!checkImages) {
						errors.add("No two players can have the same color");
					}
				}

				// Every player needs a name
				if (selectedPlayerType.equals("Human")) {
					if (playerName[i].getText().trim().equals("")) {
						errors.add("You need a name");
					}
					numPlayers++;
				} else if (!selectedPlayerType.equals("Closed")) { // Else if
					// they are a computer
					numPlayers++;
					if (playerName[i].getText().trim().equals("")) {
						errors.add("Computer " + i + " needs a name");
					}
				}
			}

			if (numPlayers < 3) {
				errors.add("You cannot have a game with fewer than 3 players.");
			}

			// Display all of the errors
			String errorString = "";
			for (String error : errors) {
				errorString += "•  " + error + "\n";
			}

			if (errors.size() > 0) {
				JOptionPane.showMessageDialog(frame, errorString, "Error",
						JOptionPane.ERROR_MESSAGE);
			} else {
				List<Player> players = new ArrayList<Player>();

				for (int i = 0; i < 6; i++) {
					if (!((JComboBox) playerType[i]).getSelectedItem().equals(
							"Closed")) {
						Color color;
						Player newPlayer;

						if (playerColor[i].getSelectedItem().equals(iconTeal)) {
							color = new Color(24, 143, 140); // teal
						} else if (playerColor[i].getSelectedItem().equals(
								iconRed)) {
							color = new Color(189, 13, 13); // red
						} else if (playerColor[i].getSelectedItem().equals(
								iconPurple)) {
							color = new Color(128, 21, 124); // purple
						} else if (playerColor[i].getSelectedItem().equals(
								iconGreen)) {
							color = new Color(27, 168, 13); // green
						} else if (playerColor[i].getSelectedItem().equals(
								iconBrown)) {
							color = new Color(90, 69, 6); // brown
						} else {
							color = new Color(16, 20, 209); // blue
						}

						if (((JComboBox) playerType[i]).getSelectedItem()
								.equals("Human")) {
							newPlayer = new Human(playerName[i].getText(),
									color, game.getMap());
						} else if (((JComboBox) playerType[i])
								.getSelectedItem().equals("Expert Computer")) {
							newPlayer = new ExpertAIStrategy(playerName[i]
									.getText(), color, game.getMap());
						} else {
							newPlayer = new SimpleAIStrategy(playerName[i]
									.getText(), color, game.getMap());
						}

						players.add(newPlayer);
					}
				}

				game.givePlayers(players);

				if (chooseTerritories.isSelected()) {
					game.setChoose(true);
				} else {
					game.setChoose(false);
				}

				frame.dispose();
			}
		}
	}

	private void updatePlayerNames() {
		int numExpertAI = 0;
		int numEasyAI = 0;
		for (int i = 0; i < 6; i++) {
			String name = playerName[i].getText();
			if (playerType[i].getSelectedItem().equals("Easy Computer")) {
				if (name.length() >= 5
						&& (name.substring(0, 4).equals("Easy")
								|| name.substring(0, 4).equals("Expe") || name
								.substring(0, 4).equals("Play"))) {
					numEasyAI++;
					playerName[i].setText("Easy AI " + numEasyAI);
				} else if (name.trim().equals("")) {
					numEasyAI++;
					playerName[i].setText("Easy AI " + numEasyAI);
				}
			} else if (playerType[i].getSelectedItem()
					.equals("Expert Computer")) {
				if (name.length() >= 5
						&& (name.substring(0, 4).equals("Easy")
								|| name.substring(0, 4).equals("Expe") || name
								.substring(0, 4).equals("Play"))) {
					numExpertAI++;
					playerName[i].setText("Expert AI " + numExpertAI);
				} else if (name.trim().equals("")) {
					numExpertAI++;
					playerName[i].setText("Expert AI " + numExpertAI);
				}
			} else if (playerType[i].getSelectedItem().equals("Human")) {
				if (name.length() >= 5
						&& (name.substring(0, 4).equals("Easy")
								|| name.substring(0, 4).equals("Expe") || name
								.substring(0, 4).equals("Play"))) {
					playerName[i].setText("Player");
				}
				if (name.trim().equals("")) {
					playerName[i].setText("Player");
				}
			}
		}
	}

	private class ComboChangeListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < 6; i++) {
				String selectedPlayerType = (String) ((JComboBox) playerType[i])
						.getSelectedItem();
				if (selectedPlayerType.equals("Closed")) {
					playerColor[i].setEnabled(false);
					playerName[i].setEnabled(false);
				} else {
					playerColor[i].setEnabled(true);
					playerName[i].setEnabled(true);
				}

				updatePlayerNames();

			}
		}
	}
}