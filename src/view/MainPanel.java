package view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import base.Game;

/**
 * The main panel of the interface  It contains all of the other panels.
 * 
 * @author Phil
 *
 */
public class MainPanel extends JPanel {

	static GameMapPanel gameMapPanel;
	static DiceTrayContainer diceTrayContainer;
	static Game theGame;
	static ConsolePanel statsPanel;
	static CardPanel cardPanel;
	private BufferedImage cachedOcean;

	/**
	 * Constructor for the MainPanel, it needs the game to associate with.
	 * 
	 * @param game
	 */
	public MainPanel(Game game) {
		theGame = game;
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		gameMapPanel = new GameMapPanel(game);

		add(gameMapPanel);
		setBackground(gameMapPanel.OCEAN_COLOR);

		diceTrayContainer = new DiceTrayContainer();
		add(diceTrayContainer);

		statsPanel = new ConsolePanel(theGame);

		JPanel southPanel = new SouthPanel();
		southPanel.add(statsPanel);

		JPanel cardPanel = new CardPanel(game);

		southPanel.add(cardPanel);
		southPanel.setMaximumSize(new Dimension(1000, 200));
		add(southPanel);
		
		cachedOcean = ImageReader.readImage("fullbg.png");
		
	}

	/**
	 * Returns the game map panel.
	 * 
	 * @return the game map panel.
	 */
	public GameMapPanel getGameMapPanel() {
		return gameMapPanel;
	}

	/**
	 * Paints the panel.
	 * 
	 */
	public void paintComponent(Graphics g) {
		 drawOcean(g);
	}

	private void drawOcean(Graphics g) {

		g.drawImage(cachedOcean, 0, 0,
				getWidth(),
				gameMapPanel.getHeight() + diceTrayContainer.getHeight(), this);
	}

	/**
	 * Returns the dice tray container.
	 * 
	 * @return the dice tray container.
	 */
	public DiceTrayContainer getDiceTrayContainer() {
		return diceTrayContainer;
	}
}
