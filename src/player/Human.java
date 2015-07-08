package player;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import base.Game;

import view.FortifyDialog;
import view.RiskFrame;

import map.Map;
import map.Territory;

/**
 * An class that handles behaviors of a human player
 * 
 * @author Ross
 * @extends Player
 */
public class Human extends Player implements ActionListener, Serializable {

	private static final long serialVersionUID = 8496201026901054349L;
	private boolean skipStage;
	private int numFortify;

	/**
	 * Instantializes a new human player
	 * 
	 * @param name
	 *            , the name of this player
	 * @param color
	 *            , the player's color
	 * @param map
	 *            , a reference to the map instance
	 */
	public Human(String name, Color color, Map map) {
		super(name, color, map);
		skipStage = false;
	}

	/**
	 * Returns a point where the user has clicked on the map and figures out
	 * where they are clicking on, and whether or not it is a valid move
	 * 
	 * @return the Point where the player clicked
	 */
	private Point getCoordinates() {
		// TODO
		return new Point(0, 0);
	}

	/**
	 * Waits for a player to click on a defending and an attacking country and
	 * creates an attack object out of that information
	 * 
	 * @return an Attack object that knows all relevant information about the
	 *         attack
	 */
	public Attack decideAttack() {
		RiskFrame.clearAllSelectedTerritories();
		while ((RiskFrame.getSelectedTerritories()[0] == null || RiskFrame
				.getSelectedTerritories()[1] == null)
				&& !skipStage) {

				if (RiskFrame.getSelectedTerritories()[0] == null
						|| RiskFrame.getSelectedTerritories()[0]
								.getTroopsOnTerritory() == 1) {
					RiskFrame.clearAllSelectedTerritories();
				}
			Thread.yield();// while two valid territories are not selected
		}

		Attack attack = new Attack(RiskFrame.getSelectedTerritories()[0],
				RiskFrame.getSelectedTerritories()[1]);
		RiskFrame.clearAllSelectedTerritories();

		if (skipStage) {
			skipStage = false;
			return null;
		}

		return attack;
	}

	private boolean areTwoTerritoriesSelected() {
		return !(RiskFrame.getSelectedTerritories()[0] == null || RiskFrame
				.getSelectedTerritories()[1] == null);
	}

	private boolean playerOwnsTerritories() {
		if (RiskFrame.getSelectedTerritories()[0] != null
				&& RiskFrame.getSelectedTerritories()[0].getPlayer() instanceof Human) {
			return RiskFrame.getSelectedTerritories()[0].getPlayer().equals(
					this)
					&& RiskFrame.getSelectedTerritories()[1].getPlayer()
							.equals(this);
		} else if (RiskFrame.getSelectedTerritories()[0] != null) {
			return RiskFrame.getSelectedTerritories()[0].getPlayer().equals(
					this);
		} else {
			return true;
		}
	}

	/**
	 * Waits for a player to click on a country to move units from and a country
	 * to move units to, and creates a fortify object out of that information
	 * 
	 * @return a Fortify object that knows all relevant information about the
	 *         fortify move
	 */
	public Fortify decideFortify() {
		RiskFrame.clearAllSelectedTerritories();
		while ((!areTwoTerritoriesSelected()
				|| !playerOwnsTerritories()
				|| !map.areAdjacent(RiskFrame.getSelectedTerritories()[0],
						RiskFrame.getSelectedTerritories()[1]) || RiskFrame
				.getSelectedTerritories()[0].equals(RiskFrame
				.getSelectedTerritories()[1]))
				&& !skipStage) {
			Territory selected0 = RiskFrame.getSelectedTerritories()[0];
			Territory selected1 = RiskFrame.getSelectedTerritories()[1];
			if (selected0 != null && selected1 != null) {
				if (!map.areAdjacent(selected0, selected1)
						|| selected0.equals(selected1)
						|| !selected1.getPlayer().equals(this)) {
					RiskFrame.clearAllSelectedTerritories();
				}
			} else if (selected0 != null) {
				if (!selected0.getPlayer().equals(this)
						|| selected0.getTroopsOnTerritory() == 1) {
					RiskFrame.clearAllSelectedTerritories();
				}
			}
			Thread.yield();// while two territories are not selected
		}

		if (skipStage) {
			skipStage = false;
			return null;
		}

		Territory territory1 = RiskFrame.getSelectedTerritories()[0];
		Territory territory2 = RiskFrame.getSelectedTerritories()[1];

		boolean isFortifyAfterAttack = false;

		FortifyDialog fortifyDialog = new FortifyDialog(this, territory1,
				territory2, isFortifyAfterAttack, null);

		while (fortifyDialog.isDisplayable()) {
			Thread.yield();
		}
		RiskFrame.clearAllSelectedTerritories();
		return new Fortify(territory1, territory2, numFortify);
	}

	/**
	 * Waits for a player to click on a country that it wants to reinforce, and
	 * creates a Reinforcement object out of that information
	 * 
	 * @return a Reinforcement object that knows all relevant information about
	 *         the reinforcement move
	 */
	public Reinforcement decideReinforcement() {
		RiskFrame.clearAllSelectedTerritories();

		RiskFrame.setSelectionPolicy(0);
		Territory ref = RiskFrame.getSelectedTerritories()[0];
		RiskFrame.clearAllSelectedTerritories();
		RiskFrame.setSelectionPolicy(0);
		while (ref == null && Game.getLoadedGame() == null) {
			ref = RiskFrame.getSelectedTerritories()[0];
			Thread.yield();
		}

		if (Game.getLoadedGame() != null) {
			return null;
		}

		Reinforcement reinforcement = new Reinforcement(ref, 1);
		RiskFrame.clearAllSelectedTerritories();
		return reinforcement;
	}

	/**
	 * Forces the player to choose how many troops they want to move from their
	 * attacking territory to the territory they just conquered
	 * 
	 * @param attackingTerritory
	 *            - The territory from which troop will be moved
	 * @param defendingTerritory
	 *            - The territory that troops need to be moved onto
	 * @return the number of troops the player chooses to move onto the
	 *         territory that they just won. Will be greater than or equal to
	 *         the number of dice that they rolled, and less than the number of
	 *         troops on the attacking territory
	 */
	public int decideFortifyAfterAttack(Territory attackingTerritory,
			Territory defendingTerritory) {

		FortifyDialog fortifyDialog = new FortifyDialog(this,
				attackingTerritory, defendingTerritory, true, null);

		while (fortifyDialog.isDisplayable()) {
			Thread.yield();
		}

		return numFortify;
	}

	/**
	 * Skips stage.
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		skipStage = true;
	}

	/**
	 * Sets the forify value to the number specified.
	 * 
	 * @param value
	 *            vale to set fortify.
	 */
	public void setFortifyNum(int value) {
		numFortify = value;
	}

	/**
	 * Gets the territory in the pregame that they player wants to occupy.
	 * 
	 * @deprecated
	 * @return The territory that the player wants to occupy
	 */
	public Territory getPregameTerritory() {
		while (RiskFrame.getSelectedTerritories() != null) {
			return RiskFrame.getSelectedTerritories()[0];
		}
		return null;
	}

}
