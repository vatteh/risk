package map;

import java.io.Serializable;

/**
 * This maps each territory to a unique identification number. It behaves as
 * enum. Indexing starts at 1.
 * 
 * @author Duong Pham
 * @version April 15, 2009
 * 
 */
public class Territories implements Serializable {

	// North America
	public static final int ALASKA = 1;
	public static final int ALBERTA = 2;
	public static final int CENTRAL_AMERICA = 3;
	public static final int EASTERN_UNITED_STATES = 4;
	public static final int GREENLAND = 5;
	public static final int NORTHWEST_TERRITORY = 6;
	public static final int ONTARIO = 7;
	public static final int QUEBEC = 8;
	public static final int WESTERN_UNITED_STATES = 9;

	// South America
	public static final int ARGENTINA = 10;
	public static final int BRAZIL = 11;
	public static final int PERU = 12;
	public static final int VENEZUELA = 13;
	
	// Europe
	public static final int GREAT_BRITAIN = 14;
	public static final int ICELAND = 15;
	public static final int NORTHERN_EUROPE = 16;
	public static final int SCANDINAVIA = 17;
	public static final int SOUTHERN_EUROPE = 18;
	public static final int UKRAINE = 19;
	public static final int WESTERN_EUROPE = 20;

	// Africa
	public static final int CENTRAL_AFRICA = 21;
	public static final int EAST_AFRICA = 22;
	public static final int EGYPT = 23;
	public static final int MADAGASCAR = 24;
	public static final int NORTH_AFRICA = 25;
	public static final int SOUTH_AFRICA = 26;

	// Asia
	public static final int AFGHANISTAN = 27;
	public static final int CHINA = 28;
	public static final int INDIA = 29;
	public static final int IRKUTSK = 30;
	public static final int JAPAN = 31;
	public static final int KAMCHATKA = 32;
	public static final int MIDDLE_EAST = 33;
	public static final int MONGOLIA = 34;
	public static final int SIAM = 35;
	public static final int SIBERIA = 36;
	public static final int URAL = 37;
	public static final int YAKUTSK = 38;

	// Oceania
	public static final int EASTERN_AUSTRALIA = 39;
	public static final int INDONESIA = 40;
	public static final int NEW_GUINEA = 41;
	public static final int WESTERN_AUSTRALIA = 42;
	
}
