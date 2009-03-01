package de.stm.android.wowcharacter.activitiy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import de.stm.android.wowcharacter.R;
import de.stm.android.wowcharacter.data.Model;
import de.stm.android.wowcharacter.data.WOWCharacter;

public class Characterlist extends ListActivity {
	Model model;

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

	public boolean onOptionsItemSelected(MenuItem item) {
		return (applyMenuChoice(item) || super.onOptionsItemSelected(item));
	}

	private void init() {
		setContentView(R.layout.characterlist);

		model = Model.getInstance();
		
		Map<String,WOWCharacter> mapCharacters = model.getMapCharacters();
		
		// TODO wenn Characterliste leer, dann gleich zur Suche springen
		if (mapCharacters.size() == 0) {
			goToSearch();
		} else {
			sortAndFill("NAME");
		}
	}

	private void populateMenu(Menu menu) {
		new MenuInflater(getApplication()).inflate(R.menu.characterlist, menu);
	}

	private boolean applyMenuChoice(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.search:
			goToSearch();
			return (true);
		case R.id.sort_level:
			sortAndFill("LEVEL");
			return (true);
		case R.id.sort_name:
			sortAndFill("NAME");
			return (true);
		case R.id.clear_list:
			model.deleteAllFavoriteCharacters();
			sortAndFill("NAME");
			return (true);
		}
		return false;
	}

	/**
	 * Nach Attribut sortierte Charaktere (Favoriten) in Liste fuellen
	 * 
	 * @param attribute Attribut nach dem sortiert werden soll
	 */
	private void sortAndFill( final Object attribute ) {
		Map<String,WOWCharacter> mapCharacters = model.getMapCharacters();
		Collection<WOWCharacter> c = mapCharacters.values();
		ArrayList<WOWCharacter> al = new ArrayList<WOWCharacter>(c);

		//Comparator der Attribut zum Sortieren nutzt    TODO auf/absteigende Sortierung
		Comparator<WOWCharacter> comp = new Comparator<WOWCharacter>() {
			public int compare(WOWCharacter thisObject, WOWCharacter otherObject) {
				Object o = thisObject.get(attribute);
				if( o instanceof String ) {
					return o.toString().compareTo(otherObject.get(attribute).toString());					
				} else if( o instanceof Integer ) {
					return ((Integer)o).compareTo(((Integer)otherObject.get(attribute)));										
				}
				return 0;
			}
		};

		Collections.sort(al,comp);
		
		WOWCharacter[] a = al.toArray(new WOWCharacter[al.size()] );
		
		//in Liste fuellen
		setListAdapter(new ArrayAdapter<WOWCharacter>(this, 
				android.R.layout.simple_list_item_1,
				a));

	}
	
	private void goToSearch() {
		Intent intent = new Intent(this,
				de.stm.android.wowcharacter.activitiy.Search.class);
		startActivity(intent);
	}
}
