package base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

import javax.swing.JOptionPane;

import map.Continent;
import map.Map;
import map.Territory;
import player.Attack;
import player.Fortify;
import player.Human;
import player.Player;
import player.Reinforcement;
import player.Strategy;
import soundscapes.SoundEffects;
import view.ConsoleUpdate;
import view.DiceRoll;
import view.GameStatsFrame;
import view.GifDialog;
import view.PreGameInputDialog;
import view.RiskFrame;
import view.Statistics;
import view.StatisticsUpdate;
import view.TitleSplashScreenWindow;

/**
 * Will Control the Risk game. Has methods to manipulate troops on territories, check cards
 * change stages and players.
 * 
 * @author Victor
 *
 */

public class Game extends Observable implements Serializable{

	private static final long serialVersionUID = 2806486479318057613L;
	private Stage currentStage;
	private Player currentPlayer;
	private Map map;
	private List<Player> playerList;
	private int turnInValue;  
	private Deck deck;
	private GameSettings gameSettings;
	private int round;
	private boolean choose;
	private static Statistics gameStats;
	public static long startTime;
	public static double finishTime;
	public static boolean doPreGame = true;
	private static Game loadedGame;
	private Game tempGame;
	
	/**
	 * Construct the game with default values
	 * 
	 * @param players - a list of players playing the game
	 */
	public Game() {
		playerList = new ArrayList<Player>();
		currentStage = Stage.PREGAME;
		turnInValue = 4;
		map = Map.getInstance();
		
		deck = new Deck();
		gameSettings = new GameSettings();
		round = 1;
		
		startTime = startTiming();
		loadedGame = null;
		tempGame = null;
	}
	
	public Game getTempGame() {
		return tempGame;
	}
	
	/**
	 * When the RiskMenuBar loads the game memory, setLoadedGame will be called to set the 
	 * game for all game instances to share. This will retrieve the instance when needed.
	 * 
	 * @return the game instance loaded from the open dialog and set in the RiskFrame
	 */
	public static Game getLoadedGame() {
		return loadedGame;
	}
	
	/**
	 * When the RiskMenuBar loads the game memory, setLoadedGame will be called to set the 
	 * game for all game instances to share.
	 * 
	 * @param game - the game just loaded from the RiskMenuBar
	 */
	public static void setLoadedGame(Game game) {
		loadedGame = game;
	}
	
	/**
	 * Starts when the game is created and will keep track of the current game.
	 * 
	 * @return the start of the current game.
	 */
	private static long startTiming() {
		System.gc();
		return System.currentTimeMillis();
	}
	
	/**
	 * Called at the end of the game to be displayed in the stats.
	 * 
	 * @param startingTime- the time the game started
	 * @return the length of the the game
	 */
	private static double stopTiming(long startingTime) {
		long elapsedTime = System.currentTimeMillis() - startingTime;
		return elapsedTime / 1000.0;
	}
	
	/**
	 * 
	 * @param gameStats - sets the games stats
	 */
	public void associateStats(Statistics gameStats) {
		this.gameStats = gameStats;
	}
	
	/**
	 * 
	 * @return the game current stats
	 */
	public Statistics getGameStats() {
		return gameStats;
	}
	
	/**
	 * The pregame dialog will set this to let the game decide whether the human player wants 
	 * to choose initial territories. 
	 * 
	 * @param choose - whether to choose-true or not-false 
	 */
	public void setChoose(boolean choose) {
		this.choose = choose;
	}
	
	/**
	 * Used in main to decide whether to choose initial territories or ramdomize
	 * @return true for choose, false for random
	 */
	public boolean getChoose() {
		return choose;
	}
	
	/**
	 * The pregame dialog will make the players and give them to the game instance with this 
	 * method
	 * 
	 * @param players - the players playing the game
	 */
	public void givePlayers(List<Player> players) {
		this.playerList = players;
		currentPlayer = playerList.get(0);
	}
	
	/**
	 * Returns the instance that stores the current game settings.
	 * 
	 * @return the game settings for the game
	 */ 
	public GameSettings getGameSettings() {
		return gameSettings;
	}
	
	/**
	 * Returns all players playing the game
	 * 
	 * @return - the human players playing the game.
	 */
	public List<Player> getAllPlayers() {
		return playerList;
	}
	
	/**
	 * Returns the current round which is the amount of times each player has gone through the stages.
	 * 
	 * @return the current round
	 */
	public int getRound() {
		return round;
	}
	
	/**
	 * Returns the current player
	 * 
	 * @return - the current player 
	 */
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	/**
	 * Returns the map being used by the game
	 * 
	 * @return - Returns the current map
	 */
	public Map getMap() {
		return map;
	}
	
	/**
	 * Returns the troops left the player has to drop on the board
	 * 
	 * @return the Troops left the current player has to give
	 */
    public int getTroopsToGive() {
		return this.currentPlayer.getTroopsToGive();
	}
	
