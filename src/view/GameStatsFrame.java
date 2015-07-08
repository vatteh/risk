package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import player.Human;

/**
 * Frame and panel that holds all the stats.  This can be displayed in game from the menu bar.  
 * It also appears at the very end of the game.
 * 
 * @author Duong Pham
 *
 */
public class GameStatsFrame extends JFrame {
	
	private Statistics gameStats;
	private GameStatsPanel mainStatsPanel;
	private int h = 600;
	private int w = 480;
	
	/**
	 * This is the contructor.
	 * 
	 * @param gameStats
	 * 			the frame needs an instance of the statistics object.
	 */
	public GameStatsFrame(Statistics gameStats) {
		this.gameStats = gameStats;
		
		mainStatsPanel = new GameStatsPanel(gameStats);
		
		this.setTitle("Risk Game Stats");
		this.setPreferredSize(new Dimension(h,w));
		this.setLayout(new BorderLayout());
		
		this.add(mainStatsPanel, BorderLayout.NORTH);
		
		this.setLocationRelativeTo(null);
		this.setLocation(getX() - h/2, getY() - w/2);
		this.setResizable(false);
		this.pack();
		this.setVisible(true);
		this.setAlwaysOnTop(true);
	}
	
	private class GameStatsPanel extends JPanel implements Observer {
		
		private TitledBorder header;
		private JTextArea textArea;
	
		public GameStatsPanel(Statistics gameStats) {
			gameStats.addObserver(this);
			
			String headerStr = "";
			if (gameStats.winner instanceof Human) {
				headerStr = "Congratulations " + gameStats.winner.getName() + " you are the winner";
			} else {
				headerStr = "Sorrry, but you just lost the game";
			}
			if (gameStats.winner == null) {
				headerStr = "Game Statistics";
			}
			header = BorderFactory.createTitledBorder(headerStr);
			Border blackline = BorderFactory.createLineBorder(Color.BLACK);
			header.setTitleJustification(TitledBorder.CENTER);
			header.setBorder(blackline);
			this.setBorder(header);
			
			this.setLayout(new GridLayout(30,1));
			this.setMaximumSize(new Dimension(h,w));
			
			textArea = new JTextArea();
			textArea.setEditable(false);
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
			textArea.setSize(640, 480);
			textArea.setFont(new Font("TRUETYPE_FONT", Font.BOLD, 12));
			textArea.setForeground(Color.BLACK);
			textArea.setBackground(this.getBackground());
			this.add(textArea);
			
			update(null, null);
		}

		public void update(Observable o, Object arg) {
			textArea.setText("");
			
			String totalTimePlayed = "Total time played:  " + gameStats.totalTimePlayed;
			String totalRounds = "Total rounds played:  " + gameStats.roundsPlayed;
			String numTroopsOwned = "Number of troops under your command:  " + gameStats.totalTroopsOwned;		
			String totalAttacks = "Number of attacks you've done:  " + gameStats.totalAttacks;
			String totalBattlesWon = "Number of battles you won:  " + gameStats.totalBattlesWon;
			String totalBattlesLost = "Number of battles you lost:  " + gameStats.totalBattlesLost;
			String totalNumberRetreats = "Number of retreats:  " + gameStats.totalNumRetreats;
			
			String territoriesOwned = "";
			if (gameStats.territoriesOwnedByPlayer != null) {
				territoriesOwned = "Terrritories you own:  " + gameStats.territoriesOwnedByPlayer;
			} else {
				territoriesOwned = "Terrritories you own:  -";
			}
			
			String continentsOwned = "";
			if (gameStats.continentsOwnedByPlayer != null) {
				continentsOwned = "Continents you own:  " + gameStats.continentsOwnedByPlayer;
			} else {
				continentsOwned = "Continents you own:  -";
			}
			
			String numTerrOwned = "";
			if (gameStats.continentsOwnedByPlayer != null) {
				numTerrOwned = "Number of territories you own:  " + gameStats.numTerritoriesOwnedByPlayer;
			} else {
				numTerrOwned = "Number of territories you own:  -";
			}
			
			String numContinentsOwned = "";
			if (gameStats.continentsOwnedByPlayer != null) {
				numContinentsOwned = "Number of continents you own:  " + gameStats.numContinentsOwnedByPlayer;
			} else {
				numContinentsOwned = "Number of continents you own:  -";
			}
			
			String mostAttackedT = "";
			if (gameStats.mostAttackedTerritory != null) {
				mostAttackedT = "Most aggressive territory:  " + gameStats.mostAttackedTerritory;
			} else {
				mostAttackedT = "Most aggressive territory:  -";
			}
			
			String mostDefendedT = "";
			if (gameStats.mostDefendedTerritory != null) {
				mostDefendedT = "Most war ravaged territory:  " + gameStats.mostDefendedTerritory;
			} else {
				mostDefendedT = "Most war ravaged territory:  -";
			}
			
			String pacifistT = "";
			if (gameStats.mostPacifistTerritory != null) {
				pacifistT = "Most pacifist territory:  " + gameStats.mostPacifistTerritory;
			} else {
				pacifistT = "Most pacifist territory:  -";
			}

			String troopsLived = "Total number of troops that ever lived:  " + gameStats.totalTroopsEverLived;
			String troopsKilledOverTime = "Total casualties over time:  " + gameStats.totalTroopsEverDied;
			String troopsSurvived = "Number of surviving troops:  " + gameStats.survivngTroops;
			
			textArea.append(totalTimePlayed +"\n");
			textArea.append(totalRounds +"\n\n");
			textArea.append(numTroopsOwned +"\n"); // your command
			textArea.append(totalAttacks +"\n");
			textArea.append(totalBattlesWon +"\n");
			textArea.append(totalBattlesLost +"\n");
			textArea.append(totalNumberRetreats + "\n\n");			
			textArea.append(territoriesOwned + "\n");
			textArea.append(continentsOwned + "\n");
			textArea.append(numTerrOwned + "\n");
			textArea.append(numContinentsOwned + "\n\n");
			textArea.append(mostAttackedT +"\n");
			textArea.append(mostDefendedT +"\n");
			textArea.append(pacifistT + "\n\n");
			textArea.append(troopsLived +"\n");
			textArea.append(troopsKilledOverTime +"\n");
			textArea.append(troopsSurvived);
		}
	}
	
}
