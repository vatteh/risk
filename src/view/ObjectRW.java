package view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Writes an object to an ObjectOutputStream.  This is used for serialization/saving.
 * 
 * @author Phil
 *
 */
public class ObjectRW {

	/**
	 * Constructor for the ObjectRW.
	 * 
	 * @param o
	 * 			the object to write.
	 * @param file
	 * 			the file to write to.
	 */
	public static void writeObject(Object o, String file){
		ObjectOutputStream oos = null;
		
		try {
			oos = new ObjectOutputStream(new FileOutputStream(new File(file)));
			oos.writeObject(o);
		}catch(Exception e){
			e.printStackTrace();
			throw new IllegalArgumentException("problem saving: " + o.getClass().getName());
		}
		
		try {
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads a saved object using an ObjectInputStream.
	 * 
	 * @param file
	 * 			the file to open.
	 * @return  the object loaded from the saved object file.
	 */
	public static Object readObject(String file){
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(new File(file)));
			return ois.readObject();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(ClassNotFoundException c){
			c.printStackTrace();
		}
		System.exit(1);
		
		return null;
	}
	
	
}
