package br.com.medicalservices.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.GregorianCalendar;
import java.util.Locale;

public class AlarmReciever extends BroadcastReceiver

{

	GPSTracker gps;
	String strLatitude, strLongitude, deviceid;
	SessionManager session;
	String username;
	Geocoder geocoder;
	double longitude = 0, latitude = 0;
	int id;
	SharedPreferences prefs;
	int minute= 10;
	@Override
	public void onReceive(Context context, Intent intent) {

		// TODO Auto-generated method stub
		gps = new GPSTracker(context);
		prefs = PreferenceManager.getDefaultSharedPreferences(context);

		minute = Integer.valueOf(prefs.getString("gpsdata", "1"));
		geocoder = new Geocoder(context, Locale.getDefault());
		session = new SessionManager(context);

		username = session.getuserid();
		deviceid = session.getdeviceid();

		if (session.isLoggedIn()) {
			if (gps.canGetLocation()) {
				Log.d("Your Location", "latitude:" + gps.getLatitude()
						+ ", longitude: " + gps.getLongitude());
				strLatitude = String.valueOf(gps.getLatitude());
				strLongitude = String.valueOf(gps.getLongitude());
			} else {
				strLatitude = "";
				strLongitude = "";
			}

			new Login().execute();


			Long time = new GregorianCalendar().getTimeInMillis()
					+ minute * 60 * 1000;

			Intent intentAlarm = new Intent(context, AlarmReciever.class);

			// create the object
			AlarmManager alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);

			// set the alarm for particular time
			alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent
					.getBroadcast(context, 1, intentAlarm,
							PendingIntent.FLAG_UPDATE_CURRENT));

			System.out.println("Web Service-----");
		}
	}

	private class Login extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {

			APIManager.updatelocation(username, strLongitude, strLatitude, deviceid);

			return "Executed";
		}

		@Override
		protected void onPostExecute(String result) {

			if(DataManager.status.equals("false"))
			{
				session.logoutUser();
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

}