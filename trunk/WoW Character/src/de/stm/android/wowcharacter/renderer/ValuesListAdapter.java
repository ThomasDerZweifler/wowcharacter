package de.stm.android.wowcharacter.renderer;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.view.*;
import android.widget.*;
import de.stm.android.wowcharacter.R;

/**
 * Adapter fuer ExpandableList (Values-Tab)
 * 
 * @author <a href="mailto:thomasfunke71@googlemail.com">Thomas Funke</a>, <a
 *         href="mailto:stefan.moldenhauer@googlemail.com">Stefan Moldenhauer</a>
 */
public class ValuesListAdapter extends BaseExpandableListAdapter {
	private static final int resParent = R.layout.valueslistparentitem;
	private static final int resChild = R.layout.valueslistchilditem;	
	private Activity context;
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

	public View getChildView( int groupPosition, int childPosition, boolean isLastChild,
			View convertView, ViewGroup parent ) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			view = inflater.inflate( resChild, null );
		}
		TextView row = (TextView)view.findViewById( R.id.rowChild );
		row.setText( getChild( groupPosition, childPosition ).toString() );
		return view;
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
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			view = inflater.inflate( resParent, null );
		}
		TextView row = (TextView)view.findViewById( R.id.rowParent );
		row.setText( getGroup( groupPosition ).toString() );
		return view;
	}

	public boolean isChildSelectable( int groupPosition, int childPosition ) {
		return true;
	}

	public boolean hasStableIds() {
		return true;
	}
}
