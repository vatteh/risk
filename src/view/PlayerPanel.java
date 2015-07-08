package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import player.Player;

import base.Game;

/**
 * This draws the players in hte console panel.  It is the list of current players,
 * their colors, drawn on the screen, and a yellow pointer next to the player
 * whose turn it is.
 * 
 * @author Victor
 *
 */
public class PlayerPanel extends JPanel{
	
private Game game;

	/**
	 * Constructs the Panel
	 * @param game - the game to draw from
	 */
	
	public PlayerPanel(Game game) {
		this.game = game;
		this.setPreferredSize(new Dimension(480,55));
	
	}

	/**
	 * Paints the players on the panel
	 */
	public void paint(Graphics g) {
		g.setColor(Color.lightGray);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g.setFont(new Font("SansSerif", Font.BOLD, 12));
		g.setColor(Color.DARK_GRAY);
		
		int x = 30;
		int y = 20;
		int i = 1;
		g.setFont(new Font("SansSerif", Font.BOLD, 16));
		g.setColor(Color.black);
		
		for (Player player: game.getAllPlayers()) {
			g.setColor(player.getColor());
			g.drawString(player.getName(), x, y);
					
			if (player == game.getCurrentPlayer()) {
				g.setColor(Color.YELLOW);
				g.fillOval(x - 15, y - 10, 10, 10);
			}
			
			x += 150;
			if (i == 3) {
				y += 20;
				x = 30;
			}
			i++;
		}
	}
}
