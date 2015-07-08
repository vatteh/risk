package player;

import java.io.Serializable;

/**
 * 
 * Types of strategies that can be employed by an AI player.
 * 
 * @author Phil
 *
 */
public enum EvaluationStrategy implements Serializable{

	PRE_GAME, //Meaning: strategy for pregame phase
	BELLIGERENT, //Meaning: prevent opponents from advancing 
	MANIFEST_DESTINY, //Meaning: capture the most advantageous continents and spread thin
	DEFENSE,
	FORTIFICATION
	
}