	/**
	 * Returns the current stage
	 * 
	 * @return - the current stage
	 */
	public Stage getCurrentStage() {
		return currentStage;
	}
	
	/**
	 * Used to switch to the next stage. First stage is Pregame, then will
	 * cycle between the main three.
	 */
	private void nextStage(){
		if (currentStage == Stage.PREGAME) {
			currentStage = Stage.REINFORCEMENTS;
		}
		else if (currentStage == Stage.REINFORCEMENTS) {
			currentStage = Stage.ATTACK;
		}
		else if (currentStage == Stage.ATTACK) {
			currentStage = Stage.FORTIFY;
		}
		else {
			currentStage = Stage.REINFORCEMENTS;
		}
		this.setChanged();
		this.notifyObservers(ConsoleUpdate.NEW_STAGE);
		
		// To notify the card panel
		this.setChanged();
		this.notifyObservers(currentPlayer);
	}
	
	/**
	 * Switches the current player to the next player to the next player on the list.
	 */
	private void setNextPlayer() {
		
		int nextIndex = (playerList.indexOf(currentPlayer) + 1);
		
		if (nextIndex < playerList.size()){
			currentPlayer = playerList.get(nextIndex);
		}
		else {
			currentPlayer = playerList.get(0);
			if (this.getCurrentStage() != Stage.PREGAME) {
				round++;
			}
		}
		
		// To notify the console
		this.setChanged();
		this.notifyObservers(ConsoleUpdate.NEW_PLAYER);
		
		// To notify the card panel
		this.setChanged();
		this.notifyObservers(currentPlayer);
	}
	
	/**
	 * Will determine whether the three given cards are a match. Using three cases:
	 * 1. If all three are the same
	 * 2. If all three are different - will work if only one is a wild card.
	 * 3. If two are wild cards - the third could be any card.
	 * 4. If two cards are the same and one is a wild.
	 * 
	 * @param card1
	 * @param card2
	 * @param card3
	 * @return - whether the three cards are a match
	 */
	public boolean tradeCards(Card card1, Card card2, Card card3) {
		// Check parameters
		if (card1 == null || card2 == null || card3 == null) {
			this.setChanged();
			this.notifyObservers(ConsoleUpdate.BAD_TRADE);
			return false;
		}
		// Case 1
		if (card2.getCardType() == card1.getCardType() && card3.getCardType() == card1.getCardType()) {

			currentPlayer.removeCard(card1);
			currentPlayer.removeCard(card2);
			currentPlayer.removeCard(card3);
			this.currentPlayer.incrementTroopsToGive(turnInValue);
			increaseTurnInValue();
			cardBonuses(card1,card2,card3);
			
			if (gameSettings.getPlayAnimations()) {
				new GifDialog("animations/gifs/cards.gif");
			}
			return true;
		}
		// Case 2
		else if ((card1.getCardType() != card2.getCardType()) && (card1.getCardType() != card3.getCardType()) && (card2.getCardType() != card3.getCardType())) {
			
			currentPlayer.removeCard(card1);
			currentPlayer.removeCard(card2);
			currentPlayer.removeCard(card3);
			this.currentPlayer.incrementTroopsToGive(turnInValue);
			increaseTurnInValue();
			cardBonuses(card1,card2,card3);
			
			if (gameSettings.getPlayAnimations()) {
				new GifDialog("animations/gifs/cards.gif");
			}
			return true;
		}
		
		// Case 3
		boolean card1wild = (card1.getCardType() == CardType.WILD);
		boolean card2wild = (card2.getCardType() == CardType.WILD);
		boolean card3wild = (card3.getCardType() == CardType.WILD);
		
		if ((card1wild && card2wild) || (card1wild && card3wild) || (card2wild && card3wild)) {
			
			currentPlayer.removeCard(card1);
			currentPlayer.removeCard(card2);
			currentPlayer.removeCard(card3);
			this.currentPlayer.incrementTroopsToGive(turnInValue);
			increaseTurnInValue();
			cardBonuses(card1,card2,card3);
			
			if (gameSettings.getPlayAnimations()) {
				new GifDialog("animations/gifs/cards.gif");
			}
			
			return true;	
		}
		// Case 4
		else if ((card1wild && card2.getCardType() == card3.getCardType()) || (card2wild && card1.getCardType() == card3.getCardType()) || (card3wild && card2.getCardType() == card1.getCardType())){
			
			currentPlayer.removeCard(card1);
			currentPlayer.removeCard(card2);
			currentPlayer.removeCard(card3);
			this.currentPlayer.incrementTroopsToGive(turnInValue);
			increaseTurnInValue();
			cardBonuses(card1,card2,card3);
			
			if (gameSettings.getPlayAnimations()) {
				new GifDialog("animations/gifs/cards.gif");
			}
			
			return true;
		}
		else {
			this.setChanged();
			this.notifyObservers(ConsoleUpdate.BAD_TRADE);
			return false;
		}
		
	}
	
