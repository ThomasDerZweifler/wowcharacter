package de.stm.android.wow.character;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class Characterlist extends ListActivity {
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
		return(applyMenuChoice(item) ||
		super.onOptionsItemSelected(item));
	}

	private void init() {
		setContentView(R.layout.characterlist);
		// TODO wenn Characterliste leer, dann gleich zur Suche springen
	}

	private void populateMenu(Menu menu) {
		new MenuInflater(getApplication()).inflate(R.menu.characterlist, menu);
	}
	
	private boolean applyMenuChoice(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.search:	
			goToSearch();
			return(true);
		case R.id.clear_list:
			// TODO Funktion zur Löschung der Characterliste erstellen
			return(true);
		}
		return false;
	}

	private void goToSearch() {
		Intent intent = new Intent(this,
				de.stm.android.wow.character.Search.class);
		startActivity(intent);    	
	}
}
