package de.stm.android.wowcharacter.renderer;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.*;
import android.widget.*;
import de.stm.android.wowcharacter.R;

/**
 * Renderer fuer Zeilen des ItemsListViews
 * 
 * @author <a href="mailto:thomasfunke71@googlemail.com">Thomas Funke</a>, <a
 *         href="mailto:stefan.moldenhauer@googlemail.com">Stefan Moldenhauer</a>
 */
@SuppressWarnings("unchecked")
public class ItemListAdapter extends ArrayAdapter {
	private static final int res = R.layout.characterviewtabitem;
	private Activity context;
	private ArrayList<Object[]> item;

	/**
	 * @param context
	 * @param item
	 */
	public ItemListAdapter( Activity context, ArrayList<Object[]> item ) {
		super( context, res, item );
		this.context = context;
		this.item = item;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate( res, null );
		}
		Object[] itemValues = item.get( position );
		ImageView itemImage = (ImageView)row.findViewById( R.id.ItemImage );
		Object o = itemValues[0];
		if(o instanceof Bitmap) {
			itemImage.setImageBitmap(  (Bitmap)o );
		}
		TextView name = (TextView)row.findViewById( R.id.ItemName );
		name.setText( itemValues[1].toString() );
		TextView level = (TextView)row.findViewById( R.id.ItemLevel );
		level.setText( "(Level: " + itemValues[2].toString() + ")" );

		return row;
	}
}
