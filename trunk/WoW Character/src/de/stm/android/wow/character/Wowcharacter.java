package de.stm.android.wow.character;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Wowcharacter extends Activity implements Constants {

    protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
    	
    	if (requestCode == SPLASH_CLICKED) {
            if (resultCode == RESULT_OK) {
	    		setContentView(R.layout.search);
            }
        }
    }

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, de.stm.android.wow.character.Splash.class);
        startActivityForResult(intent, SPLASH_CLICKED);
    }
    
}