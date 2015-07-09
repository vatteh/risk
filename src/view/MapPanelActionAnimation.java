package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import base.Stage;

/**
 * 
 * The action animation that is displayed on the map panel when a territory
 * attacks another territory.  This class implements the Animation interface.
 * 
 * @author Phil
 *
 */
public class MapPanelActionAnimation implements Animation {

	List<BufferedImage> sequence;
	protected static final int ATTACK_ANIMATION_FRAMES = 20;
	private static final int FORTIFY_ANIMATION_FRAMES = 10;

	private static final byte ACTION_REINFORCE = 0;
	private static final byte ACTION_ATTACK = 1;
	private static final byte ACTION_FORTIFY = 2;

	public static int size = 150;
	public static final int REINFORCEMENT_SIZE = 100;
	private static final int default_length = size / 3;

	private static final Color attackColor = Color.red;
	private static final Color reinforceColor = new Color(0, 183, 239);

	private static final String DIR = "animations/mapanimations";
	private static final String REINFORCE_DIR = DIR + "/reinforce.png";

	protected Enum<Stage> thisAction;

	private int thisEvent;

	private Point displacement;

	public static final int CONQUER = 1;

	private double angle;

	/**
	 * Constructor.
	 * 
	 * @param displacement
	 * 			the spacing vector of the arrow.
	 * @param action
	 * 			the stage of the game.
	 */
	protected MapPanelActionAnimation(Point displacement, Enum<Stage> action) {
		thisAction = action;
		if (action.equals(Stage.ATTACK) || action.equals(Stage.FORTIFY)) {
			this.displacement = displacement;
			double d[] = unitVector(displacement);
			d[0] *= (size / 2);
			d[1] *= (size / 2);
			displacement.x = (int) d[0];
			displacement.y = (int) d[1];
			angle = calculateAngle2();
		}
		sequence = buildAnimationSequence();
	}

	/**
	 * Builds the animation sequence when an event occurs.
	 * 
	 * @param thisEvent
	 * 			the event number.
	 */
	protected MapPanelActionAnimation(int thisEvent) {

		this.thisEvent = thisEvent;
		sequence = buildAnimationSequence();
		thisAction = null;
	}

	private List<BufferedImage> buildAnimationSequence() {

		if (thisAction != null) {

			if (thisAction.equals(Stage.ATTACK)) {
				return buildArrowAnimationSequence(0);
			} else if (thisAction.equals(Stage.REINFORCEMENTS)) {
				return buildOrbAnimationSequence2(reinforceColor, "REINFORCE!");
			} else if (thisAction.equals(Stage.FORTIFY)) {
				return buildArrowAnimationSequence(1);
			}
		} else {

			switch (thisEvent) {
			case CONQUER: {
				return buildOrbAnimationSequence2(Color.pink, "CONQUERED!");
			}
			}
		}

		throw new IllegalArgumentException("INVALID STAGE PARAMETER: "
				+ thisAction.toString());

	}

	private List<BufferedImage> buildOrbAnimationSequence2(Color color, String s) {
		List<BufferedImage> animationSequence = new ArrayList();
		BufferedImage newImage = null;
		Graphics2D g = null;
		int width = REINFORCEMENT_SIZE / 3;
		int height = REINFORCEMENT_SIZE / 3;
		int startX = width;
		int startY = height;
		for (int i = 0; i < 20; i++) {
			newImage = new BufferedImage(REINFORCEMENT_SIZE,
					REINFORCEMENT_SIZE, BufferedImage.TYPE_INT_ARGB);
			g = newImage.createGraphics();
			g.setStroke(new BasicStroke(2));
			;
			g.setColor(color);
			g.setFont(new Font("Arial", Font.BOLD, 14));
			g.drawString(s, 10, 10);
			g.drawOval(startX, startY, width, height);
			g.dispose();
			animationSequence.add(newImage);
			width -= 2;
			height -= 2;
			startX += 1;
			startY += 1;
		}
		return animationSequence;
	}

