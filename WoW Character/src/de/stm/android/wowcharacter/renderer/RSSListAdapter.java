package de.stm.android.wowcharacter.renderer;

import android.app.Activity;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.stm.android.wowcharacter.R;

/**
 * Adaptiert das Modell (ArrayList<T>) mit dem View (ItemList), ist gleichzeitig Item-Renderer <br>
 * <br>
 * Zur Datenmanipulation folgende Methoden nutzen:
 * 
 * @see #add(Object[])
 * @see #insert(Object[])
 * @see #sort(java.util.Comparator)
 * @see #remove(Object[])
 * @see #clear()
 * @author <a href="mailto:thomasfunke71@googlemail.com">Thomas Funke</a>, <a
 *         href="mailto:stefan.moldenhauer@googlemail.com">Stefan Moldenhauer</a>
 */
public class RSSListAdapter extends ArrayAdapter<Object[]> {
	private static final int res = R.layout.characterviewtabrss;
	private static final int resSelected = R.layout.characterviewtabrssselected;
	private Activity context;
	private int selectedPos = -1;

	/**
	 * @param context
	 * @param item
	 */
	public RSSListAdapter( Activity context ) {
		super( context, res );
		this.context = context;
	}

	public void setSelectedPosition(int pos){
		selectedPos = pos;
		notifyDataSetChanged();
	}

	public int getSelectedPosition(){
		return selectedPos;
	}
	
	@Override
	public View getView( int position, View convertView,
			@SuppressWarnings("unused") ViewGroup parent ) {
		View row = convertView;
//		if (row == null) {
			LayoutInflater inflater = context.getLayoutInflater();			
			row = inflater.inflate( (selectedPos == position) ? resSelected : res, null );
//		}
		if (getCount() > position) {
			Object[] itemValues = getItem( position );
			TextView title = (TextView)row.findViewById( R.id.rssTitle );
			title.setText( itemValues[0].toString() );
			TextView datetimestamp = (TextView)row.findViewById( R.id.rssDatetimestamp );
			datetimestamp.setText( itemValues[1].toString() );
			TextView content = (TextView)row.findViewById( R.id.rssContent );
			String text = itemValues[2].toString();
			Spanned s = Html.fromHtml( text );
			content.setText( s );
			content.setMovementMethod( LinkMovementMethod.getInstance() );
		}
		return row;
	}
}
