package de.stm.android.wowcharacter.activitiy;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ViewFlipper;
import de.stm.android.wowcharacter.R;

/**
 * 
 * 
 * @version $Revision:  $Date: $
 * @author <a href="mailto:tfunke@icubic.de">Thomas Funke</a>
 *
 *
 * in "AndroidManifest.xml" .activity.Main statt .activity.Splash eintragen:
 * <application android:icon="@drawable/icon" android:label="@string/app_name">
        <activity android:screenOrientation="sensor" android:label="@string/app_name"
        	android:icon="@drawable/icon"
        	android:name=".activitiy.Main"> 
 			 <intent-filter> 
   				<action android:name="android.intent.action.MAIN"/> 
   				<category android:name="android.intent.category.LAUNCHER"/> 
 			 </intent-filter> 
 		</activity> ...
 */
public class Main extends Activity {
	private ViewFlipper viewFlipper;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		
		if (savedInstanceState == null) {
			//enter first
		} else {
			//come back
		}

		setContentView( R.layout.main );
		
		View v = findViewById( R.id.flipperView );
		
		viewFlipper = (ViewFlipper)findViewById( R.id.flipperView );
		viewFlipper.setInAnimation( AnimationUtils.loadAnimation( this, R.anim.rotate ) );
		viewFlipper.setOutAnimation( AnimationUtils.loadAnimation( this, R.anim.slide_left_out ) );

		Button btn = new Button( this );
		btn.setText( "erster view" );
		viewFlipper.addView( btn );

		setContentView( R.layout.splash );
		View splash = findViewById( R.id.splash );
		setContentView( v );
		viewFlipper.addView( splash );

		setContentView( R.layout.search );
		View v1 = findViewById( R.id.searchview );
		setContentView( v );
		viewFlipper.addView( v1 );

		setContentView( R.layout.characterlist );
		View v2 = findViewById( R.id.characterlist );
		setContentView( v );
		viewFlipper.addView( v2 );

		
//		  View v = activity.getViewInflate().inflate(R.layout.buttons, null, null);
//		  Button b = (Button)v.findViewById(R.id.button_small_left);
//		setContentView( R.layout.characterview );
		
//		View v3 = findViewById( R.id.characterDetails );//not found!
//		viewFlipper.addView( v3 );

		viewFlipper.setFlipInterval( 2000 );
		viewFlipper.startFlipping();
		
		super.onCreate( savedInstanceState );

	}
}