	private List<BufferedImage> buildReinforceAnimationSequence() {
		List<BufferedImage> animationSequence = new ArrayList();
		BufferedImage theImage = ImageReader.readImage(REINFORCE_DIR);
		BufferedImage newImage = new BufferedImage(REINFORCEMENT_SIZE,
				REINFORCEMENT_SIZE, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = newImage.createGraphics();

		g.setColor(reinforceColor);
		Font f = g.getFont();
		g.setFont(new Font(f.getName(), Font.BOLD, 8));
		g.drawString("REINFORCE!", 0, 10);
		int width = newImage.getWidth();
		int height = newImage.getHeight();
		g.drawImage(theImage, width / 3, height / 3, width * 2 / 3,
				height * 2 / 3, null);
		g.dispose();
		animationSequence.add(newImage);
		return animationSequence;
	}

	private List<BufferedImage> buildArrowAnimationSequence(int id) {
		List<BufferedImage> animationSequence = new ArrayList();

		BufferedImage nextImage = null;
		Graphics2D g = null;

		double theta = 150;

		Point origin = new Point(size / 2, size / 2);
		Point end = displacement;

		Point finalPos = new Point(end.x + origin.x, ((origin.y - end.y)));

		Point finalHeadPos1 = findFinalHeadPos(theta, finalPos);
		Point finalHeadPos2 = findFinalHeadPos(-theta, finalPos);

		int increment = 360 / ATTACK_ANIMATION_FRAMES;

		String message = "";
		switch (id) {
		case 0:
			message = "ATTACKING!";
			break;
		case 1:
			message = "FORTIFYING!";
		}

		for (int i = 0; i < ATTACK_ANIMATION_FRAMES; i++) {
			nextImage = new BufferedImage(size + 10, size + 10,
					BufferedImage.TYPE_INT_ARGB);
			g = nextImage.createGraphics();

			Font f = g.getFont();
			g.setFont(new Font(f.getName(), Font.BOLD, f.getSize()));

			switch (id) {

			case 0:
				g.setColor(attackColor);
				break;
			case 1:
				g.setColor(reinforceColor);
			}

			g.drawString(message, 20, 20);
			g.setStroke(new BasicStroke(2));
			;
			g.drawLine(origin.x, origin.y, finalPos.x, finalPos.y);
			g
					.drawLine(finalPos.x, finalPos.y, finalHeadPos1.x,
							finalHeadPos1.y);
			g
					.drawLine(finalPos.x, finalPos.y, finalHeadPos2.x,
							finalHeadPos2.y);

			if (theta == 180) {
				increment -= increment;
			} else if (theta == 90) {
				increment -= increment;
			}

			theta += increment;

			finalHeadPos1 = findFinalHeadPos(theta, finalPos);
			finalHeadPos2 = findFinalHeadPos(-theta, finalPos);
			animationSequence.add(nextImage);
		}
		g.dispose();
		return animationSequence;
	}

	private Point findFinalHeadPos(double theta, Point ref) {
		int headLength = default_length / 4;
		Point head1 = calculateDisplacementForTheta(angle + theta, headLength);
		Point finalHeadPos1 = new Point(ref.x + head1.x, ref.y - head1.y);
		return finalHeadPos1;
	}

	private Point calculateDisplacementForTheta(double theta, int factor) {
		int dx = (int) (Math.cos(Math.toRadians(theta)) * factor);
		int dy = (int) (Math.sin(Math.toRadians(theta)) * factor);
		return new Point(dx, dy);
	}

	private double calculateLength() {
		return lengthOfVector(displacement);

	}

	/**
	 * Returns the length of the vector for the spacing of the vector.
	 * 
	 * @param displacement
	 * 			the spacing of the vector.
	 * @return  the length of the vector.
	 */
	public static double lengthOfVector(Point displacement) {
		return Math.sqrt(Math.pow(displacement.x, 2)
				+ Math.pow(displacement.y, 2));
	}

	private double calculateAngle() {
		if (displacement.x == 0) {
			if (displacement.y > 0) {
				return 90;
			} else {
				return 270;
			}
		}
		double ratio = ((double) displacement.y) / displacement.x;
		return Math.toDegrees(Math.atan(ratio));
	}

	private double calculateAngle2() {
		return Math.toDegrees(Math.atan(((double) displacement.y)
				/ displacement.x));

	}

	/**
	 * Returns the animation sequence after it has been built.
	 * 
	 * @param i
	 * 			the animation sequence at this index.
	 */
	public List<BufferedImage>[] getAnimationSequence(int i) {
		List<BufferedImage>[] result = new List[1];
		result[0] = sequence;
		return result;
	}

	private static double[] unitVector(Point p) {
		double sum = Math.pow(p.x, 2) + Math.pow(p.y, 2);
		double fac = Math.sqrt(sum);
		double[] d = new double[2];
		d[0] = ((double) p.x / fac);
		d[1] = ((double) p.y / fac);
		return d;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		panel.setBackground(Color.black);
		frame.setContentPane(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MapPanelActionAnimation mpa = new MapPanelActionAnimation(new Point(50,
				-0), Stage.FORTIFY);
		List<BufferedImage> seq = mpa.buildAnimationSequence();
		for (int i = 0; i < ATTACK_ANIMATION_FRAMES; i++) {
			JButton j = new JButton();

			j.setBackground(Color.black);
			j.setIcon(new ImageIcon(seq.get(i)));
			panel.add(j);
		}
		frame.pack();
		frame.setVisible(true);
	}

}
