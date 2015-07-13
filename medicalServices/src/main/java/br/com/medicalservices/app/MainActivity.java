package br.com.medicalservices.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import br.com.medicalservices.app.utils.ImageUtil;
import br.com.medicalservices.app.views.RoundedImageView;
import chat.demo.app.R;

import com.astuetz.PagerSlidingTabStrip;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainActivity extends AppCompatActivity  {

	SessionManager sesion;
	String name, deviceid, logintype;
	Toolbar toolbar;
	ViewPager pager;
	ViewPagerAdapter adapter;
	PagerSlidingTabStrip tabs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sesion = new SessionManager(this);

		name = sesion.firstname() + " " + sesion.lastname();

		deviceid = sesion.getdeviceid();
		logintype = sesion.getLogintype();

		toolbar = (Toolbar) findViewById(R.id.tool_bar);
		setSupportActionBar(toolbar);

		adapter =  new ViewPagerAdapter(getSupportFragmentManager(),new CharSequence[]{"Recentes", "Grupos", "Amigos"},3);

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);

		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setViewPager(pager);

		prepareHeaderView();

	}

	private void prepareHeaderView() {

		RoundedImageView iv = (RoundedImageView) findViewById(R.id.img);
		TextView tv = (TextView) findViewById(R.id.txtname);
		tv.setText(name);

		ImageLoader.getInstance().init(
				ImageLoaderConfiguration.createDefault(this));
		String logintype = sesion.getLogintype();
		String profileurl = sesion.getProfilePic();

		if (profileurl != null) {
			if (logintype.equals("email")) {
				String photourl = DataManager.url + profileurl;
				ImageUtil.displayImage(iv, photourl, null);
				System.out.println(photourl);
			}
		}

		LinearLayout header = (LinearLayout) findViewById(R.id.header);
		header.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(MainActivity.this, UserProfile.class);
				DataManager.profileid = sesion.getuserid();
				startActivity(i);
				overridePendingTransition(0, 0);

			}
		});


	}


	@Override
	protected void onResume() {
		super.onResume();
		new getuserprofile().execute();
	}



	public class getuserprofile extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... params) {

			response = APIManager.getuserprofile(getApplicationContext(),
					sesion.getuserid(), deviceid, sesion.getuserid());

			return "";

		}

		@Override
		protected void onPostExecute(String result) {

			if (response) {

				sesion.setProfilepic(DataManager.alluserlist.get(0)
						.getProfilepic());
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

			APIManager.logout(sesion.getuserid(), deviceid);

			return "Executed";
		}

		@Override
		protected void onPostExecute(String result) {

			if (DataManager.status.equals("1")) {

				sesion.logoutUser();

				Toast.makeText(getApplicationContext(),
						"Logout Successfully..", Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}
}