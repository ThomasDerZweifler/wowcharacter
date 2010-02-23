package de.stm.android.wowcharacter.renderer;

import android.app.Activity;
import android.text.Html;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.*;
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
	private Activity context;

	/**
	 * @param context
	 * @param item
	 */
	public RSSListAdapter( Activity context ) {
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
			TextView title = (TextView)row.findViewById( R.id.rssTitle );
			title.setText( itemValues[0].toString() );
			TextView datetimestamp = (TextView)row.findViewById( R.id.rssDatetimestamp );
			datetimestamp.setText( itemValues[1].toString() );
			TextView content = (TextView)row.findViewById( R.id.rssContent );
			String text = itemValues[2].toString();
			content.setText( text );
			// Linkify.addLinks(content,Linkify.WEB_URLS);
			Spanned s = Html.fromHtml( text );
			URLSpan[] urlSpans = content.getUrls();
			for (URLSpan urlSpan : urlSpans) {
				String url = urlSpan.getURL();
				Log.i("url:",url);
			}
			// content.setText( s );
		}
		return row;
	}
}
