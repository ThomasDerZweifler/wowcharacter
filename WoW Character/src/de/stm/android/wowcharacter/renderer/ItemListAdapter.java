package de.stm.android.wowcharacter.renderer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import de.stm.android.wowcharacter.R;

/**
 * Adaptiert das Modell (ArrayList<T>) mit dem View (ItemList), ist gleichzeitig Item-Renderer
 *
 * <br><br>Zur Datenmanipulation folgende Methoden nutzen:
 * 
 * @see #add(Object[])
 * @see #insert(Object[])
 * @see #sort(java.util.Comparator)
 * @see #remove(Object[])
 * @see #clear()
 * 
 * @author <a href="mailto:thomasfunke71@googlemail.com">Thomas Funke</a>, <a
 *         href="mailto:stefan.moldenhauer@googlemail.com">Stefan Moldenhauer</a>
 */
public class ItemListAdapter extends ArrayAdapter<Object[]> {
	private static final int res = R.layout.characterviewtabitem;
	private Activity context;

	/**
	 * @param context
	 * @param item
	 */
	public ItemListAdapter( Activity context ) {
		super( context, res );
		this.context = context;
	}

	@Override
	public View getView( int position, View convertView,
			@SuppressWarnings("unused") ViewGroup parent ) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate( res, null );
		}
		if (getCount() > position) {
			Object[] itemValues = getItem( position );
			String quality = itemValues[3].toString();//fuer Farbe
			ImageView itemImage = (ImageView)row.findViewById( R.id.ItemImage );
			Object o = itemValues[0];
			if (o instanceof Bitmap) {
				itemImage.setImageBitmap( (Bitmap)o );
				itemImage.setAlpha(220);
				itemImage.setFadingEdgeLength(20);
				itemImage.setBackgroundColor( Color.BLUE );
			}
			TextView name = (TextView)row.findViewById( R.id.ItemName );
			name.setText( itemValues[1].toString() );
			name.setTextColor( Color.BLUE );
			TextView level = (TextView)row.findViewById( R.id.ItemLevel );
			level.setText( "(Level: " + itemValues[2].toString() + ")" );
		}
		return row;
	}
}
