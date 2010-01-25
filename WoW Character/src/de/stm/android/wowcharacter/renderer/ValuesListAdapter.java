package de.stm.android.wowcharacter.renderer;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.view.*;
import android.widget.*;

/**
 * Adapter fuer ExpandableList (Values-Tab)
 * 
 * @author <a href="mailto:thomasfunke71@googlemail.com">Thomas Funke</a>, <a
 *         href="mailto:stefan.moldenhauer@googlemail.com">Stefan Moldenhauer</a>
 */
public class ValuesListAdapter extends BaseExpandableListAdapter {
	private Activity context;
	// children[i] contains the children (String[]) for groups[i].
	private String[] groups;
	private HashMap<Integer,ArrayList<String>> map;

	public ValuesListAdapter( Activity context ) {
		this.context = context;
	}

	public Object getChild( int groupPosition, int childPosition ) {
		ArrayList<String> al = map.get( groupPosition );
		if( al != null ) {
			return al.get( childPosition );		
		}
		return null;
	}

	public long getChildId( int groupPosition, int childPosition ) {
		return childPosition;
	}

	public int getChildrenCount( int groupPosition ) {
		ArrayList<String> al = map.get( groupPosition );
		if( al != null ) {
			return al.size();
		}
		return 0;
	}

	public void setValues( String[] groups, HashMap<Integer,ArrayList<String>> map ) {
		this.groups = groups;
		this.map = map;
	}

	public TextView getGenericView() {
		// Layout parameters for the ExpandableListView
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, 64 );
		TextView textView = new TextView( context );
		textView.setLayoutParams( lp );
		// Center the text vertically
		textView.setGravity( Gravity.CENTER_VERTICAL | Gravity.LEFT );
		// Set the text starting position
		textView.setPadding( 36, 0, 0, 0 );
		return textView;
	}

	public View getChildView( int groupPosition, int childPosition, boolean isLastChild,
			View convertView, ViewGroup parent ) {
		TextView textView = getGenericView();
		textView.setText( getChild( groupPosition, childPosition ).toString() );
		return textView;
	}

	public Object getGroup( int groupPosition ) {
		return groups[groupPosition];
	}

	public int getGroupCount() {
		if (groups != null) {
			return groups.length;
		}
		return 0;
	}

	public long getGroupId( int groupPosition ) {
		return groupPosition;
	}

	public View getGroupView( int groupPosition, boolean isExpanded, View convertView,
			ViewGroup parent ) {
		TextView textView = getGenericView();
		textView.setText( getGroup( groupPosition ).toString() );
		return textView;
	}

	public boolean isChildSelectable( int groupPosition, int childPosition ) {
		return true;
	}

	public boolean hasStableIds() {
		return true;
	}
}
