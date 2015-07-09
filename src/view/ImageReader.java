package view;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Reads an image.  It is like a facade because it makes it easier to read images.
 * 
 * @author Phil
 *
 */
public class ImageReader {

	public static BufferedImage readImage(String filepath){
		try {
			return ImageIO.read(new File(filepath));
		} catch (IOException e) {
			System.out.println("CANT READ: " + filepath);
			e.printStackTrace();
		}
		
		return null;
	}
	
}
