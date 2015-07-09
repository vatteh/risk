package view;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.Map;

import base.TerritoryType;

import map.Territories;

/**
 * Maps a territory to a map of points.  This is used for click and tool-tip detection.
 * 
 * @author dthpham
 *
 */
public class PointToTerritoryMapping {

	private static int[] territoryIDs;

	private static int[][] coordinateToTerritoryIDMapping;

	/**
	 * Constructor for the PointToTerritoryMapping.
	 * 
	 * @param offscreenMap
	 * 			the offscreen map.
	 * @param originalTerritoryImageCache
	 * 			the original territory image cache.
	 * @param territorylocations
	 * 			all the territory locations.
	 */
	public PointToTerritoryMapping(BufferedImage offscreenMap,
			Map<String, BufferedImage> originalTerritoryImageCache,
			Map<String, Point> territorylocations) {

		buildCTTIDMapping(offscreenMap, originalTerritoryImageCache,
				territorylocations);

	}

	/**
	 * Builds the coordinate to territory ID mapping.
	 * 
	 * @param map
	 * 			the image of the map.
	 * @param territoryMap
	 * 			the territory map.
	 * @param territoryLocations
	 * 			the territory locations.
	 */
	protected static void buildCTTIDMapping(BufferedImage map,
			Map<String, BufferedImage> territoryMap,
			Map<String, Point> territoryLocations) {

		if (coordinateToTerritoryIDMapping != null) {
			return;
		}

		coordinateToTerritoryIDMapping = new int[map.getHeight()][map
				.getWidth()];

		for (String territoryName : territoryMap.keySet()) {
			Point p = territoryLocations.get((territoryName));
			indexTerritory(p, territoryMap.get(territoryName), territoryName
					.toUpperCase());
		}
	}

	private static void indexTerritory(Point p, BufferedImage territory,
			String correspondingField) {
		int width = territory.getWidth();
		int height = territory.getHeight();
		int x = p.x;
		int y = p.y;
		Territories instance = new Territories();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {

				int vX = x + j;
				int vY = y + i;

				int rgb = territory.getRGB(j, i);

				if (rgb != 0) {
					coordinateToTerritoryIDMapping[vY][vX] = getFieldValue(
							correspondingField, instance);
				}

			}
		}

	}

	/**
	 * Returns the name of the territory at the posx and posy.
	 * 
	 * @param x
	 * 			the x coordinate.
	 * @param y
	 * 			the y coordinate.
	 * @return  the name of the territory at the specified position.
	 */
	public static String getNameOfTerritoryAt(int x, int y) {
		return fieldValueToFieldName(getTerritoryIDAt(x, y));
	}

	private static String fieldValueToFieldName(int value) {

		if (value == -1) {
			return "ocean";
		}

		Field[] fields = Territories.class.getDeclaredFields();
		Territories instance = new Territories();
		for (Field f : fields) {
			if (getFieldValue(f.getName(), instance) == value) {
				return f.getName();
			}
		}
		throw new IllegalArgumentException(
				"INVALID FIELD VALUE FOR CLASS TERRITORIES: " + value);
	}

	private static int getTerritoryIDAt(int x, int y) {
		int res = coordinateToTerritoryIDMapping[y][x];
		if (res == 0) {
			return -1;
		}
		return res;
	}

	private static void loadTerritoryIDs() {

		if (territoryIDs == null) {
			Field[] fields = Territories.class.getDeclaredFields();
			territoryIDs = new int[fields.length];
			int curIndex = 0;
			Territories t = new Territories();
			for (Field f : fields) {

				try {
					territoryIDs[curIndex] = getFieldValue(f.getName(), t);
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception n) {
					n.printStackTrace();
				}
				curIndex++;
			}
		}
	}

	private static int getFieldValue(String f, Territories instance) {
		try {
			return Territories.class.getField(f).getInt(instance);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new IllegalArgumentException();
	}

	private static Enum getTerritoryType(String name) {
		Enum[] e = TerritoryType.class.getEnumConstants();
		for (Enum f : e) {
			if (f.toString().toLowerCase().equals(name.toLowerCase())) {
				return f;
			}

		}
		throw new IllegalArgumentException("No such Enum: " + name);
	}

	public static void main(String[] args) {
		GameMapPanel g = new GameMapPanel(null);
		PointToTerritoryMapping.buildCTTIDMapping(g.offscreenMap,
				g.originalTerritoryImageCache, g.territorylocations);
		// getTerritoryType("EAST_AFRICA");
	}

}
