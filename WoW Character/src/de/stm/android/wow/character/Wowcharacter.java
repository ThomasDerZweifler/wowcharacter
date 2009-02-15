package de.stm.android.wow.character;


import java.util.EventListener;

import android.app.Activity;
import android.os.Bundle;

public class Wowcharacter extends Activity {
	public Wowcharacter() {
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		initListener();
	}

	private void initListener() {
		// TODO Auto-generated method stub
	}

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Splash(this);
    }
    
	public EventListener getListener() {
		// TODO Auto-generated method stub
		return null;
	}
}