	/**
	 * After a successful turn-in of cards, not only will the player get the current turn in-value of
	 * the set, but the player will get two additional armies for every card they turn in that has a 
	 * territory on it they currently own.
	 * 
	 * @param card1
	 * @param card2
	 * @param card3
	 * @return the amount if any additional troops they get.
	 */
	private void cardBonuses(Card card1,Card card2,Card card3) {
		
		for(Territory territory: map.getFriendlyTerritories(currentPlayer)) {
			if (territory.getName().equals(card1.getTerritoryType().toString()) || territory.getName().equals(card2.getTerritoryType().toString()) || territory.getName().equals(card3.getTerritoryType().toString())) {
				territory.incrementTroops(2);
				this.setChanged();
				this.notifyObservers(ConsoleUpdate.TERRITORY_BONUS);
			}
		}	
	}
	
	/**
	 * Returns the current turnInValue for a card set.
	 * 
	 * @return the current turn in value
	 */
	public int getTurnInValue() {
		return turnInValue;
	}

	/**
	 * Increases the turn in value of the cards:
	 * goes from 4,6,8,10,12,15,20,25,30.....
	 */
	private void increaseTurnInValue() {
		this.setChanged();
		this.notifyObservers(ConsoleUpdate.GOOD_TRADE);
		
		if (turnInValue < 12) {
			turnInValue += 2; 
		}
		
		else if (turnInValue == 12) {
			turnInValue += 3;
		}
		
		else {
			turnInValue += 5;
		}
		
		this.setChanged();
		this.notifyObservers(ConsoleUpdate.UPDATE_STATS);
	}
	
	/**
	 * Calculates the number of troops to give this player for reinforcements and adds that to 
	 * the variable troopsToGive in the Player instance.
	 * @param toGive - the player to give troops
	 */
	private void troopsToGiveThisTurn(Player toGive) {
		if (toGive == null) {
			return;
		}
		
		int territoriesOwned = 0;
		List<Continent> contintents = map.getContinents(); 
		
		// Go through each territory and find how many belong to this player 
		for (Continent continent: contintents) {
			List<Territory> territories = continent.getTerritoryList();
			
			for (Territory territory: territories) {
				if (territory.getPlayer() == toGive)
					territoriesOwned++;
			}
		}
		
		// Divide by 3
		int troopsGet = territoriesOwned/3;
		
		// If less than 3; troops get will be three
		if (troopsGet < 3) {
			troopsGet = 3;
		}
		
		// Look for continent bonuses
		for (Continent continent: contintents) {
			if (continent.getConqueredBy() == toGive) {
				troopsGet += continent.getValue();
			}
		}
		
		this.currentPlayer.incrementTroopsToGive(troopsGet);
		this.setChanged();
		this.notifyObservers(ConsoleUpdate.TROOPS_THIS_TURN);
		
	}
	
	/**
	 * Returns a random number between 1 and 6
	 * @return - a number between 1 and 6
	 */
	private int diceRoll() {
		return (int)((Math.random() * 6) + 1); 
	}
	
	/**
	 * When a player has been given their troops to place, this will be called to allow the player to 
	 * place them on the board one by one
	 */
	private void calculateReinforcement(){
		
		setChanged();
		notifyObservers(ConsoleUpdate.PROGRESS_START);
		
		RiskFrame.setSelectionPolicy(1);
		
		// Put Reinforcements on the board
		while (currentPlayer.getTroopsToGive() > 0) {
			
			// Get where the player wants to add one troop to the territory
			Reinforcement toPlace = currentPlayer.decideReinforcement();
			
			if (toPlace == null && Game.getLoadedGame() != null) {
				break;
			}
			
			if (toPlace != null && toPlace.getTerritory() != null) {
				if (toPlace.getTerritory().getPlayer() == currentPlayer) {
				
				
				toPlace.getTerritory().incrementTroops(1);
				currentPlayer.incrementTroopsToGive(-1);
					
				// Notify the console to display the territory name
				setChanged();
				notifyObservers(toPlace.getTerritory());
				
				// Notify the progress bar to decrement
				setChanged();
				notifyObservers(ConsoleUpdate.PROGRESS_BAR_UPDATE);
				
				// Notify End Game Stats
				setChanged();
				notifyObservers(StatisticsUpdate.TROOP_BORN);
				
				if (gameSettings.getPlaySounds()) {
					SoundEffects.playMarchingSound();
				}
				
				if (gameSettings.getPlayAnimations()) {
					RiskFrame.fireReinforcementAnimation(toPlace);
					pause((int)RiskFrame.getMillisLengthAnimation());
				}
				
				RiskFrame.updateMapPanel(map.getTerritories());
			
				}else {
					System.out.println("The Player had an enemy territory in thier Reinforcement instance.");
				}
			}
			else {
				System.out.println("The Player retruned a null Reinforcement instance.");
			}	
		}
		
		if (currentStage != Stage.REINFORCEMENTS)
			RiskFrame.setSelectionPolicy(2);
	}
	
