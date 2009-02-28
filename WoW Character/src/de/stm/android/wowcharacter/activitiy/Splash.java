package de.stm.android.wowcharacter.activitiy;

import java.util.Timer;
import java.util.TimerTask;

import de.stm.android.wowcharacter.R;
import de.stm.android.wowcharacter.R.id;
import de.stm.android.wowcharacter.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Splash
 * 
 * @author tfunke
 *
 */
public class Splash extends Activity implements OnClickListener {
	private Timer timer;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

	private void init() {
		timer = new Timer("WOW-Timer");
		TimerTask timerTask = new TimerTask() {
			public void run() {
				goToCharacterList();
			}
		};
		timer.schedule(timerTask, 5000);
		
		setContentView(R.layout.splash);				
        ((RelativeLayout) findViewById(R.id.splash)).setOnClickListener(this);
        
        TextView mTextView = (TextView) findViewById(R.id.splash_label_ver);

        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            mTextView.setText("Version: " + pi.versionName);
        } catch (NameNotFoundException e) {
            // should never happen 
        }; 
	}
	  
    /**
     * 
     */
    public void onClick(View v) {
    	goToCharacterList();
    }

    /**
     *
     */
	private void goToCharacterList() {
    	timer.cancel();
		//zum Suchdialog uebergehen
		Intent intent = new Intent(this,
				de.stm.android.wowcharacter.activitiy.Characterlist.class);
		startActivity(intent);    	
		//diese Aktivitaet vom "History-Stack" nehmen
    	finish();
    }
}