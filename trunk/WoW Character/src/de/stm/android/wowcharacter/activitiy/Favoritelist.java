package de.stm.android.wowcharacter.activitiy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import de.stm.android.wowcharacter.R;
import de.stm.android.wowcharacter.data.Character;
import de.stm.android.wowcharacter.data.Model;
import de.stm.android.wowcharacter.data.Character.Data;
import de.stm.android.wowcharacter.renderer.CharacterListAdapter;

/**
 * 
 * Einstiegspunkt für Splash gefolgt von der Favoritenliste. Wenn Favoritenliste leer,
 * dann wird in die Suche verzweigt
 * 
 * @author <a href="mailto:thomasfunke71@googlemail.com">Thomas Funke</a>,
 * <a href="mailto:stefan.moldenhauer@googlemail.com">Stefan Moldenhauer</a>
 * 
 */
public class Favoritelist extends ListActivity {
	private Model model;
	protected final static int CONTEXTMENU_REMOVE_FAVORITE = 0;
	/** Bestaetigungsdialog zum Loeschen aller Character */
	private Builder alertDeleteAll;
	/** NachrichtenID zum Beenden des Splash */
	private static final int STOPSPLASH = 0;
    /** time in milliseconds */
    private static final long SPLASHTIME = 3000;
	/** Sortierrichtungen */
	public static enum SortDirection {
		ASCEND, DESCEND
	};
    private Handler splashHandler = new Handler() {
        /* (non-Javadoc)
         * @see android.os.Handler#handleMessage(android.os.Message)
         */
        @Override
        public void handleMessage(Message msg) {
             switch (msg.what) {
             case STOPSPLASH:
            	 goToCharacterList();
            	 break;
             }
             super.handleMessage(msg);
        }
   };

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		populateMenu(menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return (applyMenuChoice(item) || super.onOptionsItemSelected(item));
	}

	@Override
	protected void onStart() {
		super.onStart();
		sortAndFill("NAME", SortDirection.ASCEND);
	}

