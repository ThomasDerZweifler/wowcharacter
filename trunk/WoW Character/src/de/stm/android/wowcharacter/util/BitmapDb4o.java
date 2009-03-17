package de.stm.android.wowcharacter.util;

public class BitmapDb4o {
	private String name;
	private int width;
	private int height;
	private int[] pixels;
	
	public BitmapDb4o( String name, int[] pixels, int width, int height ) {
		this.name = name;
		this.pixels = pixels;
		this.width = width;
		this.height = height;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth( int width ) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight( int height ) {
		this.height = height;
	}

	public int[] getPixels() {
		return pixels;
	}

	public void setPixels( int[] pixels ) {
		this.pixels = pixels;
	}

}
