package de.stm.android.wow.character;

import android.app.Activity;
import android.os.Bundle;

public class Wowcharacter extends Activity implements Constants {

	// Listen for results.
	protected void request(int requestCode, int resultCode, Bundle data){
	    // See which child activity is calling us back.
	    switch (requestCode) {
	        case SPLASH_CLICKED:
	        	System.out.println("splash clicked.");
	        	data.getString("param1");
	    		setContentView(R.layout.search);

	        default:
	            break;
	    }
	}

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Splash(this);
    }
    
}