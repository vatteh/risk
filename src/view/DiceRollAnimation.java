package view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 
 * A DiceRollAnimator is associated with the GameMapPanel since the MapPanel
 * shows this Animation.
 * 
 * When an object calls getAnimationSequence() on DiceRollAnimation, the
 * DiceRollAnimation returns a series of images in the form of a time-ordered
 * List.
 * 
 * 
 * @author Phil
 * 
 */
public class DiceRollAnimation implements Animation {

	private static final double IMAGE_HEIGHT = 90;
	private static final double IMAGE_WIDTH = 135;
	private static final Color Alpha_Color = Color.white;
	private static final String directory = "animations/dicerollanimation/";
	private static BufferedImage attackingDiceCanvas;
	private static BufferedImage defendingDiceCanvas;

	private static BufferedImage[] defendingDice;
	private static BufferedImage[] attackingDice;

	private static final int NUM_FRAMES = 5;
	private static final int rotationSpeedPerFrame = 15;

	private DiceRoll theRoll;

	public static final int ATTACKER_DICE_ROLL_ANIMATION = 0;
	public static final int DEFENDER_DICE_ROLL_ANIMATION = 1;

	private List<BufferedImage>[] defenderAnimation;
	private List<BufferedImage>[] attackerAnimation;

	/**
	 * 
	 * @param roll
	 *            - The DiceRoll representing a set of results.
	 */
	protected DiceRollAnimation(DiceRoll roll) {
		
		theRoll = roll;

		if (attackingDiceCanvas == null || defendingDiceCanvas == null) {
			attackingDiceCanvas = ImageReader.readImage(directory
					+ "attackingdice.png");
			defendingDiceCanvas = ImageReader.readImage(directory
					+ "defendingdice.png");
		}
		buildCollections();

		attackerAnimation = makeAnimationSequence(NUM_FRAMES,
				DiceRollAnimation.ATTACKER_DICE_ROLL_ANIMATION);
		defenderAnimation = makeAnimationSequence(NUM_FRAMES,
				DiceRollAnimation.DEFENDER_DICE_ROLL_ANIMATION);
	}

	/**
	 * When getting an animation sequence, an Animation retrieves a list of
	 * off-screen BufferedImages representing a complete animation, and returns
	 * that list.
	 * 
	 * @param i
	 *            - one of the animation subtypes defined in DiceRollAnimation:
	 *            DEFENDER_DICE_ROLL_ANIMATION OR ATTACKER_DICE_ROLL_ANIMATION
	 * 
	 * 
	 * @return - an array of Lists, where each list represents an animation of a
	 *         single dice.
	 * 
	 * 
	 */
	public List<BufferedImage>[] getAnimationSequence(int i) {

		return i == DiceRollAnimation.ATTACKER_DICE_ROLL_ANIMATION ? attackerAnimation
				: defenderAnimation;
	}

	private List<BufferedImage>[] makeAnimationSequence(int numFrames, int type) {

		int[] result = null;

		if (type == DiceRollAnimation.ATTACKER_DICE_ROLL_ANIMATION) {
			result = theRoll.getAttackerResult();
		} else if (type == DiceRollAnimation.DEFENDER_DICE_ROLL_ANIMATION) {
			result = theRoll.getDefenderResult();
		} else {
			throw new IllegalArgumentException("INVALID ANIMATION TYPE: "
					+ type);
		}

		List<BufferedImage>[] animation = new ArrayList[result.length];

		for (int i = 0; i < result.length; i++) {

			double currentRotation = 0;

			List<BufferedImage> nextList = new ArrayList(numFrames);

			int direction = 1;

			if (i % 2 == 0) {
				direction = -1;
			}

			for (int j = 0; j < numFrames; j++) {

				int numToShow;

				if (j < numFrames - 1) {
					numToShow = (int) DiceRoll.generateRandomNumber(1, 6);
				} else {

					numToShow = result[i];
				}

				BufferedImage nextImage = null;

				if (type == DiceRollAnimation.ATTACKER_DICE_ROLL_ANIMATION) {
					nextImage = DiceRollAnimation.rotateImage(DiceRollAnimation
							.getAttackingDiceImage(numToShow), currentRotation);
				} else {
					nextImage = DiceRollAnimation.rotateImage(DiceRollAnimation
							.getDefendingDiceImage(numToShow), currentRotation);
				}

				nextList.add(nextImage);

				currentRotation += direction * rotationSpeedPerFrame;
			}

			animation[i] = nextList;
		}

		return animation;
	}

	private static BufferedImage getAttackingDiceImage(int dice) {
		return attackingDice[dice - 1];
	}

