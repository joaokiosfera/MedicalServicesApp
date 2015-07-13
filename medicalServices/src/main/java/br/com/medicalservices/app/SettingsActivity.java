package br.com.medicalservices.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import java.io.File;

public class SettingsActivity extends PreferenceActivity implements ConnectionCallbacks,
OnConnectionFailedListener{

	public static SettingsActivity newInstance() {
		return new SettingsActivity();
	}
	SessionManager session;
	String username, status = "", deviceid;
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.prefs);

		session = new SessionManager(this);

		username = session.getuserid();
		deviceid = session.getdeviceid();
		
	
		
		// Display value of selected end trail

		// set default GPS checkbox to true and set Enable false.
		CheckBoxPreference cbgps = (CheckBoxPreference) findPreference("cbgps");

		cbgps.setDefaultValue(true);
		cbgps.setEnabled(false);

		// Logout button.. Show Alert
		Preference button = (Preference) findPreference("button");
		button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {

				logout();

				return true;
			}
		});

		final EditTextPreference etstatus = (EditTextPreference) findPreference("status");

		etstatus.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				// TODO Auto-generated method stub
				
				status = newValue.toString();
				
				System.out.println("status---"+status);
				
				new updatestatus().execute();
				
				return true;
			}
		});
		SharedPreferences prefs;
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		String twitterusername = prefs.getString("status", "");

		etstatus.setSummary(twitterusername);
		etstatus.setText(twitterusername);

		// Twitter checkbox only can enable if Twitter username has entered.

	}

	// Logout alert
	public void logout() {

		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Logout"); // Set Alert dialog title
									// here
		alert.setMessage("Are you sure want to logout?"); // Message here

		alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			
								
				new logoutuser().execute();
			
			}
		});
		alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				dialog.cancel();

			}
		});
		AlertDialog alertDialog = alert.create();
		alertDialog.show();

	}


	
	private class updatestatus extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {

			APIManager.updatestatus(username, status, deviceid);

			return "Executed";
		}

		@Override
		protected void onPostExecute(String result) {

			if(DataManager.status.equals("false"))
			{
				session.logoutUser();
			}else
			{
			Toast.makeText(SettingsActivity.this, "Status Updated Successfully..", Toast.LENGTH_LONG).show();
			Intent i = new Intent(SettingsActivity.this, MainActivity.class);
			i.setAction("settings");
			startActivity(i);
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			
			
		}
	}
	
	private class logoutuser extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {

			APIManager.logout(username, deviceid);

			return "Executed";
		}

		@Override
		protected void onPostExecute(String result) {

			if(DataManager.status.equals("1"))
			{
				
				 session.logoutUser();
				 clearApplicationData();
				
			Toast.makeText(SettingsActivity.this, "Logout Successfully..", Toast.LENGTH_LONG).show();
			Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
			startActivity(i);
		
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			
			
		}
	}

	public void clearApplicationData() 
	{
	    File cache = this.getCacheDir();
	    File appDir = new File(cache.getParent());
	    if (appDir.exists()) {
	        String[] children = appDir.list();
	        for (String s : children) {
	            if (!s.equals("lib")) {
	                deleteDir(new File(appDir, s));Log.i("TAG", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
	            }
	        }
	    }
	}

	public static boolean deleteDir(File dir) 
	{
	    if (dir != null && dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i = 0; i < children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }
	    return dir.delete();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}
}