package br.com.medicalservices.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


public class Splash extends Activity {

	private boolean mIsBackButtonPressed;
	private static final int SPLASH_DURATION = 2000; // 3 seconds
	SessionManager session;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		
		session = new SessionManager(this);
		DataManager.username = session.getuserid();
		
						
		Handler handler = new Handler();

		// run a thread after 2 seconds to start the home screen
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {

				if (!mIsBackButtonPressed) {

	
					if(session.isLoggedIn())
					{
					Intent i = new Intent(Splash.this, MainActivity.class);
					i.setAction("splash");
					finish();
					startActivity(i);
					overridePendingTransition(0, 0);
					}else
					{
						Intent i = new Intent(Splash.this, LoginActivity.class);
						finish();
						startActivity(i);
						overridePendingTransition(0, 0);
					}
				}
			}

		}, SPLASH_DURATION);
		
		
		
	}
	
	
	
}
