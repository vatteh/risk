package view;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import javax.swing.JFrame;

import player.Attack;
import player.Fortify;
import player.Reinforcement;
import sun.audio.AudioData;
import sun.audio.AudioDataStream;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import map.Continent;
import map.Territories;
import map.Territory;

import base.Game;

public class RiskFrame extends JFrame {

	public static final int SELECT_FOR_ATTACKS = 2;
	public static final int SELECT_FOR_FORTIFY = 2;
	public static final int SELECT_FOR_PREGAME = 1;
	public static final int SELECT_FOR_REINFORCEMENTS = 1;
	public static final int SELECT_NONE = 0;

	private static MainPanel mainPanel;
	private static Object animationLock;
	private static boolean waiting;

	public RiskFrame(Game game) {

		animationLock = new Object();
		waiting = false;
		mainPanel = new MainPanel(game);
		setContentPane(mainPanel);
		this.setResizable(false);
		this.setJMenuBar(new RiskMenuBar(game));
		this.setPreferredSize(new Dimension(1005, 730));
		this.setTitle("Risk - Team BSOD");
		this.initialize();
	}

	private static GameMapPanel getGameMapPanel() {
		return mainPanel.getGameMapPanel();
	}

	/**
	 * Initializes the GameMapPanel with territories drawn as neutral
	 */
	public static void initializeGameMapPanel() {
		getGameMapPanel().initializeOpeningAnimation();
	}

	/**
	 * Updates the view's representation of the game state, specifically who
	 * owns which territory and how many troops there are in each territory.
	 * 
	 * (please restrict the size of this list to only the number of territories
	 * that has changed - re-tinting the images has a high time-cost);
	 * 
	 * @param changedTerritories
	 *            - the list of Territories that has changed in either owner or
	 *            number of troops
	 * 
	 */
	public static void updateMapPanel(List<Territory> changedTerritories) {
		getGameMapPanel().update(changedTerritories);
	}

	/**
	 * 
	 * Gets the selected Territories as shown on the GameMapPanel.
	 * 
	 * The array that this method returned is ordered, meaning that whichever
	 * Territory was selected first precedes the ones selected later.
	 * 
	 * If no territories are selected, this will simply return an empty array.
	 * 
	 * Postcondition: The size of the array <= 2
	 * 
	 * @return - the selected Territory(s)
	 */
	public static Territory[] getSelectedTerritories() {

		return getGameMapPanel().getSelectedTerritories();
	}

	public static void fireDiceRollAnimation(DiceRoll roll) {

		mainPanel.getDiceTrayContainer().fireDiceRollAnimation(roll);

	}

	/**
	 * Fires the MapPanel animation for an attack move Only one thread can be
	 * calling this method.
	 * 
	 *@param attack
	 *            - the Attack object
	 */
	public static void fireAttackAnimation(final Attack attack) {

		Runnable newThread = new Runnable() {
			public void run() {
				synchronized (animationLock) {
					while (getGameMapPanel().animationOn()) {

					}
					getGameMapPanel().turnOnAnimation();
					getGameMapPanel().fireAttackAnimation(
							attack.getDefendingTerritory(),
							attack.getAttackingTerritory());
					releaseLock();
				}

			}
		};

		dispatchThread(newThread);

	}

	/**
	 * * Fires the MapPanel animation for a fortify move
	 * 
	 * Only one thread can be calling this method.
	 * 
	 * @param fortify
	 *            - the Fortify object
	 * 
	 */
	public static void fireFortifyAnimation(final Fortify fortify) {

		Runnable newThread = new Runnable() {
			public void run() {
				synchronized (animationLock) {
					while (getGameMapPanel().animationOn()) {

					}

					getGameMapPanel().turnOnAnimation();
					getGameMapPanel().fireFortifyAnimation(fortify.getFrom(),
							fortify.getTo());

					releaseLock();
				}

			}
		};

		dispatchThread(newThread);

	}

	public static void fireReinforcementAnimation(final Reinforcement reinforce) {
		Runnable newThread = new Runnable() {
			public void run() {
				synchronized (animationLock) {
					while (getGameMapPanel().animationOn()) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					getGameMapPanel().turnOnAnimation();
					getGameMapPanel().fireReinforcementAnimation(
							reinforce.getTerritory());

					releaseLock();
				}

			}
		};

		dispatchThread(newThread);
	}

	public static void fireConqueredAnimation(final Territory at) {
		Runnable newThread = new Runnable() {
			public void run() {
				synchronized (animationLock) {
					while (getGameMapPanel().animationOn()) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					getGameMapPanel().turnOnAnimation();
					getGameMapPanel().fireConqueredAnimation(at);

					releaseLock();
				}

			}
		};

		dispatchThread(newThread);
	}

	public static void clearSelectedTerritory(String territory) {
		getGameMapPanel().clearTerritory(territory);
	}

	public static void clearSelectedTerritory(Territory t) {
		clearSelectedTerritory(t.getName().toLowerCase());
	}

	public static void clearAllSelectedTerritories() {
		getGameMapPanel().clearAllTerritories();

	}

	private static void releaseLock() {
		animationLock.notify();
		waiting = false;
	}

	public static void dispatchThread(Runnable r) {
		Thread t = new Thread(r);
		t.start();
	}

	private void initialize() {
		// Position in center of screen
		setLocationRelativeTo(null);
		setLocation(getX() - 502, 0); // 512 = 1024 / 2

		setVisible(true);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initializeGameMapPanel();

	}

	/**
	 * Sets the selection policy
	 * 
	 * @param policy
	 *            - SELECT_FOR_ATTACKS; SELECT_FOR_FORTIFY ; SELECT_FOR_PREGAME;
	 *            SELECT_FOR_REINFORCEMENTS; SELECT_NONE ;
	 */
	public static void setSelectionPolicy(int policy) {
		GameMapPanel m = getGameMapPanel();
		m.setSelectionPolicy(policy);

	}

	/**
	 * Tests this module
	 */

	public static void main(String[] args) {

		RiskFrame frame = new RiskFrame(new Game());

		frame.setVisible(true);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		int[] d1 = { 3, 3, 3 };
		int[] d2 = { 4, 4 };
		DiceRoll a = new DiceRoll(d1, d2);

		frame.fireDiceRollAnimation(a);

		initializeGameMapPanel();

		// testAttacks();

		testReinforcements();
	}

	public static void testAttacks() {
		Field[] fields = Territories.class.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Territory t = null;
			Territory t2 = null;
			Field fl = fields[i];
			try {
				t = new Territory(null, fl.getInt(new Territories()));
				if (i + 1 < fields.length) {
					Field f2 = fields[i + 1];
					t2 = new Territory(null, f2.getInt(new Territories()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (t2 != null) {
				Attack a = new Attack(t, t2);
				RiskFrame.fireAttackAnimation(a);
			}

		}
	}

	public static void testReinforcements() {
		Field[] fields = Territories.class.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Territory t = null;
			Field fl = fields[i];
			try {
				t = new Territory(null, fl.getInt(new Territories()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			Reinforcement reinforce = new Reinforcement(t, 0);
			RiskFrame.fireReinforcementAnimation(reinforce);

		}
	}

	public static long getMillisLengthAnimation() {
		return getGameMapPanel().getMillisLengthForAnimation();
	}

	public static void fireConqueredContinentAnimation(Continent conquered) {

		getGameMapPanel().fireConqueredContinentAnimation(conquered);

	}
}
