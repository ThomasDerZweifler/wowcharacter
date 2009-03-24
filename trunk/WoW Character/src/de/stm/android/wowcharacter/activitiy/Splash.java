package de.stm.android.wowcharacter.activitiy;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.stm.android.wowcharacter.R;

/**
 * Splash
 * 
 * @version $Revision:  $Date: $
 * @author <a href="mailto:tfunke@icubic.de">Thomas Funke</a>
 *
 */
public class Splash extends Activity implements OnClickListener {
    private static final int STOPSPLASH = 0;
    //time in milliseconds
    private static final long SPLASHTIME = 3000;
    //handler for splash screen
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
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

	private void init() {
		
		setContentView(R.layout.splash);				
        ((RelativeLayout) findViewById(R.id.splash)).setOnClickListener(this);
        
        TextView mTextView = (TextView) findViewById(R.id.splash_label_ver);

        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            mTextView.setText("Version: " + pi.versionName);
        } catch (NameNotFoundException e) {
            // should never happen 
        };
        
        Message msg = new Message();
        msg.what = STOPSPLASH;
        splashHandler.sendMessageDelayed(msg, SPLASHTIME);

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
		//zum Suchdialog uebergehen
		Intent intent = new Intent(this,
				de.stm.android.wowcharacter.activitiy.Characterlist.class);
		startActivity(intent);    	
		//diese Aktivitaet vom "History-Stack" nehmen
    	finish();
    }
}