package view;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

import player.Human;
import player.Player;

import map.Map;
import map.Territory;

import base.Game;
import base.Stage;

/**
 * Keeps track of all the stats. A thread is continuously running to update the
 * stats every second.
 * 
 * @author Duong Pham
 * 
 */
public class Statistics extends Observable implements Observer, Serializable {

	private static final long serialVersionUID = 6765561195972235723L;
	private Game game;
	private Map map;
	public static Player winner;
	public static String totalTimePlayed;
	public static int roundsPlayed;
	public static int totalTroopsOwned;
	public static int totalTroopsEverLived;
	public static int totalTroopsEverDied;
	public static int totalAttacks;
	public static int totalBattlesWon;
	public static int totalBattlesLost;
	public static String mostAttackedTerritory;
	public static String mostDefendedTerritory;
	public static String territoriesOwnedByPlayer;
	public static String continentsOwnedByPlayer;
	public static String numTerritoriesOwnedByPlayer;
	public static String numContinentsOwnedByPlayer;
	public static int totalNumRetreats;
	public static int survivngTroops;
	private static Thread timeThread;
	public static String mostPacifistTerritory; 

	/**
	 * This is the constructor.
	 * 
	 * @param game
	 *            a Game class is needed to grab stats from.
	 */
	public Statistics(Game game) {

		this.game = game;
		map = this.game.getMap();
		game.addObserver(this);
		totalTroopsEverLived = 0;
		for (Territory t : map.getTerritories()) {
			totalTroopsEverLived += t.getTroopsOnTerritory();
		}
		totalAttacks = 0;
		totalBattlesWon = 0;
		totalBattlesLost = 0;
		totalTroopsEverLived = 0;
		totalTroopsEverDied = 0;
		totalNumRetreats = 0;
		survivngTroops = 0;
		this.update(null, null);
		
		timeThread = new Thread(new Runnable() {
			public void run() {
				while (true) {
					update(null, null);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		timeThread.start();
	}

	/**
	 * Updates the stats.
	 * 
	 * @param o
	 * @param arg
	 */
	public void update(Observable o, Object arg) {
		double elapsedTimeInSeconds = (System.currentTimeMillis() - Game.startTime) / 1000;
		totalTimePlayed = ((int) elapsedTimeInSeconds / 3600) + " hrs "
				+ ((int) (elapsedTimeInSeconds % 3600) / 60) + " mins "
				+ ((int) elapsedTimeInSeconds % 60) + " secs";
		if (game != null) {
			roundsPlayed = game.getRound();
		}
		totalTroopsOwned = 0;
		if (game.getCurrentPlayer() instanceof Human) {
			for (Territory t : map.getFriendlyTerritories(game.getAllPlayers()
					.get(0))) {
				totalTroopsOwned += t.getTroopsOnTerritory();
			}
		}
		if (arg == StatisticsUpdate.ATTACKING
				&& game.getCurrentPlayer() instanceof Human)
			totalAttacks++;
		if (arg == StatisticsUpdate.BATTLE_WON
				&& game.getCurrentPlayer() instanceof Human)
			totalBattlesWon++;
		if (arg == StatisticsUpdate.BATTLE_LOST
				&& game.getCurrentPlayer() instanceof Human)
			totalBattlesLost--;
		if (arg == StatisticsUpdate.TERRITORY_ATTACKED) {
			mostAttackedTerritory = map.getMostAttackedTerritory();
			mostDefendedTerritory = map.getMostDefendedTerritory();
			mostPacifistTerritory = map.getLeastAttackedTerritory();
		}
		if (arg == StatisticsUpdate.TROOP_BORN) {
			totalTroopsEverLived++;
		}
		if (arg == StatisticsUpdate.TROOP_DEADED) {
			totalTroopsEverDied++;
		}
		if (game.getCurrentPlayer() instanceof Human) {
			territoriesOwnedByPlayer = map.getTerritoryOwnedText(game
					.getCurrentPlayer());
		}
		if (game.getCurrentPlayer() instanceof Human) {
			numTerritoriesOwnedByPlayer = ""
					+ map.getFriendlyTerritories(game.getCurrentPlayer())
							.size();
		}
		if (game.getCurrentPlayer() instanceof Human) {
			continentsOwnedByPlayer = map.getContinentsOwnedText(game
					.getCurrentPlayer());
		}
		if (game.getCurrentPlayer() instanceof Human) {
			numContinentsOwnedByPlayer = ""
					+ map.getConqueredContinents(game.getCurrentPlayer())
							.size();
		}
		if (arg == StatisticsUpdate.RETREAT
				&& game.getCurrentPlayer() instanceof Human) {
			totalNumRetreats++;
		}
		survivngTroops = totalTroopsEverLived - totalTroopsEverDied;
		if (arg == StatisticsUpdate.GAME_WON) {
			winner = game.getCurrentPlayer();
			try {
				timeThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.setChanged();
		this.notifyObservers();
	}

}
