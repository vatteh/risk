package view;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import player.Human;
import player.Player;

import map.Territory;
import base.Game;

/**
 * The dialog that pops up when the player needs to make a decison to fortify or not.
 * 
 * @author Ross
 *
 */
public class FortifyDialog extends JDialog {

	private JSlider slider;
	private Human player;
	private Frame parentFrame;
	private Dialog dialog;

	/**
	 * The constructor for the dialog.
	 * 
	 * @param thisPlayer
	 * 			the player to display the dialog for.
	 * @param attackingTerritory
	 * 			the attacking territory.
	 * @param defendingTerritory
	 * 			the defending territory.
	 * @param isFortifyAfterAttack
	 * 			the deecision to fortify after an attack or not.
	 * @param parentFrame
	 * 			the dialogs parent frame.
	 */
	public FortifyDialog(Human thisPlayer, Territory attackingTerritory,
			Territory defendingTerritory, boolean isFortifyAfterAttack,
			JFrame parentFrame) {
		super(parentFrame, "Troops to move", true);
		this.parentFrame = parentFrame;
		this.setResizable(false);
		dialog = this;

		RiskFrame.clearAllSelectedTerritories();

		// Position in center of screen
		this.setLocationRelativeTo(parentFrame);
		this.setSize(450, 210);
		this.setLocation((int) (this.getX() - (this.getSize().getWidth() / 2)),
				(int) (this.getY() - (this.getSize().getHeight() / 2)));

		player = thisPlayer;
		this.setSize(new Dimension(140, 560));
		if (isFortifyAfterAttack) {
			this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		} else {
			this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		}

		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		this.add(panel);

		int minTroopsToMove;
		int maxTroopsToMove;

		if (isFortifyAfterAttack) {
			// Gets the players number of attacking dice or the total number of
			// troops which can be moved, whichever is smaller
			minTroopsToMove = Math.min(player.getNumAttackingDice(),
					attackingTerritory.getTroopsOnTerritory() - 1);
			maxTroopsToMove = attackingTerritory.getTroopsOnTerritory() - 1;
		} else {
			minTroopsToMove = 0;
			maxTroopsToMove = attackingTerritory.getTroopsOnTerritory() - 1;
		}

		panel.setLayout(new GridLayout(3, 1));

		JLabel label = new JLabel("How many troops do you want to move from "
				+ attackingTerritory.getFormattedName() + " to "
				+ defendingTerritory.getFormattedName() + "?\n");

		panel.add(label);

		slider = new JSlider(minTroopsToMove, maxTroopsToMove);
		if (maxTroopsToMove > 20) {
			slider.setMajorTickSpacing(5);
			slider.setMinorTickSpacing(1);
			if (isFortifyAfterAttack) {
				slider.setLabelTable(slider.createStandardLabels(5, 5)); // Start
				// at
				// 5 and
				// increment
				// by 5
			} else {
				slider.setLabelTable(slider.createStandardLabels(5, 0));
			}
		} else {
			slider.setMajorTickSpacing(1);
			slider.setLabelTable(slider.createStandardLabels(1));
		}

		slider.setPaintTicks(true);
		slider.setSnapToTicks(true);
		slider.setPaintLabels(true);
		slider.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // top =
		// 5
		panel.add(slider);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 3));
		panel.add(buttonPanel);

		JButton submit = new JButton("Ok");
		submit.addActionListener(new GoButtonListener());
		buttonPanel.add(submit);

		buttonPanel.add(new JLabel()); // make this cell empty

		JButton maxButton = new JButton("Max");
		maxButton.addActionListener(new MaxButtonListener());
		buttonPanel.add(maxButton);

		pack();
		setVisible(true);
	}

	private class GoButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			player.setFortifyNum(slider.getValue());
			dialog.dispose();
		}
	}

	private class MaxButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			player.setFortifyNum(slider.getMaximum());
			dialog.dispose();
		}
	}
}
