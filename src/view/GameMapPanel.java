package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.awt.RenderingHints;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import javax.swing.SwingWorker;

import com.sun.xml.internal.ws.util.StringUtils;

import player.Player;

import map.Continent;
import map.Continents;
import map.Territory;

import base.Game;
import base.Stage;

/**
 * 
 * 
 * The GameMapPanel is the graphical Component where in-game animations,
 * territories, and other game sequences appear. It contains the map image.
 * 
 * @author Phil
 * 
 */
public class GameMapPanel extends JPanel implements AnimationCanvas {

	private Game game;

	protected static BufferedImage offscreenMap, undoMap;

	private static BufferedImage peripheralTerritories;

	public static final double IMAGE_SIZE_RATIO = ((double) 400) / 628;

	protected static final Color OCEAN_COLOR = new Color(203, 174, 114);

	private static final Color PERIPHERIES_COLOR = Color.DARK_GRAY;

	// For construction purposes
	private static Scanner scanner;

	//
	private static List<String> textFileCache;

	private static String nextImagename;

	private static Point nextPoint = new Point(0, 0);

	private static final String TERRITORY_DIR = "territories_highlighted";

	private static final String EXT = ".png";

	protected static Map<String, Point> territorylocations;

	private static Map<String, Point> numeralPoints;

	private static final String NUMERAL_POINTS_FILENAME = "numeralpoints.dat";

	protected static Map<String, BufferedImage> originalTerritoryImageCache;

	private static String hilightedTerritory;

	private static final Color HILIGHTED_OUTLINE_COLOR = Color.blue;

	private static Map<String, BufferedImage> outlinedImageCache;

	private static final String OUTLINED_DIR = "territories_highlighted";

	private Map<String, BufferedImage> tintedState;

	private static String animateTerritoryAttacking;

	private static String animateTerritoryReinforcing;

	private static BufferedImage imageAttack;

	private static boolean doneInitializing;

	// Below: For paintComponent() image buffer
	private static Map<String, Color> highlightedTerritoriesBuffer;
	private static Map<String, Integer> territoryNumeralsBuffer;
	private static List<String> selectedTerritoriesBuffer;
	private static Set<String> deSelectedTerritoriesBuffer;

	private int selectPolicy = 1;

	private static boolean animationOn;

	private static boolean paintLocked; // switch used to control repaint()

	private Set<String> offshoreNumerals;

	private JToolTip nextTooltip;

	private List<String[]> offshoreAdjacencyGraph;

	private Image backgroundImage = ImageReader.readImage("fullbg.png");

	private static final int attackMillisDelay = 100;

	protected BufferedImage cachedAnimationCanvas;

	/**
	 * Instantiates the GameMapPanel
	 * 
	 * @param game
	 *            - the Game instance The GameMapPanel assumes that a Game is
	 *            singleton
	 */
	public GameMapPanel(Game game) {

		this.setDoubleBuffered(true);

		setLayout(null);
		paintLocked = false;
		this.game = game;

		tintedState = new HashMap<String, BufferedImage>();
		setOpaque(false);
		createImageCache();
		initializeTerritoryLocations();
		initializeNumeralPoints();
		initializeMap();
		createBuffers();
		initOffshoreNumerals();

		peripheralTerritories = tintPeripheralTerritories();

		this.tintOutlinedTerritories();

		addMouseListener(new ClickListener());

		// buildDebuggingScanner();
		buildTextFileCache();
		// Initializes the array containing the 2D coordinate space that
		// maps each point of the map to a TerritoryID
		new PointToTerritoryMapping(offscreenMap, originalTerritoryImageCache,
				territorylocations);
		initTooltip();
		this.buildOffshoreAdjacencyGraph();
		// differentiateContinents();

		cachedAnimationCanvas = new BufferedImage(offscreenMap.getWidth(),
				offscreenMap.getHeight(), BufferedImage.TYPE_INT_ARGB);

	}

	/**
	 * Sets the game to the one specified.  Used for loading.
	 * 
	 * @param game
	 * 			the game to set as.
	 */
	public void setGame(Game game) {
		this.game = game;

	}

	private void initTooltip() {

		nextTooltip = new JToolTip();
		nextTooltip.setBackground(Color.black);
		nextTooltip.setForeground(Color.orange);
		nextTooltip.setVisible(true);

		add(nextTooltip);
		addMouseMotionListener(new MotionListener());
	}

	private void initOffshoreNumerals() {
		offshoreNumerals = new HashSet<String>();
		String[] s = { "madagascar", "indonesia", "new_guinea", "japan",
				"iceland", "great_britain" };

		for (String str : s) {
			offshoreNumerals.add(str);
		}
	}

	private void createBuffers() {
		highlightedTerritoriesBuffer = new HashMap<String, Color>();
		selectedTerritoriesBuffer = new ArrayList<String>();
		territoryNumeralsBuffer = new HashMap<String, Integer>();
		deSelectedTerritoriesBuffer = new HashSet<String>();

		for (String s : originalTerritoryImageCache.keySet()) {
			territoryNumeralsBuffer.put(s, 0);
		}

	}

	private void buildTextFileCache() {
		this.textFileCache = new ArrayList<String>();
	}

