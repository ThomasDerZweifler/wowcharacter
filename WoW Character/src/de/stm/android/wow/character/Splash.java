package de.stm.android.wow.character;

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
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent ev) {
//
//		Log.i("id", "onKeyDown");
//		switch (keyCode) {
//		case KeyEvent.KEYCODE_BACK:
//	        setResult(RESULT_CANCELED);
//	        finish();
//			break;
//		default:
//			return false;
//		}
//		return true;
//	}
    
    /**
     * 
     */
    public void onClick(View v) {
		//zum Suchdialog uebergehen
		Intent intent = new Intent(this,
				de.stm.android.wow.character.Search.class);
		startActivity(intent);
		//diese Aktivitaet vom "History-Stack" nehmen
    	finish();
    }
}