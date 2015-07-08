package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import player.Human;
import player.Player;

import base.Game;

/**
 * An end game dialog to appear when somebody wins the game.  It is not used.
 * 
 * @author Ross
 *
 */
public class EndGameDialog extends JFrame {

	/**
	 * Constructor for the dialog.  It needs an instance of the game to get the winner.
	 * 
	 * @param newGame
	 * 			the game to associate with.
	 */
	public EndGameDialog(Game newGame) {
		super();
		this.setSize(new Dimension(440, 370));
		this.setResizable(false);

		if (newGame.getWinner() instanceof Human) {
			this.setTitle("You win!");
		} else {
			this.setTitle("You lose");
		}

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		this.add(panel);

		JLabel winner = new JLabel("Winner: " + newGame.getWinner().getName());
		panel.add(winner);

		this.pack();
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}
}
