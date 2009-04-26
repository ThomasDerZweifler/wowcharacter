package de.stm.android.wowcharacter.data;

/**
 * 
 * Itemdetails
 * 
 * @author <a href="mailto:stefan.moldenhauer@googlemail.com">Stefan Moldenhauer</a>
 * 
 */
public class Item {
	private int id;
	private String iconname;
	private String itemname;
	private int quality;
	private int level;
	
	public Item(int id, String iconname, String itemname, int quality, int level) {
		this.id = id;
		this.iconname = iconname;
		this.itemname = itemname;
		this.quality = quality;
		this.level = level;
	}

	public int getId() {
		return id;
	}

	public String getIconname() {
		return iconname;
	}

	public String getItemname() {
		return itemname;
	}

	public int getQuality() {
		return quality;
	}

	public int getLevel() {
		return level;
	}
}