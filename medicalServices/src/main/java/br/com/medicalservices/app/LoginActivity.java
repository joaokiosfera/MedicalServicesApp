package br.com.medicalservices.app;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends Activity implements OnClickListener {

	Dialog builder;
	EditText etemail, etpassword;
	Button btnlogin, btnforgotpass, btnregister;
	ProgressDialog pDialog;
	ConnectionDetector connection;
	String email, password, city, userid, country, regid, deviceid, firstname,
			lastname, profilepic;
	String msg = "";
	double longitude = 0, latitude = 0;
	String PROJECT_NUMBER = DataManager.PROJECT_NUMBER;
	GPSTracker gps;
	String strLatitude, strLongitude;
	Geocoder geocoder;
	SessionManager session;
	GoogleCloudMessaging gcm;
	SharedPreferences prefs;
	private ProgressDialog progress;
	String resetemail = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		session = new SessionManager(this);
		geocoder = new Geocoder(this, Locale.getDefault());
		gps = new GPSTracker(this);
		getRegId();

		getlocation();

		deviceid = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		connection = new ConnectionDetector(this);
		widgets();

	}

	public void getRegId() {

		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {

				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging
								.getInstance(getApplicationContext());
					}
					regid = gcm.register(PROJECT_NUMBER);
					msg = "Device registered, registration ID=" + regid;

				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();

					System.out.println("Error---" + ex.getMessage());
				}

				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				System.out.println("Registerid---" + regid);
			}
		}.execute(null, null, null);

	}

	public void widgets() {

		etemail = (EditText) findViewById(R.id.etemail);

		etpassword = (EditText) findViewById(R.id.etpassword);

		btnlogin = (Button) findViewById(R.id.btnlogin);

		btnforgotpass = (Button) findViewById(R.id.btnforgotpass);

		btnregister = (Button) findViewById(R.id.btnregister);

		btnlogin.setOnClickListener(this);
		btnforgotpass.setOnClickListener(this);
		btnregister.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == btnlogin) {
			register();
		}
		else if (v == btnregister)
		{
			Intent it = new Intent(this, RegisterActivity.class);
			startActivity(it);
			overridePendingTransition(0, 0);
		}

		else if (v == btnforgotpass) {
			openmenu();
		}

	}

	private void openmenu() {

		builder = new Dialog(LoginActivity.this,
				android.R.style.Theme_DeviceDefault_DialogWhenLarge_NoActionBar);
		builder.setContentView(R.layout.restpass_layout);
		builder.setCanceledOnTouchOutside(true);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

		builder.show();

		final EditText etemail = (EditText) builder.findViewById(R.id.etemail);

		TextView txtconfirm = (TextView) builder.findViewById(R.id.txtok);
		TextView txtcancel = (TextView) builder.findViewById(R.id.txtcancel);

		txtconfirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				resetemail = etemail.getText().toString();

				if (resetemail.length() < 1) {
					Toast.makeText(LoginActivity.this, "Digite seu endereço de Email!",
							Toast.LENGTH_LONG).show();
				} else if (!resetemail.contains("@")) {
					Toast.makeText(LoginActivity.this,
							"Digite um Email válido", Toast.LENGTH_LONG)
							.show();
				} else {

					new resetpassword().execute();

				}
			}
		});

		txtcancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				builder.dismiss();

			}
		});

	}

	private class resetpassword extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(LoginActivity.this,
					"Requisitando Senha...", "Aguarde um momento....");
		}

		@Override
		protected String doInBackground(String... params) {

			APIManager.resetpassword(resetemail, userid);

			return "Executed";
		}

		@Override
		protected void onPostExecute(String result) {

			progress.cancel();
		
				
					Toast.makeText(
							getApplicationContext(),
							"A sua senha foi enviada para o email cadastrado.",
							Toast.LENGTH_LONG).show();
					}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	public void getlocation() {

		if (gps.canGetLocation()) {
			try {
				if (gps.canGetLocation()) {
					Log.d("Your Location", "latitude:" + gps.getLatitude()
							+ ", longitude: " + gps.getLongitude());
					latitude = gps.getLatitude();
					longitude = gps.getLongitude();
				}

				if (latitude == 0.0 && longitude == 0.0) {
					city = "";
					country = "";
				} else {
					List<Address> addresses;
					addresses = geocoder
							.getFromLocation(latitude, longitude, 1);

					System.out.println("address..." + addresses.toArray());

					city = addresses.get(0).getAddressLine(2);
					country = addresses.get(0).getCountryName();

				}

				System.out.println("city----" + city);
				System.out.println("country----" + country);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void register() {
		email = etemail.getText().toString();

		password = etpassword.getText().toString();

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		if (email.length() < 2) {
			etemail.setError("Digite um email válido");
			etemail.setFocusable(true);
		} else if (password.length() < 6) {
			etpassword.setError("Digite uma senha válida");
			etpassword.setFocusable(true);
		} else {

			if (connection.isConnectingToInternet()) {
				if (email.contains("@")) {
					if (email.contains(".")) {
						new login().execute();
					} else {
						etemail.setError("Digite um email válido");
						etemail.setFocusable(true);
					}
				} else {
					etemail.setError("Digite uma senha válida");
					etemail.setFocusable(true);
				}
			} else {
				Toast.makeText(LoginActivity.this,
						"Sem conexão com a internet...", Toast.LENGTH_LONG)
						.show();
			}

		}

	}

	public class login extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(LoginActivity.this, "Carregando...",
					"Aguarde um momento....");

		}

		@Override
		protected String doInBackground(String... params) {

			response = registernotification();

			return "";

		}

		@Override
		protected void onPostExecute(String result) {

			progress.cancel();

			if (response) {
				DataManager.username = userid;
				session.setlogintype("email");
				session.createLoginSession(userid, firstname, lastname,
						deviceid);
				session.setProfilepic(profilepic);
				scheduleAlarm();
				Intent i = new Intent(LoginActivity.this,
						MainActivity.class);
				i.setAction("splash");
				finish();
				startActivity(i);
				overridePendingTransition(0, 0);

			} else {
				etpassword.setFocusable(true);
				etpassword.setError("Email ou Senha inválidos");
				etpassword.setFocusable(true);
			}

		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}

	public boolean registernotification() {

		boolean isloggin = false;

		String url = DataManager.url + "login.php";
		HttpClient client = new DefaultHttpClient();
		HttpPost get = new HttpPost(url);

		List<NameValuePair> paramas = new ArrayList<NameValuePair>();
		paramas.add(new BasicNameValuePair("gcmid", regid));
		paramas.add(new BasicNameValuePair("email", email));
		paramas.add(new BasicNameValuePair("password", password));
		paramas.add(new BasicNameValuePair("logintype", "email"));

		paramas.add(new BasicNameValuePair("city", city));
		paramas.add(new BasicNameValuePair("country", country));
		paramas.add(new BasicNameValuePair("deviceid", deviceid));
		UrlEncodedFormEntity ent;
		try {
			ent = new UrlEncodedFormEntity(paramas, HTTP.UTF_8);
			get.setEntity(ent);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String returnString = null;
		HttpResponse response = null;
		try {
			response = client.execute(get);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {

				returnString = EntityUtils.toString(resEntity);
				System.out.println("returnString---" + returnString.toString());
				try {

					JSONObject obj = new JSONObject(returnString);

					String success = obj.getString("success");

					if (success.equals("1")) {
						JSONArray array = obj.getJSONArray("users");

						String status = "Available";
						for (int i = 0; i < array.length(); i++)

						{

							userid = new String(
									array.getJSONObject(i).getString("userid")
											.getBytes("ISO-8859-1"), "UTF-8");

							firstname = new String(array.getJSONObject(i)
									.getString("firstname")
									.getBytes("ISO-8859-1"), "UTF-8");

							lastname = new String(array.getJSONObject(i)
									.getString("lastname")
									.getBytes("ISO-8859-1"), "UTF-8");

							status = new String(
									array.getJSONObject(i).getString("status")
											.getBytes("ISO-8859-1"), "UTF-8");
							
							profilepic = new String(array.getJSONObject(i)
									.getString("profilepic")
									.getBytes("ISO-8859-1"), "UTF-8");

						}
						isloggin = true;

						SharedPreferences.Editor se = prefs.edit();
						se.putString("status", status);
						se.commit();
					} else {
						isloggin = false;
					}
				} catch (JSONException e) {
					Log.e("JSON Parser", "Error parsing data " + e.toString());
				}

			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isloggin = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isloggin = false;
		}

		return isloggin;
	}

	public void connectionerror() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				LoginActivity.this);

		alertDialog.setTitle("Error!");

		alertDialog.setMessage("Conexão Perdida! Tente Novamente");

		alertDialog.setPositiveButton("Tentar",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						new login().execute();

					}
				});

		alertDialog.show();
	}

	public void scheduleAlarm() {

		Long time = new GregorianCalendar().getTimeInMillis() + 1 * 60 * 1000;

		Intent intentAlarm = new Intent(this, AlarmReciever.class);

		// create the object
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, intentAlarm, 0);

		try {
			alarmManager.cancel(pi);
		} catch (Exception e) {
			Log.e("Cancel",
					"AlarmManager update was not canceled. " + e.toString());
		}
		// set the alarm for particular time
		alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent
				.getBroadcast(this, 1, intentAlarm,
						PendingIntent.FLAG_UPDATE_CURRENT));

	}

}