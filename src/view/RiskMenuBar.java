package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;

import player.Human;

import base.Game;
import base.GameSettings;
import base.Stage;

/**
 * This is the menu bar of the main Risk frame.
 * 
 * @author Duong Pham
 *
 */
public class RiskMenuBar extends JMenuBar implements Observer{

	private static final long serialVersionUID = -9208928533413877331L;
	private Game game;
	private GameSettings gameSettings;
	private JMenu gameFile;
	private JMenu settings;
	private JMenu help;
	private JMenu statistics;
	private JMenuItem saveOption;
	private JMenuItem loadOption;
	private JMenuItem exitOption;
	private JMenuItem attackDiceLabel;
	private JRadioButtonMenuItem attackDiceRBMenuItem1;
	private JRadioButtonMenuItem attackDiceRBMenuItem2;
	private JRadioButtonMenuItem attackDiceRBMenuItem3;
	private JMenuItem defendDiceLabel;
	private JRadioButtonMenuItem defendDiceRBMenuItem1;
	private JRadioButtonMenuItem defendDiceRBMenuItem2;
	private JCheckBoxMenuItem soundCBMenuItem;
	private JCheckBoxMenuItem animationCBMenuItem;
	private JMenuItem tipsOption;
	private JMenuItem aboutOption;
	private JMenuItem viewStatsOption;
	
	/**
	 * This is the constructor, it needs an instance of the Game.
	 * 
	 * @param game
	 * 			the game to associate itself to.
	 */
	public RiskMenuBar(Game game){
		
		super();
		
		this.game = game;
		game.addObserver(this);
		gameSettings = game.getGameSettings();
		
		gameFile = new JMenu("Game");
		settings = new JMenu("Settings");
		help = new JMenu("Help");
		statistics = new JMenu("Statistics");
		
		loadOption = new JMenuItem("Load");
		saveOption = new JMenuItem("Save");
		exitOption = new JMenuItem("Exit");
		
		loadOption.addActionListener(new GameActionListener());
		saveOption.addActionListener(new GameActionListener());
		exitOption.addActionListener(new GameActionListener());
		
		gameFile.add(loadOption);
		gameFile.add(saveOption);
		gameFile.addSeparator();
		gameFile.add(exitOption);
		
		aboutOption = new JMenuItem("About");
		tipsOption = new JMenuItem("Bonuses");
		
		aboutOption.addActionListener(new HelpActionListener());
		tipsOption.addActionListener(new TipsActionListener());
		
		help.add(tipsOption);
		help.add(aboutOption);
		
		viewStatsOption = new JMenuItem("View Current Game Stats");
		viewStatsOption.addActionListener(new StatsActionListener());
		statistics.add(viewStatsOption);
		
		ButtonGroup group1 = new ButtonGroup();
		attackDiceRBMenuItem1 = new JRadioButtonMenuItem("Attack Dice 1");
		attackDiceRBMenuItem2 = new JRadioButtonMenuItem("Attack Dice 2");
		attackDiceRBMenuItem3 = new JRadioButtonMenuItem("Attack Dice 3");
		attackDiceRBMenuItem1.addActionListener(new SettingsCheckBoxListener());
		attackDiceRBMenuItem2.addActionListener(new SettingsCheckBoxListener());
		attackDiceRBMenuItem3.addActionListener(new SettingsCheckBoxListener());
		group1.add(attackDiceRBMenuItem1);
		group1.add(attackDiceRBMenuItem2);
		group1.add(attackDiceRBMenuItem3);
		
		attackDiceRBMenuItem3.setSelected(true);
		
		attackDiceLabel = new JMenuItem("Default Attack Dice");
		attackDiceLabel.setEnabled(false);
		
		settings.add(attackDiceLabel);
		settings.add(attackDiceRBMenuItem1);
		settings.add(attackDiceRBMenuItem2);
		settings.add(attackDiceRBMenuItem3);
		
		settings.addSeparator();
		
		ButtonGroup group2 = new ButtonGroup();
		defendDiceRBMenuItem1 = new JRadioButtonMenuItem("Defend Dice 1");
		defendDiceRBMenuItem2 = new JRadioButtonMenuItem("Defend Dice 2");
		defendDiceRBMenuItem1.addActionListener(new SettingsCheckBoxListener());
		defendDiceRBMenuItem2.addActionListener(new SettingsCheckBoxListener());
		group2.add(defendDiceRBMenuItem1);
		group2.add(defendDiceRBMenuItem2);
		
		defendDiceRBMenuItem2.setSelected(true);
		
		defendDiceLabel = new JMenuItem("Default Defend Dice");
		defendDiceLabel.setEnabled(false);
		
		settings.add(defendDiceLabel);
		settings.add(defendDiceRBMenuItem1);
		settings.add(defendDiceRBMenuItem2);
		
		settings.addSeparator();
		
		soundCBMenuItem = new JCheckBoxMenuItem("Sounds on");
		animationCBMenuItem = new JCheckBoxMenuItem("Animations on");
		
		soundCBMenuItem.setSelected(true);
		animationCBMenuItem.setSelected(true);
		
		soundCBMenuItem.addActionListener(new SettingsCheckBoxListener());
		animationCBMenuItem.addActionListener(new SettingsCheckBoxListener());
		
		settings.add(soundCBMenuItem);
		settings.add(animationCBMenuItem);
		
		this.add(gameFile);
		this.add(settings);
		this.add(help);
		this.add(statistics);
	}

