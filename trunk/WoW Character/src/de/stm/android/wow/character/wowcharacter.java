package de.stm.android.wow.character;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class wowcharacter extends Activity {

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        ((RelativeLayout) findViewById(R.id.splash)).setOnClickListener(mSplashClickListener);
        
        TextView mTextView = (TextView) findViewById(R.id.splash_label02);

        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            mTextView.setText("Version: " + pi.versionName);
        } catch (NameNotFoundException e) {
            // should never happen 
        }; 
    }

    OnClickListener mSplashClickListener = new OnClickListener() {
        public void onClick(View v) {
            setContentView(R.layout.search);
        }
    };

}