	private void init() {
		setContentView(R.layout.favoritelist);

        Message msg = new Message();
        msg.what = STOPSPLASH;
        splashHandler.sendMessageDelayed(msg, SPLASHTIME);

		initSplash();
		
		model = Model.getInstance();
		getListView().setOnCreateContextMenuListener(
				new OnCreateContextMenuListener() {
					public void onCreateContextMenu(ContextMenu cm, View view,
							ContextMenuInfo cmi) {
						Character character = (Character) getListAdapter()
								.getItem(
										((AdapterView.AdapterContextMenuInfo) cmi).position);
						cm.setHeaderTitle(character.toString());
						cm
								.add(
										0,
										CONTEXTMENU_REMOVE_FAVORITE,
										0,
										R.string.charlist_contextMenu_removeFromFavorites);
					}
				});
		alertDeleteAll = new AlertDialog.Builder(this).setTitle(R.string.warn)
				.setMessage(R.string.charlist_deleteAll).setPositiveButton(
						R.string.yes, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								model.deleteAllFavoriteCharacters();
								sortAndFill("NAME", SortDirection.ASCEND);
							}
						}).setNegativeButton(R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						});
	}

	private void initSplash() {
        ((RelativeLayout) findViewById(R.id.splash)).setOnClickListener(new OnClickListener(){
        	public void onClick(View v) {
        		goToCharacterList();
        	}
        });
        
        TextView mTextView = (TextView) findViewById(R.id.splash_label_ver);

        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            mTextView.setText("Version: " + pi.versionName);
        } catch (NameNotFoundException e) {
            // should never happen 
        };		
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Character character = (Character) getListAdapter().getItem(
				position);

		model.getInfos(character);
		goToDetails(character);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case CONTEXTMENU_REMOVE_FAVORITE:
			AdapterView.AdapterContextMenuInfo cmi = (AdapterView.AdapterContextMenuInfo) item
					.getMenuInfo();
			Character character = (Character) getListAdapter().getItem(
					cmi.position);
			boolean success = Model.getInstance().removeFavorite(character);
			if (success) {
				String s = getString(R.string.charlist_favorite_deleted_toast);
				s = s.replace("%1", character.toString());
				Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
				sortAndFill("NAME", SortDirection.ASCEND);
				return true;
			}
		}
		// TODO evtl. noch das fehlerhafte Loeschen behandeln
		return false;
	}

	/**
	 * @param menu
	 */
	private void populateMenu(Menu menu) {
		new MenuInflater(getApplication()).inflate(R.menu.characterlist, menu);
	}

	/**
	 * @param item
	 * @return
	 */
	private boolean applyMenuChoice(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.search:
			goToSearch();
			return (true);
		case R.id.sort_level_asc:
			sortAndFill("LEVEL", SortDirection.ASCEND);
			return (true);
		case R.id.sort_level_desc:
			sortAndFill("LEVEL", SortDirection.DESCEND);
			return (true);
		case R.id.sort_name_asc:
			sortAndFill("NAME", SortDirection.ASCEND);
			return (true);
		case R.id.sort_name_desc:
			sortAndFill("NAME", SortDirection.DESCEND);
			return (true);
		case R.id.clear_list:
			alertDeleteAll.show();
			return (true);
		}
		return false;
	}

	/**
	 * Nach Attribut sortierte Charaktere (Favoriten) in Liste fuellen
	 * 
	 * @param attribute
	 *            Attribut nach dem sortiert werden soll
	 */
	private void sortAndFill(final Object attribute,
			final SortDirection direction) {
		Map<String, Character> mapCharacters = model.getMapCharacters();
		Collection<Character> c = mapCharacters.values();
		ArrayList<Character> al = new ArrayList<Character>(c);
		// Comparator der Attribut zum Sortieren nutzt TODO auf/absteigende
		// Sortierung
		Comparator<Character> comp = new Comparator<Character>() {
			public int compare(Character thisObject, Character otherObject) {
				Object o = thisObject.get(Data.valueOf((String)attribute));
				int result = 0;// nicht Vertauschen
				if (o instanceof String) {
					result = o.toString().compareTo(
							otherObject.get(Data.valueOf((String)attribute)).toString());
				} else if (o instanceof Integer) {
					result = ((Integer) o).compareTo(((Integer) otherObject
							.get(Data.valueOf((String)attribute))));
				}
				if (direction == SortDirection.DESCEND) {
					result *= -1;
				}
				return result;
			}
		};
		Collections.sort(al, comp);
		// in Liste fuellen
		setListAdapter(new CharacterListAdapter(this, al));

	}

	private void goToCharacterList() {
		splashHandler.removeMessages(STOPSPLASH);
		
		ViewFlipper flipper = (ViewFlipper)findViewById(R.id.flipperView);
		flipper.setInAnimation( AnimationUtils.loadAnimation( this, R.anim.fade_in ) );
		flipper.setOutAnimation( AnimationUtils.loadAnimation( this, R.anim.fade_out ) );
		flipper.showNext();

		String sAppName = getString(R.string.app_name);
		String sTitle = getString(R.string.charlist_title);
		setTitle(sAppName + " (" + sTitle + ")");

		Map<String, Character> mapCharacters = model.getMapCharacters();

		// wenn Characterliste leer, dann gleich zur Suche springen
		if (mapCharacters.size() == 0) {
			goToSearch();
		}
	}

	/**
	 * 
	 */
	private void goToSearch() {
		Intent intent = new Intent(this, de.stm.android.wowcharacter.activitiy.Searchlist.class);
		startActivity(intent);
	}

	private void goToDetails(Character character) {
		Intent intent = new Intent(this,
				de.stm.android.wowcharacter.activitiy.Characterview.class);
		intent.putExtra(Character.ID_WOWCHARACTER, character.getKey());
		startActivity(intent);
	}
}