	private class GameActionListener implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			JMenuItem source = (JMenuItem) arg0.getSource();
			if (source.getText().equals("New")) {
				Game loadedGame = new Game();	
				Game.setLoadedGame((Game)loadedGame);
			}
			else if (source.getText().equals("Load")) {
				Object loadedGame = FileOutputGUI.getObject();
				
				if (loadedGame instanceof Game) {	
					Game.setLoadedGame((Game)loadedGame);
				}

			}
			else if (source.getText().equals("Save")) {
				FileOutputGUI.saveObject(game);
			}
			else if (source.getText().equals("Exit")) {
				System.exit(0);
			}	
		}
	}

	private class HelpActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			String aboutGame = "";
			aboutGame+="Risk is a turn-based game for three to six players and is played on a board depicting a stylized Napoleonic-era";
			aboutGame+="\npolitical map of the Earth,divided into forty-two territories, which are grouped into six continents.";
			aboutGame+="\nPlayers control armies with which they attempt to capture territories from other players. The goal of the";  
			aboutGame+="\ngame is \"world domination,\" to control all the territories or \"conquer the world\" through the elimination";
			aboutGame+="\nof the other players. Using area movement, Risk ignores limitations such as the vast size of the world and";
			aboutGame+="\nthe logistics of long campaigns.  (source: Wikipedia)";
			aboutGame+="\n\nExtra Features: Loading, Saving, and Game Stats";
			aboutGame+="\n\nAuthors:\n\t\u2022 Victor Atteh\n\t\u2022 Duong Pham\n\t\u2022 Phil Lee\n\t\u2022 Ross Miller";
			JOptionPane.showMessageDialog(null,aboutGame,"About Risk", JOptionPane.PLAIN_MESSAGE);
		}
	}
	
	private class SettingsCheckBoxListener implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {	
			if (arg0.getSource().equals(animationCBMenuItem))
				gameSettings.togglePlayAnimations(animationCBMenuItem.isSelected());
			if (arg0.getSource() == soundCBMenuItem)
				gameSettings.togglePlaySounds(soundCBMenuItem.isSelected());
			if(arg0.getSource() == attackDiceRBMenuItem1)
				game.getAllPlayers().get(0).setNumAttackingDice(1);
			if(arg0.getSource() == attackDiceRBMenuItem2)
				game.getAllPlayers().get(0).setNumAttackingDice(2);
			if(arg0.getSource() == attackDiceRBMenuItem3)
				game.getAllPlayers().get(0).setNumAttackingDice(3);
			if(arg0.getSource() == defendDiceRBMenuItem1)
				game.getAllPlayers().get(0).setNumDefendingDice(1);
			if(arg0.getSource() == defendDiceRBMenuItem2)
				game.getAllPlayers().get(0).setNumDefendingDice(2);
		}
	}
	
	private class TipsActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String tipsText = "";
			tipsText += "Asia: 7\n";
			tipsText += "North America: 5\n";
			tipsText += "Europe: 5\n";
			tipsText += "Africa: 3\n";
			tipsText += "South America: 2\n";
			tipsText += "Oceania: 2\n";	
			JOptionPane.showMessageDialog(null,tipsText,"Continent Bonuses", JOptionPane.PLAIN_MESSAGE);
		}
	}
	
	private class StatsActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			game.getGameStats().update(null, null);
			GameStatsFrame statsFrame = new GameStatsFrame(game.getGameStats());
		}
	}

	public void update(Observable arg0, Object arg1) {
		if (game.getCurrentStage() == Stage.REINFORCEMENTS && game.getCurrentPlayer() instanceof Human) {
			loadOption.setEnabled(true);
			saveOption.setEnabled(true);
		}
		else {
			loadOption.setEnabled(false);
			saveOption.setEnabled(false);
		}
		
	}
	
}