package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import base.Game;
import base.Stage;

/**
 * The stats panel holds some inforamtiuon on the current state of the game,
 * including the current round, current stage, and current trade in value.
 * 
 * @author Victor
 *
 */
public class StatsPanel extends JPanel {

	private Game game;
	
	/**
	 * Constructor.
	 * 
	 * @param game
	 * 			the game to associate with.
	 */
	public StatsPanel(Game game) {
		this.game = game;
		this.setPreferredSize(new Dimension(480, 14));
		this.setBackground(Color.black);
	}

	/**
	 * Paints the players on the panel
	 */
	public void paint(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// Current stats title
		g.setFont(new Font("TRUETYPE_FONT", Font.BOLD, 12));
		g.setColor(Color.DARK_GRAY);
		//g.drawString("Current Stats", 175, 10);

		// set the font
		g.setFont(new Font("TRUETYPE_FONT", Font.BOLD, 12));
		
		// Current Round
		g.setColor(Color.RED);
		g.drawString("Current Round: ", 0, 12);
		g.setColor(Color.BLACK);
		Integer round = game.getRound();
		g.drawString(round.toString(), 100, 12);
		
		// Turn in Value
		g.setColor(Color.RED);
		g.drawString("Turn in Value: ", 150, 12);
		g.setColor(Color.BLACK);
		Integer turnInValue = game.getTurnInValue();
		g.drawString(turnInValue.toString(), 240, 12);

		// Current Stage
		g.setColor(Color.RED);
		g.drawString("Current Stage: ", 290, 12);
		g.setColor(Color.BLACK);
		
		String stage;
		if (game.getCurrentStage() == Stage.PREGAME)
			stage = "Pre-Game";
		else if (game.getCurrentStage() == Stage.REINFORCEMENTS)
			stage = "Reinforcement";
		else if (game.getCurrentStage() == Stage.ATTACK)
			stage = "Attack";
		else
			stage = "Fortify";
		g.drawString(stage, 380, 12);

	}
}
