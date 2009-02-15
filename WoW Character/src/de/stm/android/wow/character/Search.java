package de.stm.android.wow.character;

import android.app.Activity;
import android.os.Bundle;

public class Search extends Activity implements Constants {
	private void init() {
		setContentView(R.layout.search);				
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

}