	private static BufferedImage getDefendingDiceImage(int dice) {
		return defendingDice[dice - 1];
	}

	private static void buildCollections() {

		if (defendingDice != null && attackingDice != null) {
			return;
		}

		defendingDice = new BufferedImage[6];
		attackingDice = new BufferedImage[6];

		for (int i = 0; i < 6; i++) {
			attackingDice[i] = getImageFromCanvas(getRectangleForDice(i + 1),
					true);
			defendingDice[i] = getImageFromCanvas(getRectangleForDice(i + 1),
					false);
		}
	}

	private static BufferedImage getImageFromCanvas(Rectangle rect,
			boolean attacking) {
		BufferedImage canvas = attacking ? attackingDiceCanvas
				: defendingDiceCanvas;

		BufferedImage reference = canvas.getSubimage(rect.x, rect.y,
				rect.width, rect.height);
		return reference;

	}

	private static Rectangle getRectangleForDice(int dice) {

		int startx;
		int endx;
		int starty;
		int endy;

		double widthFactor = calculateWidthFactor(dice);
		double heightFactor = calculateHeightFactor(dice);

		startx = (int) (IMAGE_WIDTH * widthFactor);
		endx = (int) (IMAGE_WIDTH * (widthFactor + (double) 1 / 3));

		starty = (int) (IMAGE_HEIGHT * heightFactor);
		endy = (int) (IMAGE_HEIGHT * (heightFactor + (double) 1 / 2));

		int width = endx - startx;
		int height = endy - starty;

		return new Rectangle(startx, starty, width, height);
	}

	private static double calculateWidthFactor(int dice) {
		double widthFactor = -1;

		switch (dice) {
		case 1:
		case 4:
			widthFactor = (double) 0;
			break;
		case 2:
		case 5:
			widthFactor = ((double) 1) / 3;
			break;
		case 3:
		case 6:
			widthFactor = ((double) 2) / 3;
			break;
		default:
			throw new IllegalArgumentException("invalid dice! :" + dice);

		}

		return widthFactor;
	}

	private static double calculateHeightFactor(int dice) {
		return dice < 4 ? 0 : 0.5;
	}

	private static void applyAlphaMask(BufferedImage image, Color maskColor) {

	}

	/**
	 * 
	 * This is a utility method that takes in a bufferedimage and returns a new
	 * BufferedImage whose pixels are rotated to the specified number of degrees
	 * clockwise.
	 * 
	 * @param b
	 *            - The image to rotate
	 * @param degrees
	 *            - the number of degrees to rotate, CW
	 * @return a new, rotated BufferedImage
	 */
	public static BufferedImage rotateImage(BufferedImage b, double degrees) {

		Graphics2D g = b.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		AffineTransform orig = g.getTransform();

		AffineTransform transform = (AffineTransform) orig.clone();

		int centerX = b.getWidth() / 2;
		int centerY = b.getHeight() / 2;

		transform.rotate(Math.toRadians(degrees), centerX, centerY);

		BufferedImage newB = new BufferedImage(b.getWidth(), b.getHeight(),
				BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = newB.createGraphics();

//		g2.fillRect(0, 0, b.getWidth(), b.getHeight());

		g2.setTransform(transform);
		g2.drawImage(b, 0, 0, b.getWidth(), b.getHeight(), null);

		g2.setTransform(orig);

		g2.dispose();
		g.dispose();

		return newB;

	}

	public static void runTestOne() {
		JPanel panel = new JPanel();
		JButton j = new JButton();
		JButton j2 = null;

		int[] resultOne = { 3, 1, 6 };
		int[] resultTwo = { 2, 6, 6 };

		DiceRoll roll = new DiceRoll(resultOne, resultTwo);

		DiceRollAnimation d = new DiceRollAnimation(roll);

		// j.setIcon(new ImageIcon(DiceRollAnimation.getDefendingDiceImage(3)));

		for (int i = 1; i <= 6; i++) {
			j = new JButton();

			j
					.setIcon(new ImageIcon(DiceRollAnimation
							.getDefendingDiceImage(i)));

			panel.add(j);
		}

		for (int i = 1; i <= 6; i++) {
			j2 = new JButton();
			j2
					.setIcon(new ImageIcon(DiceRollAnimation
							.getAttackingDiceImage(i)));
			panel.add(j2);
		}

		JButton rotate = new JButton();
		rotate.setIcon(new ImageIcon(DiceRollAnimation.rotateImage(
				DiceRollAnimation.getDefendingDiceImage(1), 50)));

		panel.add(rotate);

		// panel.add(j);
		JFrame frame = new JFrame();
		frame.setContentPane(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		frame.setContentPane(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setVisible(true);

		runTestOne();

	}

}
