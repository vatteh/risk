package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

/**
 * Representation of the dice tray shown in the middle of the game map.
 * 
 * @author phlee
 *
 */
public class DiceTray extends JPanel implements AnimationCanvas{

	public static final int ATTACKER_DICE_TRAY = 0;
	public static final int DEFENDER_DICE_TRAY = 1;

	private static final int COLS = 3;
	public static final int HEIGHT = 60;
	public static final int WIDTH = HEIGHT*COLS;

	private ImageContainer[] images;
	
	private int diceTrayType;

	/**
	 * 
	 * @param diceTrayType - An integer indicating whether this dice tray
	 * izs ATTACKER_DICE_TRAY or DEFENDER_DICE_TRAY.
	 */
	public DiceTray(int diceTrayType){
		if(diceTrayType>1 || diceTrayType<0){
			throw new IllegalArgumentException("INVALID DICETRAYTYPE: " +diceTrayType);
		}
		
		setBorder(new LineBorder(Color.DARK_GRAY,3));
		
		this.diceTrayType = diceTrayType;
		
		setLayout(new GridLayout(0,COLS));
		setPreferredSize(new Dimension(WIDTH,HEIGHT));
		
		images = new ImageContainer[COLS];
	
		for(int i =0;i<images.length;i++){
			images[i] = new ImageContainer(null);
			add(images[i]);
			images[i].setMinimumSize(new Dimension(WIDTH/3,HEIGHT));
		}
		
	}
	
	
	/**
	 * Updates the frame.
	 * 
	 * @param im
	 * 			the list of buffered images.
	 */
	public void updateFrame(List<BufferedImage> im) {
		for(int i = 0; i<im.size();i++){
			images[i].setImage(im.get(i));
		}
		
		repaint();
		
	}
	
	/**
	 * Clears the dice tray.
	 *
	 */
	protected void clearDiceTray(){
		for(ImageContainer i : images){
			i.setImage(null);
		}
	}
	
	/**
	 * Returns the dice tray type.
	 * 
	 * @return the dice tray type.
	 */
	public int getDiceTrayType(){
		return diceTrayType;
	}
	
	/**
	 * Paints the component - unused.
	 * 
	 */
	public void paintComponent(Graphics g){
		
	}
	
	/**
	 * Testing this DiceTray module:
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		frame.setContentPane(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setSize(1000,200);
		
		frame.setLocation(new Point(500,500));
		DiceTray dt = new DiceTray(DiceTray.DEFENDER_DICE_TRAY);
		
		panel.add(dt);
		

		int[] resultOne = {6,5,1,3,3,1};
		
		int[] resultTwo = {3};

		DiceRoll roll = new DiceRoll(resultOne, resultTwo);
		
		DiceRollAnimation anim = new DiceRollAnimation(roll);
		
		
		
		frame.setVisible(true);
		
		

		DiceRollAnimator d = new DiceRollAnimator(dt, anim, 010);

		Thread t = new Thread(d);
		t.start();
		
		
		
		
		
	}
	
}
