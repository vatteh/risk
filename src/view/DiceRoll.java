package view;

import java.util.List;

/**
 * 
 * A view.DiceRoll object is used by DiceRollAnimator.
 * With a dice roll it will know which values to output
 * when the map fires an animation sequence.
 * 
 * 
 * @author Phil
 *
 */
public class DiceRoll {

	private int[] attackDice;
	
	private int[] defendDice;
	
	/**
	 * 
	 * Should specify the dice roll results with
	 * integer arrays whose lengths correspond to 
	 * the number of dice rolled for the defender or for the
	 * attacker. The values of these integers should 
	 * be from 1-6.
	 * 
	 * @param attackDiceResult - The attacker's dice rolls
	 * @param defendDiceResult - The defender's dice rolls
	 */
	public DiceRoll(int[] attackDiceResult, int[] defendDiceResult){
		this.attackDice = attackDiceResult;
		this.defendDice = defendDiceResult;
	}
	
	/**
	 * Constructor for a DiceRoll.  Takes in the attacker and defender's dice.
	 * 
	 * @param attackDice
	 * 			the attackers dice.
	 * @param defendDice
	 * 			the defenders dice.
	 */
	public DiceRoll(List<Integer> attackDice, List<Integer> defendDice){
		int[] r1 = new int[attackDice.size()];
		int[] r2 = new int[defendDice.size()];
		for(int i = 0 ; i<attackDice.size();i++){
			r1[i] = attackDice.get(i);
		}
		for(int i = 0 ; i<defendDice.size();i++){
			r2[i] = defendDice.get(i);
		}
		this.attackDice = r1;
		this.defendDice = r2;
	}
	
	/**
	 * Returns the attackers dice roll result.
	 * 
	 * @return the set of dice results for the attacker
	 */
	public int[] getAttackerResult(){
		return attackDice;
	}
	
	/**
	 * Returns the defenders dice roll result.
	 * 
	 * @return the set of dice results for the defender
	 */
	public int[] getDefenderResult(){
		return defendDice;
	}
	
	/**
	 * A utility method to generate a random integer
	 * 
	 * @param min - the minimum inclusive
	 * @param max - the maximum inclusive
	 * @return - a random integer
	 */
	public static long generateRandomNumber(int min, int max){
		int gap = max-min;
		
		return Math.round((Math.random() * gap)+min);
		
	}
	
	public static void main(String[] args){
		for(int i = 0; i<200; i++){
			System.out.println(generateRandomNumber(0,-1));
		}
	}
	
}
