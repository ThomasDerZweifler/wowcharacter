package de.stm.android.wowcharacter.util;

/**
 * 
 * Bitmap die persistiert werden kann
 * 
 * @author <a href="mailto:tfunke@icubic.de">Thomas Funke</a>
 *
 */
public class BitmapDb4o {
	private String name;
	private int width;
	private int height;
	private int[] pixels;
	
	/**
	 * 
	 * @param name
	 * @param pixels
	 * @param width
	 * @param height
	 */
	public BitmapDb4o( String name, int[] pixels, int width, int height ) {
		setName( name );
		setPixels( pixels );
		setWidth( width );
		setHeight( height );
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