	/**
	 * Calculates the attack and changes the map by decrementing troops.
	 * Is called to represent one roll.
	 * 
	 * @param attack - the attack instance holding all the info
	 * @return - whether the attack was valid and done.
	 */
	private boolean calculateAttack(Attack attack){
		
		// Check parameters
		if (attack == null || attack.getAttackingTerritory() == null || attack.getDefendingTerritory() == null) {
			return false;
		}
		// 
		else if (attack.getAttackingTerritory().getPlayer() != currentPlayer || attack.getDefendingTerritory().getPlayer() == currentPlayer) {
			System.out.println("Attack - Attacking/Defending Territory not belonging to the right player!");
			return false;
		}
		// Check in the two territories belong to the same player
		else if (attack.getAttackingTerritory().getPlayer() == attack.getDefendingTerritory().getPlayer()) {
			System.out.println("Attack - Territories owned by same person!");
			return false;
		}
		// Check if the the two territories are adjacent
		else if (!map.areAdjacent(attack.getAttackingTerritory(),attack.getDefendingTerritory())) {
			System.out.println("Attack - Territories are not adjacent!");
			return false;
		}
		// Check if the attacking territory less more than 1 troop
		else if (attack.getAttackingTerritory().getTroopsOnTerritory() < 2) {
			System.out.println("Attack - Attacking territory has less than 2 troops!");
			return false;
		}
		
		
		// If the default dice to roll for the attacking territory is invalid, it will automatically change
		int attackDiceToRoll = attack.getAttackingTerritory().getPlayer().getNumAttackingDice();
		if ((attackDiceToRoll > 3) || (attackDiceToRoll < 1) || (attackDiceToRoll >= attack.getAttackingTerritory().getTroopsOnTerritory())) {
			if (attack.getAttackingTerritory().getTroopsOnTerritory() - 1 < 4) {
				attackDiceToRoll = attack.getAttackingTerritory().getTroopsOnTerritory() - 1;
			}
			else {
				attackDiceToRoll = 3;
			}
		}
		
		// If the default dice to roll for the defending territory is invalid, it will automatically change
		int defendDiceToRoll = attack.getDefendingTerritory().getPlayer().getNumDefendingDice();
		if ((defendDiceToRoll > 2) || (defendDiceToRoll < 1) || (defendDiceToRoll > attack.getDefendingTerritory().getTroopsOnTerritory())) {
			if (attack.getDefendingTerritory().getTroopsOnTerritory() > 1) {
				defendDiceToRoll = 2;
			}
			else {
				defendDiceToRoll = 1;
			}
		}
		
		// Makes attack dice
		ArrayList<Integer> attackRolls = new ArrayList<Integer>();
		for (int i = 0; i < attackDiceToRoll; i++) {
			attackRolls.add(diceRoll());
		}
		
		// Makes defend dice
		ArrayList<Integer> defendRolls = new ArrayList<Integer>();
		for (int i = 0; i < defendDiceToRoll; i++) {
			defendRolls.add(diceRoll());
		}
		
		// Sort by descending order
		Collections.sort(attackRolls);
		Collections.sort(defendRolls);
		Collections.reverse(attackRolls);
		Collections.reverse(defendRolls);
		
		if(gameSettings.getPlayAnimations()) {
			RiskFrame.fireDiceRollAnimation(new DiceRoll(attackRolls,defendRolls));
		}
		
		Integer attackLost = 0;
		Integer defendLost = 0;
		
		// Compare the dice and decrement
		int index = 0;
		while ( index < attackRolls.size() && index < defendRolls.size() && attackRolls.get(index) != null && defendRolls.get(index) != null
				&& attack.getAttackingTerritory().getTroopsOnTerritory() != 1 && attack.getDefendingTerritory().getTroopsOnTerritory() != 0) {
			
			if (defendRolls.get(index) >= attackRolls.get(index)) {
				attack.getAttackingTerritory().incrementTroops(-1);
				attackLost++;
			}
			else {
				attack.getDefendingTerritory().incrementTroops(-1);
				defendLost++;
			}
			
			setChanged();
			notifyObservers(StatisticsUpdate.TROOP_DEADED);
			
			index++;
			RiskFrame.updateMapPanel(map.getTerritories());
		}
		
		attack.getAttackingTerritory().incrementTimesAttacked();
		attack.getDefendingTerritory().incrementTimesDefended();
		
		this.setChanged();
		this.notifyObservers(attack);
		
		List<Integer> lostResult = new ArrayList<Integer>();
		lostResult.add(attackLost);
		lostResult.add(defendLost);
		
		this.setChanged();
		this.notifyObservers(lostResult);
		
		this.setChanged();
		this.notifyObservers(StatisticsUpdate.ATTACKING);
		
		this.setChanged();
		this.notifyObservers(StatisticsUpdate.TERRITORY_ATTACKED);
		
		// Play take over sound or attackSound 
		if (attack.getDefendingTerritory().getTroopsOnTerritory() == 0) {
			if (gameSettings.getPlaySounds()) {
				SoundEffects.playTakeoverSound();
			}
		}
		else {
			if(gameSettings.getPlaySounds()) {
				SoundEffects.playAttackSound();
			}
		}
		
		if (gameSettings.getPlayAnimations()) {
			RiskFrame.fireAttackAnimation(attack);
			this.pause((int)(RiskFrame.getMillisLengthAnimation()));
		}

		return true;
	}
	
