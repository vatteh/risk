package view;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;

/**
 * Represents all of the possible Territory colors used in this game.
 * 
 * @author Phil
 * 
 */
public class AvailableColors {

	private static final int darknessAdjust = -10;

	public static final Color ONE = new Color(0, 128 + darknessAdjust, 0);

	public static final Color TWO = new Color(105 + darknessAdjust, 25, 0);

	public static final Color THREE = new Color(19 + darknessAdjust,
			13 + darknessAdjust, 132 + darknessAdjust);

	public static final Color FOUR = new Color(170 + darknessAdjust,
			104 + darknessAdjust, 15 + darknessAdjust);

	public static final Color FIVE = Color.black;

	public static final Color SIX = new Color(77 + darknessAdjust,
			30 + darknessAdjust, 102 + darknessAdjust);

	/**
	 * Returns a list of all of the available colors
	 * 
	 * @return list of all available colors.
	 */
	public static List<Color> getAllAvailableColors() {
		List<Color> list = new ArrayList(6);
		Field[] fs = AvailableColors.class.getDeclaredFields();
		for (int i = 0; i < fs.length; i++) {
			Field f = fs[i];
			Color next = null;

			try {
				Object o = f.get(f.getName());
				if (o instanceof Color) {
					next = (Color) o;
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (next != null)
				list.add(next);
		}
		return list;
	}

	/**
	 * Creates a new Frame that displays all of the available territory colors
	 * 
	 * @param args
	 *            - Command line arguments
	 */
	public static void main(String[] args) {
		JFrame j = new JFrame();
		javax.swing.JPanel p = new javax.swing.JPanel();
		JButton b = null;
		j.setContentPane(p);
		for (Color c : getAllAvailableColors()) {
			b = new JButton();
			p.add(b);
			b.setBackground(c);
		}
		j.pack();
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		j.setVisible(true);
	}

}
