package view;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

/**
 * The loading and saving panel.
 * 
 * @author Victor Atteh
 *
 */
public class FileOutputGUI extends JPanel{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3140100095778626462L;


	/**
	 * Loads the save file chooser dialog.
	 * 
	 * @param o
	 * 			the object to save.
	 */
	public static void saveObject(Object o){
		JFileChooser chooser = new JFileChooser();
		int ret = 0;
		File file = null;
		ret = chooser.showSaveDialog(new JPanel());
		file = chooser.getSelectedFile();
		ObjectRW.writeObject(o, file.getAbsolutePath()+".dat");
	}
	
	/**
	 * Loads the load file chooser dialog.
	 * 
	 * @return the loaded game object.
	 */
	public static Object getObject(){
		JFileChooser chooser = new JFileChooser();
		int ret = 0;
		File file = null;
		ret = chooser.showOpenDialog(new JPanel());
		file = chooser.getSelectedFile();
		return ObjectRW.readObject(file.getAbsolutePath());
	}
	
	
	public static void main(String[] args){
		saveObject(new java.util.ArrayList());
	}
}