	private void buildDebuggingScanner() {
		try {
			scanner = new Scanner(new File("problems.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (scanner.hasNextLine())
			nextImagename = scanner.nextLine();
		System.out.print("\nInput For: " + nextImagename + "\n");
	}

	private String eachTerritoryToString() {
		String str = "";
		for (String s : originalTerritoryImageCache.keySet()) {
			str += s + "\n";
		}
		return str;
	}

	private void initializeMap() {
		offscreenMap = ImageReader.readImage("Map_Panel_Image.png");
		// offscreenMap.createGraphics().setRenderingHint(
		// RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_ON);
		offscreenMap = new BufferedImage(
				(int) (offscreenMap.getWidth() * (IMAGE_SIZE_RATIO)),
				(int) (offscreenMap.getHeight() * (IMAGE_SIZE_RATIO)),
				BufferedImage.TYPE_INT_ARGB);
		setPreferredSize(new Dimension(offscreenMap.getWidth(), offscreenMap
				.getHeight()));
		setMinimumSize(getPreferredSize());
		setMaximumSize(getPreferredSize());
		setSize(getPreferredSize());
	}

	/**
	 * Initilizes the opening animation sequence by drawing the territories and
	 * coloring them gray.  Prepares the opening animation sequnce.
	 *
	 */
	protected void initializeOpeningAnimation() {

		int randomR = (int) DiceRoll.generateRandomNumber(0, 255);
		int randomG = (int) DiceRoll.generateRandomNumber(0, 255);
		int randomB = (int) DiceRoll.generateRandomNumber(0, 255);
		int ind = -1;
		for (int i = 0; i < 6; i++) {
			// drawOcean();
			drawPeripheralTerritories();
			ind++;
			if (ind > 5) {
				ind = 0;
			}
			colorVirginTerritories(AvailableColors.getAllAvailableColors().get(
					ind));
		}
		this.drawOffshoreAdjacencies();
		drawPeripheralTerritories();
		colorVirginTerritories(Color.LIGHT_GRAY);
		pause(1000);
		doneInitializing = true;

	}

	/**
	 * Colors all virgin territories (territories that do not have an owner)
	 * one of the available colors.
	 * 
	 * @param c
	 * 			the color to paint the virgin territory.
	 */
	private void colorVirginTerritories(Color c) {
		int rI = (int) DiceRoll.generateRandomNumber(0, 5);
		if (c == null) {
			c = AvailableColors.getAllAvailableColors().get(rI);
		}

		tintAllTerritories(c, true);
	}

	private static BufferedImage tintPeripheralTerritories() {
		BufferedImage im = ImageReader.readImage("peripheries(greyscale).png");

		java.util.List<BufferedImage> single = new java.util.ArrayList<BufferedImage>(
				1);
		single.add(im);
		im = TerritoryTinter.tintImages(single, GameMapPanel.PERIPHERIES_COLOR)
				.get(0);
		return im;
	}

	private void tintOutlinedTerritories() {
		TerritoryTinter.tintImages(this.outlinedImageCache,
				HILIGHTED_OUTLINE_COLOR);
	}

	private void tintOutlinedTerritory(Territory t, Color c) {
		List<BufferedImage> single = new ArrayList(1);
		String name = t.getName().toLowerCase();
		single.add(outlinedImageCache.get(name));
		single = TerritoryTinter.tintImages(single, c);
		outlinedImageCache.put(name, single.get(0));

	}

	private static void drawPeripheralTerritories() {
		Graphics2D g = offscreenMap.createGraphics();
		g.drawImage(peripheralTerritories, 0, 0, offscreenMap.getWidth(),
				offscreenMap.getHeight(), null);
		g.dispose();
	}

	private void drawOcean() {
		Graphics g = offscreenMap.getGraphics();
		g.setColor(GameMapPanel.OCEAN_COLOR);
	}

	private void createImageCache() {

		originalTerritoryImageCache = new HashMap<String, BufferedImage>();
		outlinedImageCache = new HashMap<String, BufferedImage>();

		BufferedImage rescaled = null;
		Graphics g = null;

		buildCacheMap(originalTerritoryImageCache, TERRITORY_DIR, 1);
		buildCacheMap(outlinedImageCache, GameMapPanel.OUTLINED_DIR, .6369);
	}

	private void buildCacheMap(Map<String, BufferedImage> map, String dir,
			double ratio) {

		BufferedImage rescaled = null;
		Graphics2D g = null;

		for (String field : GameMapPanel.getAllTerritoriesLowerCase()) {
			BufferedImage b = ImageReader.readImage(dir + "/"
					+ fieldToFilename(field));
			rescaled = new BufferedImage((int) (b.getWidth() * ratio), (int) (b
					.getHeight() * ratio), BufferedImage.TYPE_INT_ARGB);
			g = rescaled.createGraphics();
			g.drawImage(b, 0, 0, rescaled.getWidth(), rescaled.getHeight(),
					this);
			g.dispose();
			map.put(field.toLowerCase(), rescaled);
		}

	}

	/**
	 * 
	 * Updates the view's representation of the game state, specifically who
	 * owns which territory and how many troops there are in each territory.
	 * 
	 * 
	 * @param changedTerritories
	 *            - any collection of Territories that has changed
	 * 
	 */
	public void update(Collection<Territory> changedTerritories) {
		if (paintLocked == true) {
			return;
		}

		paintLocked = true;

		// GameMapPanel.drawPeripheralTerritories();
		updateTerritoryNumerals(changedTerritories);
		updateTerritoryColors(changedTerritories);
		repaint();
		paintLocked = false;

	}

	private void updateTerritoryColors(Collection<Territory> changedTerritories) {
		for (Territory t : changedTerritories) {
			if (t.getPlayer() == null) {
				this.tintTerritory(t.getName(), Color.LIGHT_GRAY, true);
			} else {
				this.tintTerritory(t.getName(), t.getPlayer().getColor(), true);
			}
		}

	}

	private void updateTerritoryNumerals(
			Collection<Territory> changedTerritories) {
		List<Player> playerList = game.getAllPlayers();
		List<Territory> territoryList = null;
		Graphics g = offscreenMap.getGraphics();

		for (Territory t : game.getMap().getTerritories()) {
			String name = t.getName().toLowerCase();
			if (offshoreNumerals.contains(t.getName().toLowerCase())) {
				int x = (int) numeralPoints.get(t.getName().toLowerCase())
						.getX();
				int y = (int) numeralPoints.get(t.getName().toLowerCase())
						.getY();
				Image subimage = createImage(new FilteredImageSource(
						backgroundImage.getSource(), new CropImageFilter(
								x + 108, y - 14, 19, 14)));
				g.drawImage(subimage, x, y - 14, this);
				// g.drawRect(x, y, 19, 14);
			}
			int troops = t.getTroopsOnTerritory();
		}

	}

	private void updateSelectedTerritory() {

	}

	/**
	 * Clears all outlined territories
	 */
	public void clearOutlinedTerritories() {
		deSelectedTerritoriesBuffer.addAll(selectedTerritoriesBuffer);
		selectedTerritoriesBuffer.clear();
	}

	/**
	 * Draws a visual representation of an invasion
	 * 
	 * @param attacker
	 *            - the attacking territory
	 * @param defender
	 *            - the defending territory
	 */
	public void drawInvasion(Territory attacker, Territory defender) {

	}

	/**
	 * 
	 * Clears all drawn invasions.
	 * 
	 */
	public void clearDrawnInvasions() {

	}

	/**
	 * Clears the dice tray
	 */
	public void clearDice() {

	}

	/**
	 * Fires a sound effect
	 * 
	 * @param soundEffect
	 *            - The type of sound effect specified in SoundEffect
	 */
	public void fireSoundEffect(Enum<SoundEffect> soundEffect) {

	}

	public void paintComponent(Graphics g) {

		g = g.create();
		if (paintLocked) {
			return;
		}

		g.setColor(OCEAN_COLOR);
		Graphics2D offscreen = offscreenMap.createGraphics();

		// The set of transforms on map:

		if (doneInitializing) {
			dumpDeselectedTerritories();
			selectTerritories();
			drawNumerals();
			// outlineContinents();

		}
		//

		offscreen.dispose();

		// ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawImage(offscreenMap, 0, 0, offscreenMap.getWidth(), offscreenMap
				.getHeight(), this);
		drawAnimations(g);
		g.dispose();

	}

	/**
	 * Updates the image.
	 * 
	 * @param img
	 * 			the image to update t.
	 * @param infoflags
	 * 			all information flags that need to be set.
	 * @param x
	 * 			the x coordinate.
	 * @param y
	 * 			the y coordinate.
	 * @param w
	 * 			the width.
	 * @param h
	 * 			the height.
	 * 
	 * @return true if there was a successful update, false if painting was locked.
	 */
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int w,
			int h) {
		paintLocked = false;
		return super.imageUpdate(img, infoflags, x, y, w, h);
	}

	private void selectTerritories() {
		if (paintLocked) {
			return;
		}
		synchronized (selectedTerritoriesBuffer) {
			try {
				for (String s : selectedTerritoriesBuffer) {
					// this.hilightTerritoryByName(s);
				}
			} catch (java.util.ConcurrentModificationException cme) {
				return;
			}

		}
	}

	private void outlineContinents() {
		for (Territory t : game.getMap().getTerritories()) {
			outlineTerritory(t);
		}
	}

	private void drawOffshoreAdjacencies() {
		Graphics2D g = offscreenMap.createGraphics();
		g.setColor(new Color(119, 119, 119));

		final int thickness = 2;

		final int offset = 2;
		for (String[] territory : this.offshoreAdjacencyGraph) {

			String t1 = territory[0];
			String t2 = territory[1];
			Point p1 = centerNumeralPoint(numeralPoints.get(t1));
			Point p2 = centerNumeralPoint(numeralPoints.get(t2));

			drawLine(g, p2.x, p2.y, p1.x, p1.y, thickness);
			// g.drawLine(p1.x,p1.y,p2.x,p2.y);
		}

		// special cases
		Point s1 = centerNumeralPoint(numeralPoints.get("alaska"));
		Point s2 = centerNumeralPoint(numeralPoints.get("kamchatka"));
		drawLine(g, s1.x, s1.y, s1.x - 100, s1.y, thickness);
		drawLine(g, s2.x, s2.y, s2.x + 100, s2.y, thickness);
		g.dispose();
	}

	private Point centerNumeralPoint(Point arg) {
		int width = 7;
		return new Point(arg.x + width / 2, arg.y - width);
	}

	private void dumpDeselectedTerritories() {

		List<String> toRemove = new ArrayList();
		for (String s : deSelectedTerritoriesBuffer) {
			if (!s.equals("ocean")) {
				deHighlightTerritoryByName(s);
			}

			toRemove.add(s);
		}
		deSelectedTerritoriesBuffer.removeAll(toRemove);

	}

	private void drawNumerals() {
		List<String> list = new ArrayList();
		for (String s : territoryNumeralsBuffer.keySet()) {
			list.add(s);
		}
		for (String s : list) {
			if (game != null) {
				map.Map map = game.getMap();
				if (map != null) {

					int troops = game.getMap().getTerritoryByName(
							s.toUpperCase()).getTroopsOnTerritory();
					drawNumberFor(s, troops);
				}
			}
		}
	}

	private void drawAnimations(Graphics g) {
		if (animationOn) {
			if (animateTerritoryAttacking != null) {
				int size = MapPanelActionAnimation.size;
				Point loc = centerOfMassFor(animateTerritoryAttacking);
				loc.x -= size / 2;
				loc.y -= size / 2;
				g.drawImage(imageAttack, loc.x, loc.y, this);
			} else if (animateTerritoryReinforcing != null) {
				int size = MapPanelActionAnimation.REINFORCEMENT_SIZE;
				Point loc = centerOfMassFor(animateTerritoryReinforcing);
				loc.x -= size / 2;
				loc.y -= size / 2;
				g.drawImage(imageAttack, loc.x, loc.y, this);
			}
		}

	}

	// Takes a string "NORTHWEST_TERRITORY" and converts to
	// "Northwest Territory"
	private String formatTerritoryName(String name) {
		name = name.replace("_", " ");
		name = name.toLowerCase();
		String[] words = name.split(" ");

		name = "";
		for (int i = 0; i < words.length; i++) {
			String firstLetter = "" + words[i].charAt(0);
			firstLetter = firstLetter.toUpperCase();
			words[i] = firstLetter + words[i].substring(1);

			name += words[i] + " ";
		}
		name = name.trim();
		return name;
	}

	private class ClickListener implements MouseListener {

		public void mouseClicked(MouseEvent arg0) {

			int button = arg0.getButton();

			processMouseClick(arg0);
			// addTerritoryToTextFileCache(arg0);
			// debuggingOperation(arg0);
		}

		public void mouseEntered(MouseEvent arg0) {

		}

		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

	}

	private class MotionListener implements MouseMotionListener {

		protected MotionListener() {
			super();
		}

		@Override
		public void mouseDragged(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseMoved(MouseEvent arg0) {
			nextTooltip.setVisible(false);
			showToolTip(arg0);
			// debuggingOperation(arg0);
		}

	}

	private void showToolTip(MouseEvent arg0) {
		Point p = arg0.getPoint();

		setLayout(null);
		String name = formatTerritoryName(getNameOfTerritoryAt(p.x, p.y));

		if (name.toLowerCase().equals("ocean")) {
			nextTooltip.setVisible(false);
			return;
		}
		nextTooltip.setVisible(true);

		nextTooltip.setTipText(name);
		nextTooltip.setBounds(new Rectangle(p.x, p.y + 20, nextTooltip
				.getPreferredSize().width,
				nextTooltip.getPreferredSize().height));
		repaint();

	}

	private void processMouseClick(final MouseEvent arg0) {

		if (animationOn() || paintLocked) { // Disable inputs if
			// animations on
			return;
		}

		int button = arg0.getButton();

		switch (button) {

		case 1:
			selectTerritory(arg0.getX(), arg0.getY());
			break;

		case 2:

			break;

		}

		repaint();

	}

	private void selectTerritory(int x, int y) {
		String name = getNameOfTerritoryAt(x, y);
		name = name.toLowerCase();

		if (name.equals("ocean")) {
			return;
		}
		if (selectedTerritoriesBuffer.size() >= this.selectPolicy) {
			clearOutlinedTerritories();
		}

		boolean unique = !selectedTerritoriesBuffer.contains(name);

		if (!unique) {
			clearTerritoryForName(name);
		}
		selectTerritoryForName(name);
	}

	private void selectTerritoryForName(String name) {
		name = name.toLowerCase();
		selectedTerritoriesBuffer.add(name);
		hilightSelectedTerritories(copy(selectedTerritoriesBuffer));
		repaint();
	}

	private void hilightSelectedTerritories(List<String> selected) {
		for (String s : selected) {
			Territory t = game.getMap().getTerritoryByName(s.toUpperCase());
			Player p = t.getPlayer();
			Color c = null;
			if (p != null) {
				c = t.getPlayer().getColor();
			} else {
				c = Color.LIGHT_GRAY;
			}
			for (int i = 0; i < 1; i++) {
				c = c.darker();
			}
			tintTerritory(s, c, false);

		}
	}

	private List copy(List l) {
		List newL = new ArrayList(l.size());
		newL.addAll(l);
		return newL;
	}

	private static String getNameOfTerritoryAt(int x, int y) {
		return PointToTerritoryMapping.getNameOfTerritoryAt(x, y);
	}

	protected void clearTerritory(String name) {
		if (paintLocked) {
			return;
		}
		clearTerritoryForName(name);
		repaint();
	}

	private void clearTerritoryForName(String name) {
		if (paintLocked) {
			return;
		}
		name = name.toLowerCase();
		selectedTerritoriesBuffer.remove(name);
		deSelectedTerritoriesBuffer.add(name);
	}

	/**
	 * Clears all outlined territories and then repaints the panel.
	 *
	 */
	protected void clearAllTerritories() {
		if (paintLocked) {
			return;
		}
		clearOutlinedTerritories();
		repaint();
	}

	private void outlineTerritory(Territory t) {

		String name = t.getName();

		outlineTerritoryByName(t.getName());

	}

	private void outlineTerritoryByName(String name) {
		name = name.toLowerCase();

		if (name.equals("ocean")) {
			return;
		}

		this.overlayImageAt(numeralPoints.get((name)), outlinedImageCache
				.get(name), false, name, false);
	}

	private void deHighlightTerritoryByName(String name) {
		this.overlayImageAt(territorylocations.get((name)), tintedState
				.get(name), false, name, false);
	}

	private void outputTextFile(String filename) {
		PrintWriter p = null;
		File f = new File(filename);
		FileWriter writer = null;
		try {
			writer = new FileWriter(f);
			p = new PrintWriter(new File(filename));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (String s : textFileCache) {
			p.write(s + "\n");
		}
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		p.close();

	}

	private void addTerritoryToTextFileCache(MouseEvent arg0) {
		int button = arg0.getButton();
		if (button == 1) {
			String territory = this.getNameOfTerritoryAt(arg0.getPoint().x,
					arg0.getPoint().y);
			if (territory.toLowerCase().equals("ocean")) {
				return;
			}
			this.textFileCache.add(territory.toLowerCase());
			System.out.println("added: " + territory);
		}
		if (button == 3) {
			System.out.println("finished");
			outputTextFile("output.txt");
		}
	}

	private void debuggingOperation(MouseEvent arg0) {

		int button = arg0.getButton();
		if (button == 2) {
			nextPoint = arg0.getPoint();
			if (scanner.hasNextLine()) {
				// GameMapPanel.territorylocations.put(nextImagename,
				// nextPoint);
				this.textFileCache.add(nextPoint.x + " " + nextPoint.y);
				System.out.println(nextPoint.x + " " + nextPoint.y);
				// System.out.println("successfully put:" + nextImagename + " "
				// + nextPoint);
				nextImagename = scanner.nextLine();
				// System.out.print("\nInput For: " + nextImagename + "\n");
			} else {
				// GameMapPanel.territorylocations.put(nextImagename,
				// nextPoint);
				System.out.println(nextPoint.x + " " + nextPoint.y);
				this.textFileCache.add(nextPoint.x + " " + nextPoint.y + "\n");
				System.out.println("successfully put:" + nextImagename + " "
						+ nextPoint);
				// this.outputTextFile("outputlocations.txt");
				// ObjectRW.writeObject(territorylocations,
				// "newlocations2.dat");
				System.out.println("finished writing points");
			}
		} else if (button == 1) {
			replaceCanvasWith(ImageReader.readImage("map_image.png"));
			System.out.print("\nInput For: " + nextImagename + "\n");
			undoMap = offscreenMap;
		} else if (button == 0) {
			replaceCanvasWith(ImageReader.readImage("map_image.png"));
			List<BufferedImage> l = new ArrayList(1);
			l.add(ImageReader.readImage("territories(greyscale)2/"
					+ (fieldToFilename(nextImagename))));

			BufferedImage im = TerritoryTinter.tintImages(l, Color.red).get(0);

			overlayImageAt(arg0.getPoint(), im, true, nextImagename, false);
		}
	}

	private static void writePointsToDisk() {
		ObjectOutputStream fout = null;
		try {
			fout = new ObjectOutputStream(new FileOutputStream(new File(
					"territorypoints.dat")));
			fout.writeObject(territorylocations);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	private static void initializeTerritoryLocations() {

		ObjectInputStream fin = null;

		try {
			// fin = new ObjectInputStream(new FileInputStream(new File(
			// "newlocations2.dat")));

			// GameMapPanel.territorylocations = (Map<String, Point>) fin
			// .readObject();
			initializeLocations();
			// fin.close();
			// rescalePoints(territorylocations);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void initializeLocations() {

		territorylocations = (HashMap) ObjectRW.readObject("newlocations2.dat");

		/*
		 * List<String> names = new ArrayList(); List<Point> points = new
		 * ArrayList(); Scanner s = null; Scanner s2 = null; try { s = new
		 * Scanner(new File("keys.txt")); s2 = new Scanner(new
		 * File("outputlocations.txt")); } catch (Exception e) {
		 * e.printStackTrace(); }
		 * 
		 * territorylocations = new HashMap<String, Point>(); while
		 * (s.hasNextLine()) { String name = s.nextLine().trim(); if
		 * (s2.hasNextLine()) { String point = s2.nextLine(); if
		 * (point.trim().length() > 0) { String[] split = point.split(" ");
		 * 
		 * String int1 = split[0]; String int2 = split[1]; int arg1 =
		 * Integer.parseInt(int1); int arg2 = Integer.parseInt(int2);
		 * territorylocations.put((name), new Point(arg1, arg2)); } } }
		 * 
		 * ObjectRW.writeObject(territorylocations, "newlocations2.dat");
		 */

	}

	private static void rescalePoints(Map<String, Point> points) {
		for (String s : points.keySet()) {
			Point p = points.get(s);
			p.x = (int) (p.x * IMAGE_SIZE_RATIO);
			p.y = (int) (p.y * IMAGE_SIZE_RATIO);
		}
	}

	private void tintAllTerritories(Color c, boolean anim) {
		tintTerritories(getAllTerritoriesLowerCase(), c, anim, true, true);
		repaint();
	}

	private static Map<String, BufferedImage> imageMapFromFields(
			Collection<String> fields) {

		Map<String, BufferedImage> intersection = new HashMap();
		for (String field : fields) {

			field = field.toLowerCase();
			if (!field.equals("ocean")) {

				BufferedImage next = originalTerritoryImageCache.get(field);

				if (next == null) {
					throw new IllegalArgumentException("NO SUCH FIELD: "
							+ field);
				}

				intersection.put(field, next);
			}
		}
		return intersection;
	}

	private void tintTerritory(String field, Color c, boolean putAll) {
		List<String> fields = new ArrayList(1);
		fields.add(field.toLowerCase());
		tintTerritories(fields, c, false, false, putAll);
	}

	private void tintTerritories(Collection<String> territoriesFields, Color c,
			boolean anim, boolean repaint, boolean putAll) {

		Map<String, BufferedImage> imageMap = imageMapFromFields(territoriesFields);

		TerritoryTinter.tintImages(imageMap, c);

		if (putAll) {
			tintedState.putAll(imageMap);
		}
		for (String territoryField : territoriesFields) {
			overlayTerritory(territoryField, false, imageMap, anim);
		}
		if (repaint) {
			repaint();
		}
	}

	private static Collection<String> getAllTerritoriesLowerCase() {
		return TerritoryTinter.getAllTerritoriesByStringLowerCase();
	}

	private void overlayTerritory(String field, boolean repaint,
			Map<String, BufferedImage> tintedImageMap, boolean anim) {

		BufferedImage theImage = tintedImageMap.get(field);

		Point loc = GameMapPanel.territorylocations.get((field));

		overlayImageAt(loc, theImage, repaint, field, anim);
	}

	/**
	 * Returns the field to a filename.
	 * 
	 * @param field
	 * 			the field that needs to be converted to a filename.
	 * @return  the field converted to a filename.
	 */
	protected static String fieldToFilename(String field) {
		return field.toLowerCase() + EXT;
	}

	private void tintTerritoriesByFile(Collection<String> filenames, Color c) {
		for (String s : filenames) {
			tintTerritoryByFile(s, c, false);
		}
	}

	private void tintTerritoryByFile(String filename, Color c, boolean repaint) {
		BufferedImage theImage = ImageReader.readImage(TERRITORY_DIR + "/"
				+ filename);

		Point loc = GameMapPanel.territorylocations.get(filename);

		overlayImageAt(loc, theImage, repaint, filename, false);
	}

	private void replaceCanvasWith(BufferedImage im) {
		if (im != null)
			this.offscreenMap = im;
		repaint();
	}

	private void overlayImageAt(Point p, BufferedImage theImage,
			boolean repaint, String debugImagename, boolean anim) {

		try {

			Graphics g = offscreenMap.createGraphics();
			g.drawImage(theImage, p.x, p.y, this);

			g.dispose();
		} catch (NullPointerException n) {
			n.printStackTrace();
		}

		if (anim) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (repaint || anim) {
			repaint();
		}
	}

	private void drawNumberFor(String territory, int number) {
		territory = territory.toLowerCase();
		Point drawAt = numeralPoints.get((territory));

		drawNumeral(number + "", drawAt.x, drawAt.y, territory);
	}

	private void drawNumeral(String numeral, int x, int y, String territory) {
		Graphics g = offscreenMap.createGraphics();
		territory = territory.toLowerCase();
		g.setFont(new Font("Arial", Font.BOLD, 15));
		if (offshoreNumerals.contains(territory)) {
			g.setColor(Color.BLACK);
			Point p = new Point(x, y);

			g.drawString(numeral, x, y);
		} else {
			g.setColor(Color.white);

			g.drawString(numeral, x, y);
		}
		g.dispose();
	}

	private Point centerOfMassFor(String territory) {

		Point p = numeralPoints.get(territory);
		BufferedImage im = tintedState.get(territory);
		return new Point(p.x, p.y);
	}

	private void initializeNumeralPoints() {
		numeralPoints = (Map<String, Point>) ObjectRW
				.readObject(NUMERAL_POINTS_FILENAME);
		rescalePoints(numeralPoints);

		// Fixing points
		numeralPoints.put("central_america", new Point((int) numeralPoints.get(
				"central_america").getX() - 11, (int) numeralPoints.get(
				"central_america").getY()));
		numeralPoints.put("venezuela", new Point((int) numeralPoints.get(
				"venezuela").getX() - 5, (int) numeralPoints.get("venezuela")
				.getY() + 5));
		numeralPoints.put("peru", new Point((int) numeralPoints.get("peru")
				.getX() - 5, (int) numeralPoints.get("peru").getY()));
		numeralPoints.put("southern_europe", new Point((int) numeralPoints.get(
				"southern_europe").getX() - 5, (int) numeralPoints.get(
				"southern_europe").getY()));
		numeralPoints.put("siberia", new Point((int) numeralPoints.get(
				"siberia").getX() - 9, (int) numeralPoints.get("siberia")
				.getY()));
		numeralPoints.put("indonesia", new Point((int) numeralPoints.get(
				"indonesia").getX(), (int) numeralPoints.get("indonesia")
				.getY() + 8));
		numeralPoints.put("china", new Point((int) numeralPoints.get("china")
				.getX(), (int) numeralPoints.get("china").getY() + 8));
		numeralPoints.put("ontario", new Point((int) numeralPoints.get(
				"ontario").getX(),
				(int) numeralPoints.get("ontario").getY() + 6));
		numeralPoints.put("siam", new Point((int) numeralPoints.get("siam")
				.getX() - 10, (int) numeralPoints.get("siam").getY()));
		numeralPoints.put("alberta", new Point((int) numeralPoints.get(
				"alberta").getX(),
				(int) numeralPoints.get("alberta").getY() + 8));
		numeralPoints.put("northwest_territory", new Point((int) numeralPoints
				.get("northwest_territory").getX(), (int) numeralPoints.get(
				"northwest_territory").getY() + 8));
		numeralPoints.put("iceland", new Point((int) numeralPoints.get(
				"iceland").getX(),
				(int) numeralPoints.get("iceland").getY() - 25));
		numeralPoints.put("great_britain", new Point((int) numeralPoints.get(
				"great_britain").getX() - 18, (int) numeralPoints.get(
				"great_britain").getY() + 16));
		numeralPoints.put("western_europe", new Point((int) numeralPoints.get(
				"western_europe").getX() - 8, (int) numeralPoints.get(
				"western_europe").getY()));
	}

	/**
	 * Fires off the attack animation.
	 * 
	 * @param defending
	 * 			the defending territory to point the arrow to.
	 * @param attacking
	 * 			the attacking territory to point the arrow from.
	 */
	protected void fireAttackAnimation(Territory defending, Territory attacking) {
		createArrowAnimation(defending, attacking, Stage.ATTACK);
	}

	/**
	 * Fires off the fortify animation.
	 * 
	 * @param from
	 * 			the territory from.
	 * @param to
	 * 			the territory to.
	 */
	protected void fireFortifyAnimation(Territory from, Territory to) {
		createArrowAnimation(to, from, Stage.FORTIFY);
	}

	/**
	 * Fires off the reinforcement animation.
	 * 
	 * @param at
	 * 			the territory that should be reinforced.
	 */
	protected void fireReinforcementAnimation(Territory at) {
		MapPanelActionAnimation mpaa = new MapPanelActionAnimation(null,
				Stage.REINFORCEMENTS);
		animateTerritoryReinforcing = at.getName().toLowerCase();

		MapPanelAnimator anim = new MapPanelAnimator(this, mpaa, 100);
		Thread t = new Thread(anim);
		t.start();
	}

	/**
	 * Fires off the conquered animation.
	 * 
	 * @param at
	 * 			the territory that was conquered.
	 */
	protected void fireConqueredAnimation(Territory at) {
		MapPanelActionAnimation mpaa = new MapPanelActionAnimation(
				MapPanelActionAnimation.CONQUER);
		animateTerritoryReinforcing = at.getName().toLowerCase();

		MapPanelAnimator anim = new MapPanelAnimator(this, mpaa, 100);
		Thread t = new Thread(anim);
		t.start();
	}

	private void createArrowAnimation(Territory defending, Territory attacking,
			Enum<Stage> e) {
		animateTerritoryAttacking = attacking.getName().toLowerCase();
		String animateTerritoryDefending = defending.getName().toLowerCase();
		Point attack = centerOfMassFor(animateTerritoryAttacking);
		Point defend = centerOfMassFor(animateTerritoryDefending);

		Point displacement = new Point(defend.x - attack.x,
				-(defend.y - attack.y));
		correctDisplacementIfSpecialCase(displacement, defending, attacking);
		MapPanelActionAnimation mpaa = new MapPanelActionAnimation(
				displacement, e);
		MapPanelAnimator anim = new MapPanelAnimator(this, mpaa, 100);
		Thread t = new Thread(anim);
		t.start();
	}

	/**
	 * Returns the length of animations in millis.
	 * 
	 * @return the animatiom length in millis.
	 */
	protected long getMillisLengthForAnimation() {
		int numFrames = MapPanelActionAnimation.ATTACK_ANIMATION_FRAMES;
		long millisDelay = GameMapPanel.attackMillisDelay;
		return numFrames * millisDelay;
	}

	private void correctDisplacementIfSpecialCase(Point displacement,
			Territory defending, Territory attacking) {
		double length = MapPanelActionAnimation.lengthOfVector(displacement);
		MapPanelActionAnimation.size = ((int) (length * 2));
		Set<String> combo = new HashSet();

		combo.add(defending.getName().toLowerCase());
		combo.add(attacking.getName().toLowerCase());

		if (combo.contains("alaska") && combo.contains("kamchatka")) {
			correctDisplacement(displacement);
		}

	}

	private void correctDisplacement(Point displacement) {
		MapPanelActionAnimation.size = 150;
		displacement.x *= -1;
		displacement.y *= -1;
	}

	/**
	 * Stops an animation.
	 * 
	 * @param originalCanvas
	 * 			the canvas to stop animation.
	 */
	protected void endAnimation(BufferedImage originalCanvas) {

		animationOn = false;
		imageAttack = null;
		animateTerritoryAttacking = null;
		animateTerritoryReinforcing = null;

		/*
		 * offscreenMap = originalCanvas;
		 */
		repaint();
	}

	/**
	 * Updates the frame with a list of buffered images.
	 * 
	 * @param im
	 * 			the list of buffered images.
	 */
	@Override
	public void updateFrame(List<BufferedImage> im) {
		BufferedImage draw = im.get(0);
		imageAttack = draw;
		repaint();

	}

	/**
	 * Determines wheter or not animations are enabled.
	 * 
	 * @return true if animations are enabled, false otherwise.
	 */
	protected boolean animationOn() {
		return animationOn;
	}

	/**
	 * Turns on animations.
	 * 
	 */
	protected void turnOnAnimation() {
		animationOn = true;
	}

	/**
	 * Returns an array of territories that have been selected.
	 * 
	 * @return an array of selected territories.
	 */
	protected Territory[] getSelectedTerritories() {
		Territory coll[] = new Territory[2];
		List<String> select = selectedTerritoriesBuffer;

		synchronized (selectedTerritoriesBuffer) {

			for (int i = 0; i < select.size(); i++) {
				coll[i] = retrieveTerritoryByName(select.get(i));
			}
		}
		return coll;
	}

	private Territory retrieveTerritoryByName(String name) {

		if (name == null) {
			return null;
		}

		name = name.toLowerCase();
		Collection<Territory> t = game.getMap().getTerritories();
		for (Territory terr : t) {
			String next = terr.getName().toLowerCase();
			if (next.equals(name)) {
				return terr;
			}
		}
		throw new IllegalArgumentException("NO SUCH TERRITORY: " + name + "!!");
	}

	/**
	 * Sets the selection policy to the number specified.  If the policy is set to
	 * 1, then only 1 territory can be selected.  Used in the game class to set
	 * the policy during the different stages of the game.
	 * 
	 * @param select the number of selections that are allowed.
	 */
	protected void setSelectionPolicy(int select) {
		this.selectPolicy = select;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		panel.setBackground(Color.black);
		frame.setContentPane(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		GameMapPanel thePanel = new GameMapPanel(null);

		panel.add(thePanel);
		frame.pack();
		Collection<String> territories = new java.util.ArrayList<String>();
		territories.add("peru");
		// thePanel.tintTerritories(territories,Color.orange);

		for (String s : TerritoryTinter.getAllTerritoriesByStringLowerCase()) {
			// thePanel.drawNumberFor(s, (int) DiceRoll
			// .generateRandomNumber(0, 20));
		}

		thePanel.initializeOpeningAnimation();
	}

	private void differentiateContinents() {
		Map<Integer, Color> cMap = new HashMap();
		cMap.put(Continents.NORTH_AMERICA, new Color(74, 70, 26));
		cMap.put(Continents.SOUTH_AMERICA, new Color(16, 110, 95));
		cMap.put(Continents.AFRICA, new Color(150, 57, 22));
		cMap.put(Continents.EUROPE, new Color(24, 63, 99));
		cMap.put(Continents.ASIA, new Color(42, 163, 68));
		cMap.put(Continents.OCEANIA, new Color(97, 27, 68));

		for (Territory t : game.getMap().getTerritories()) {
			Color next = cMap.get(t.getContinentIn().getIdentificationNum());
			outlineTerritory(t);
			this.tintOutlinedTerritory(t, next);
		}

	}

	private static int getFieldValue(Field f, Continents instance) {
		try {
			return f.getInt(instance);
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException("no such field: " + f);
	}

	private void pause(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void buildOffshoreAdjacencyGraph() {
		offshoreAdjacencyGraph = new ArrayList();
		Scanner scan = null;
		try {
			scan = new Scanner(new File("offshoreadjacency.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] rel = null;
		String next = null;
		while (scan.hasNextLine()) {
			rel = new String[2];
			next = scan.nextLine();
			rel[0] = next;
			next = scan.nextLine();
			rel[1] = next;
			offshoreAdjacencyGraph.add(rel);

		}

	}

	/**
	 * Draws a line from a coordinate to another coordinate.  Used in animations.
	 * 
	 * @param g
	 * 			the Graphics object.
	 * @param x1
	 * 			the x1 coordinate.
	 * @param y1
	 * 			the y1 coordinate.
	 * @param x2
	 * 			the x2 coordinate.
	 * @param y2
	 * 			the y2 coordinate.
	 * @param thickness
	 * 			the thickness of the line.
	 */
	protected static void drawLine(Graphics g, int x1, int y1, int x2, int y2,
			int thickness) {
		Graphics2D g2d = (Graphics2D) g;
		float[] dash = new float[2];
		dash[0] = 6L; // line segment length
		dash[1] = 6L; // gap length
		g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_ROUND, 1L, dash, 0L));

		g2d.drawLine(x1, y1, x2, y2);

	}

	/**
	 * Fires off a conquered continent animation.
	 * 
	 * @param c
	 * 			the continent that was conquered.
	 */
	protected void fireConqueredContinentAnimation(Continent c) {
		Collection<Territory> coll = c.getTerritoryList();

		for (int i = 0; i < 5; i++) {
			for (Territory next : coll) {
				selectTerritoryForName(next.getName());
			}
			pause(400);
			this.clearAllTerritories();
		}
		pause(700);
	}

}