	/**
	 * Will Calculate the fortify and move troops accordingly.
	 * 
	 * @param fortify - the Fortify instance which will hold all information
	 * @return - whether this was a valid fortify move
	 */
	private boolean calculateFortify(Fortify fortify) {
		
		// Check if values are false
		if (fortify == null || fortify.getFrom() == null || fortify.getTo() == null) {
			return false;
		}
		// Check if the two are adjacent
		else if (!map.areAdjacent(fortify.getFrom(),fortify.getTo())) {
			return false;
		}
		// Check if the two are not owned by the same player
		else if (fortify.getFrom().getPlayer() != fortify.getTo().getPlayer()) {
			System.out.println("Fortify - Territories owned by different players!");
			return false;
		}
		// Check if the amount is too many 
		else if (fortify.getAmount() >= fortify.getFrom().getTroopsOnTerritory()) {
			System.out.println("Fortify - Want to move too many troops");
			return false;
		}
		// Check if the player does not want to fortify
		else if (fortify.getAmount() < 1) {
			System.out.println("Fortify - Must not want to fortify");
			return false;
		}
		else if (fortify.getFrom().getPlayer() != this.currentPlayer) {
			System.out.println("Fortify - Territory does not belong to you!");
			return false;
		}
		
		// Increment accordingly
		fortify.getTo().incrementTroops(fortify.getAmount());
		fortify.getFrom().incrementTroops(-fortify.getAmount());
		
		RiskFrame.updateMapPanel(map.getTerritories());
		
		if (currentStage != Stage.ATTACK && gameSettings.getPlayAnimations()) {
			RiskFrame.fireFortifyAnimation(fortify);
			pause((int)(RiskFrame.getMillisLengthAnimation()));
		}
		
		this.setChanged();
		this.notifyObservers(fortify);
		
		return true;
	}
	
