package de.stm.android.wow.character;

import java.util.EventListener;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Splash {
	private Wowcharacter parent;
	
	public Splash (Wowcharacter parent) {
		this.parent = parent;
		init();
	}
	
	private void init() {
		parent.setContentView(R.layout.splash);				
        ((RelativeLayout) parent.findViewById(R.id.splash)).setOnClickListener(mSplashClickListener);
        
        TextView mTextView = (TextView) parent.findViewById(R.id.splash_label_ver);

        try {
            PackageInfo pi = parent.getPackageManager().getPackageInfo(parent.getPackageName(), 0);
            mTextView.setText("Version: " + pi.versionName);
        } catch (NameNotFoundException e) {
            // should never happen 
        }; 
	}
	
    OnClickListener mSplashClickListener = new OnClickListener() {
        public void onClick(View v) {
        	EventListener l = parent.getListener();
        }
    };
}