	/**
	 * Will pause the game to prevent super-fast AI's
	 * @param millis - the amount to pause;
	 */
	public void pause(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Will find the player that owns all continents or null if nobody does. 
	 * 
	 * @return The player that currently owns all territories
	 */
	public Player getWinner() {
		Player toCompare = map.getContinents().get(0).getConqueredBy();
		
		if (toCompare == null) {
			return null;
		}
		
		for (Continent continent: map.getContinents()) {
			if (continent.getConqueredBy() != toCompare) {
				return null;
			}
		}
		
		return toCompare;
	}
	
	/**
	 * if getChoose is false, this will be called to randomize the board
	 */
	private void randomizeBoard() {
		List<Territory> territories = map.getTerritories();
		Collections.shuffle(territories);
		
		for (Territory territory : territories) {
			territory.setPlayer(currentPlayer);
			currentPlayer.incrementTroopsToGive(-1);
			territory.incrementTroops(1);
			
			setChanged();
			notifyObservers(StatisticsUpdate.TROOP_BORN);
			
			setNextPlayer();
		}
	}
	
	/**
	 * If the human player has 5 or more cards, this will be called to prompt the player to make a trade
	 */
	private void promptCards() {
		JOptionPane.showMessageDialog(null,"You have 5 or more cards. You must make a trade!","Warning", JOptionPane.WARNING_MESSAGE);
		while (currentPlayer.getCards().size() >= 5) {
			Thread.yield();
		}
	}

	// TODO Main
	public static void main(String [] args) {
			
		// Start SplashScreen
		TitleSplashScreenWindow titleSplash = new TitleSplashScreenWindow();
		titleSplash.showSplashScreen(5);
		
		while (titleSplash.isDisplayable()) {
			Thread.yield();			
		}
		
		Game game = new Game();
		
		new SoundEffects();
		
		// Pregame Dialog
		PreGameInputDialog dialog = new PreGameInputDialog(game);
			
		while (dialog.isDisplayable()) {
			Thread.yield();			
		}
			
		new RiskFrame(game);
		Statistics gameStats = new Statistics(game);			
		game.associateStats(gameStats);
		
		game.doPreGame();
				
		RiskFrame.updateMapPanel(game.map.getTerritories());

		while(game.getWinner() == null) {
			
			/*
			 *  Reinforcement Stage
			 *  
			 *  First ask the AI if they want to trade cards. In the case of the human player if they have over 5 
			 *  cards, we wait until they make a trade.
			 *  Then troops are added to the territories from their Reinforcement instance until their troopsToGive 
			 *  variable is 0.
			 */
			
			// prompt AI if they want to turn in cards or the Human player if he has more than 5.			
			if (!(game.currentPlayer instanceof Human)) {
				if (((Strategy)game.currentPlayer).wantToTrade()) {
					List<Card> toTurnIn = ((Strategy)game.currentPlayer).getTradeCards();
						
					if (game.tradeCards(toTurnIn.get(0), toTurnIn.get(1), toTurnIn.get(2))) {
						game.deck.addTradedCards(toTurnIn.get(0), toTurnIn.get(1), toTurnIn.get(2));
					}
				}
			}
			else {
				if (game.currentPlayer.getCards().size() >= 5 && game.currentPlayer instanceof Human) {
					game.promptCards();
				}
			}

			// Give the current player their troops and set the progress bar to its max.
			game.troopsToGiveThisTurn(game.currentPlayer);
			
			// Allows the current player to place troops in the map. 
			game.calculateReinforcement();
			
			if (Game.getLoadedGame() != null && Game.getLoadedGame().getCurrentStage() == Stage.REINFORCEMENTS) {
				game = Game.getLoadedGame();
					
				for (int i = 0; i < RiskFrame.getFrames().length; i++) {
					RiskFrame.getFrames()[i].dispose();
				}
			
				new RiskFrame(game);
				
				Game.setLoadedGame(null);
				RiskFrame.updateMapPanel(game.map.getTerritories());
				game.calculateReinforcement();
			}

			// One last time to set the bar to 0
			game.setChanged();
			game.notifyObservers(ConsoleUpdate.PROGRESS_BAR_UPDATE);
			
			game.nextStage();
				
			/*
			 * Attack Stage
			 * 
			 * Will iterate through this while loop until the attacking territory has but one army left 
			 * or the player does not want to attack any more.
			 */
			
			//TODO Attack
			
			RiskFrame.updateMapPanel(game.map.getTerritories());
			
			boolean rollAgain = true;
			boolean needToGiveCard = false;
			
			// If Human, display in the console to choose a territory to attack from.
			if (game.currentPlayer instanceof Human) {
				game.setChanged();
				game.notifyObservers(ConsoleUpdate.STATE_ATTACK);
			}
			
			/*
			 * The player will choose an initial attack. If null, we assume the player does not want to attack and we move to
			 * fortify. After we calculated the attack if the defending player was defeated, we take thier cards and territory
			 * 
			 */
			
			RiskFrame.setSelectionPolicy(2);
			Attack attack = game.currentPlayer.decideAttack();
			
			while (rollAgain) {
				
			// The attacking player's turn is over if we could not calculate the attack or the territory now has only one troop
				if(attack == null || !game.map.canAttack(game.currentPlayer)) {
					rollAgain = false;
				}
			// Wrong parameters 
				else if (!game.calculateAttack(attack)) {
					attack = game.currentPlayer.decideAttack();
				}
			// Prompt the user to fortify
				else if (attack.getDefendingTerritory().getTroopsOnTerritory() == 0) {
					
					if (game.currentPlayer instanceof Human) {
						game.setChanged();							
						game.notifyObservers(StatisticsUpdate.BATTLE_WON);
					}
					
					// No troops on the defending territory means it now belongs to a new player
					Player enemy = attack.getDefendingTerritory().getPlayer();
					attack.getDefendingTerritory().setPlayer(game.currentPlayer);
					
					if (game.gameSettings.getPlayAnimations()) {
						RiskFrame.fireConqueredAnimation(attack.getDefendingTerritory());
						if (attack.getDefendingTerritory().getContinentIn().getConqueredBy() == game.currentPlayer) {
							RiskFrame.fireConqueredContinentAnimation(attack.getDefendingTerritory().getContinentIn());
						}
					}
					
					
					if (game.map.isDefeated(enemy)) {
						if(game.gameSettings.getPlayAnimations()){
							new GifDialog(enemy);
						}
						for (Card card: enemy.getCards())
							game.currentPlayer.giveCard(card);
						
						game.playerList.remove(enemy);
						game.setChanged();
						game.notifyObservers(ConsoleUpdate.PLAYER_DEFEATED);
						game.setChanged();
						game.notifyObservers(game.currentPlayer);
					}
					
					// Now there possibly could be a winner
					if (game.getWinner() == game.currentPlayer)
						break;
					
					RiskFrame.updateMapPanel(game.map.getTerritories());
					int toMove = game.currentPlayer.decideFortifyAfterAttack(attack.getAttackingTerritory(),attack.getDefendingTerritory());
					Fortify fortify = new Fortify(attack.getAttackingTerritory(),attack.getDefendingTerritory(),toMove);
					
					if (!game.calculateFortify(fortify)) {
						System.out.println("Incorrect Troops to Move: " + toMove);
					}
					
					RiskFrame.updateMapPanel(game.map.getTerritories());
					
					// If the current human player has more than 5, they need to trade and place thier
					// given troops on the board immediately.
					if (game.currentPlayer.getCards().size() >= 5 && game.currentPlayer instanceof Human) {
						game.promptCards();
						
						game.setChanged();
						game.notifyObservers(ConsoleUpdate.PROGRESS_START);
						
						game.calculateReinforcement();
						RiskFrame.setSelectionPolicy(2);
					}
					
					// By obtaining a territory, the player will get a card this turn.
					needToGiveCard = true;
					
					attack = game.currentPlayer.decideAttack();
					
				}
			// The Player lost and might not be able to attack again
				else if (attack.getAttackingTerritory().getTroopsOnTerritory() < 2){
					RiskFrame.updateMapPanel(game.map.getTerritories());
					
					if (game.map.canAttack(game.currentPlayer))
						attack = game.currentPlayer.decideAttack();
					else {
						rollAgain = false;
						
						if (game.currentPlayer instanceof Human) {
							game.setChanged();							
							game.notifyObservers(StatisticsUpdate.BATTLE_LOST);
						}
					}
				} 
			// Nether of the cases and the player is a human, ask if they want to roll again
				else if (game.currentPlayer instanceof Human){
					Object[] options = {"Continue the Assault","Forget It","Let's move to the Fortify Stage"};
					int yesNo = JOptionPane.showOptionDialog(null,"Do you want to roll again/continue attacking?",
								"Roll Again",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,
								options,options[0]);
						
					if (yesNo == JOptionPane.NO_OPTION) {
						attack = game.currentPlayer.decideAttack();
						
						game.setChanged();							
						game.notifyObservers(StatisticsUpdate.RETREAT);
					}
					else if (yesNo == JOptionPane.CANCEL_OPTION){
						rollAgain = false;
					}
				}
			// The Player is an AI 
				else {
					attack = game.currentPlayer.decideAttack();
				}
			}
		
			// Now there possibly could be a winner
			if (game.getWinner() == game.currentPlayer)
				break;
				
			// Give the player the next card
			if (needToGiveCard) {
				game.currentPlayer.giveCard(game.deck.getNextCard());
			}
			
			game.nextStage();
			
			/*
			 * Fortify Stage
			 * 
			 * Simple call calculateFortify in game class to move troops
			 */
			
			//TODO Fortify
			RiskFrame.setSelectionPolicy(2);
			RiskFrame.updateMapPanel(game.map.getTerritories());
			
			if (game.currentPlayer instanceof Human) {
				game.setChanged();
				game.notifyObservers(ConsoleUpdate.STATE_FORTIFY);
			}
			
			boolean wantsToMove = true;
			
			Fortify fortify = game.currentPlayer.decideFortify();
			
			while (wantsToMove) {
				if (fortify == null) {
					wantsToMove = false;
				}
				else if (!game.calculateFortify(fortify)) {
					fortify = game.currentPlayer.decideFortify();
				}
				else {
					wantsToMove = false;
				}
			}
			
			game.nextStage();
			game.setNextPlayer();
		}
		
		RiskFrame.updateMapPanel(game.map.getTerritories());
	// Somebody won
		if (game.getWinner() instanceof Human) {
			System.out.println("You Won!");
			
			if (game.gameSettings.getPlayAnimations()) {
				new GifDialog("animations/gifs/youwin.gif");
			}
		}
		else {
			System.out.println("You Lost?");
			if (game.gameSettings.getPlayAnimations()) {
				new GifDialog("animations/gifs/youlose.gif");
			}
		}
		
		finishTime = stopTiming(startTime);
		
		game.setChanged();							
		game.notifyObservers(StatisticsUpdate.BATTLE_WON);
		new GameStatsFrame(gameStats);
	}
	
	// TODO Pre-Game
	private void doPreGame() {
		int count = 0;
		
		// Determine the amount of initial troops according to player size
		if (playerList == null || playerList.size() < 3 || playerList.size() > 6) {
			System.exit(1);
		}
		else if (playerList.size() == 3) {
			count = 35;
		}
		else if (playerList.size() == 4) {
			count = 30;
		}
		else if (playerList.size() == 5) {
			count = 25;
		}
		else if (playerList.size() == 6)
			count = 20;
		
		for (Player player : playerList) {
			player.incrementTroopsToGive(count);
		}
		
		/*
		 * Pre-Game
		 * Players place their initial troops on the board. First, Players will take turns
		 * placing one troop on an unoccupied territory. Once all are filled, then players will 
		 * continue to place their remaining troops on their own territories.If you choose to 
		 * Randomize the board, The board will start occupied territories for all players and then players 
		 * will choose where to place their remaining armies.
		 */

		RiskFrame.setSelectionPolicy(1);
		
		// The player want to choose initial territories
		if (getChoose()) {
			setChanged();
			notifyObservers(ConsoleUpdate.PROGRESS_START);
			
			while (!map.areAllOccupied()) {
				if (getCurrentPlayer() instanceof Human) {
					
					Reinforcement toPlace;
					do {
						toPlace = currentPlayer.decideReinforcement();
					} while(toPlace.getTerritory().getPlayer() != null);
					
					toPlace.getTerritory().setPlayer(currentPlayer);
					toPlace.getTerritory().incrementTroops(1);
					currentPlayer.incrementTroopsToGive(-1);
					RiskFrame.updateMapPanel(map.getTerritories());
			

					setChanged();
					notifyObservers(ConsoleUpdate.PROGRESS_BAR_UPDATE);
					
					setChanged();
					notifyObservers(StatisticsUpdate.TROOP_BORN);
					
					setNextPlayer();
				}
				else {
					Territory toPlace = currentPlayer.getPregameTerritory();
				
					if (toPlace == null)
						System.out.println(currentPlayer.getName() + " :did not return a Territory in pregame/choose!");
					else if (toPlace.getPlayer() == currentPlayer) {
						System.out.println(currentPlayer.getName() + " :not all territories have been taken pregame/choose!");
					}
					else if (toPlace.getPlayer() != currentPlayer && toPlace.getPlayer() != null){
						System.out.println(currentPlayer.getName() + " : picked an enemy territory pregame/choose!");
					}
					else {
						toPlace.setPlayer(currentPlayer);
						toPlace.incrementTroops(1);
						currentPlayer.incrementTroopsToGive(-1);
						RiskFrame.updateMapPanel(map.getTerritories());
									
						setChanged();
						notifyObservers(StatisticsUpdate.TROOP_BORN);
						
						setNextPlayer();
					}
				}
			}
			
			int playersDone = 0;
			
			while(playersDone < playerList.size()) {
				Reinforcement toPlace = currentPlayer.decideReinforcement();
				
				if (toPlace == null || toPlace.getTerritory().getPlayer() != currentPlayer) {
					System.out.println(currentPlayer.getName() + " Returned a null reinforcement or enemy territory");
				}
				else if (currentPlayer.getTroopsToGive() == 0) {
					setNextPlayer();
				}
				else if (toPlace.getTerritory().getPlayer() == currentPlayer && currentPlayer.getTroopsToGive() > 0) {
					toPlace.getTerritory().incrementTroops(1);
					currentPlayer.incrementTroopsToGive(-1);
					
					if (currentPlayer.getTroopsToGive() == 0)
						playersDone++;
					if (currentPlayer instanceof Human) {
						setChanged();
						notifyObservers(ConsoleUpdate.PROGRESS_BAR_UPDATE);
					}
					
					setChanged();
					notifyObservers(StatisticsUpdate.TROOP_BORN);
					
					RiskFrame.updateMapPanel(map.getTerritories());
					setNextPlayer();
					
				}
				
			}
		}
		// The player wants to randomize the board
		else {
			// randomize here
			randomizeBoard();
			RiskFrame.updateMapPanel(map.getTerritories());
			
			int playersDone = 0;
			boolean firstIteration = true;
			while (playersDone < playerList.size()) {
				if (getCurrentPlayer() instanceof Human && firstIteration) {
					setChanged();
					notifyObservers(ConsoleUpdate.PROGRESS_START);
					firstIteration = false;
				}
				
				Reinforcement toPlace = currentPlayer.decideReinforcement();
				
				if (toPlace == null) {
					System.out.println(currentPlayer.getName() + " Returned a null reinforcement or enemy territory - PreGame Random");
				}
				else if (toPlace.getTerritory().getPlayer() != currentPlayer) {
					System.out.println(currentPlayer.getName() + " Returned a reinforcement with an enemy territory - PreGame Random");
				}
				else if (currentPlayer.getTroopsToGive() == 0) {
					setNextPlayer();
				}
				else if (currentPlayer.getTroopsToGive() > 0) {
					toPlace.getTerritory().incrementTroops(1);
					currentPlayer.incrementTroopsToGive(-1);
					
					setChanged();
					notifyObservers(StatisticsUpdate.TROOP_BORN);
					
					// TODO: add RiskFrame.updateTerritoryNumerals and make method in GameMapPanel public?
					List<Territory> updateList = new ArrayList<Territory>();
					updateList.add(toPlace.getTerritory());
					RiskFrame.updateMapPanel(updateList);
					
					if (getCurrentPlayer() instanceof Human) {
						setChanged();
						notifyObservers(ConsoleUpdate.PROGRESS_BAR_UPDATE);
					}
					
					if (currentPlayer.getTroopsToGive() == 0)
						playersDone++;
						
					setNextPlayer();
				}			
			}				
		}
				
		RiskFrame.updateMapPanel(map.getTerritories());
				
		nextStage();
		currentPlayer = getAllPlayers().get(0);
			
	}
}
