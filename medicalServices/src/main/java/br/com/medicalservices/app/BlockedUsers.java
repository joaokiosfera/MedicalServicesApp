package br.com.medicalservices.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.medicalservices.app.utils.ImageUtil;
import br.com.medicalservices.app.views.RoundedImageView;


public class BlockedUsers extends Fragment {
	
	public static BlockedUsers newInstance() {
		return new BlockedUsers();
	}
	
	Context context;
	ListView lv;
	private ProgressDialog progress;
	String myusername, deviceid, selecteduserid;
	SessionManager session;
	DbUsers dbuser;
	ArrayList<UserPojo> userlist = new ArrayList<UserPojo>();
	GPSTracker gps;
	String strLatitude, strLongitude, distance;
	SharedPreferences prefs;
	double longitude = 0, latitude = 0;

		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.activity_blocked_users, container, false);
			context = getActivity();

		
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		
		distance = prefs.getString("distance", "200");
		
//		distance = DataManager.searchdistance;
		lv = (ListView) rootView.findViewById(R.id.peerMsgList);
		session = new SessionManager(context);
		gps = new GPSTracker(context);
		deviceid = session.getdeviceid();
		myusername = session.getuserid();
		TextView txtname = (TextView)rootView.findViewById(R.id.txtname);

		dbuser = new DbUsers(context);
	
		new getblockedusers().execute();
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				// TODO Auto-generated method stub
				TextView sub_name = (TextView) view
						.findViewById(R.id.list_am_name_bottom);
				TextView name = (TextView) view
						.findViewById(R.id.list_am_name_top);
				Intent i = new Intent(context,
						IndividualChat.class);
				DataManager.senderid = sub_name.getTag().toString();
				DataManager.fullname = name.getText().toString();
				startActivity(i);

			}
		});
		return rootView;
	}


	public class getblockedusers extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {
			

		}

		@Override
		protected String doInBackground(String... params) {

			response = APIManager.getblockedfriends(context, myusername, deviceid);

			return "";

		}

		@Override
		protected void onPostExecute(String result) {

			if (DataManager.status.equalsIgnoreCase("1")) {
				
				 userlist = DataManager.alluserlist ;
				ImageAdapter img = new ImageAdapter(context);
				img.notifyDataSetChanged();
				lv.setAdapter(img);
			}else if(DataManager.status.equalsIgnoreCase("false"))
			{
				session.logoutUser();
			}else if(DataManager.status.equalsIgnoreCase("0"))
			{
				Toast.makeText(context, "You do not have any blocked users.", Toast.LENGTH_LONG).show();
				
			}
				
		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}
	
	
	public class ImageAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		ArrayList<UserPojo> amList = DataManager.alluserlist;

		public ImageAdapter(Context c) {

			mInflater = LayoutInflater.from(c);

		}

		@Override
		public int getCount() {

			return amList.size();

		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {

			ViewHolder holder = null;

			if (v == null) {
				holder = new ViewHolder();
				v = mInflater.inflate(R.layout.blockeduserrow, null);

				holder.name = (TextView) v.findViewById(R.id.list_am_name_top);
				holder.sub_name = (TextView) v
						.findViewById(R.id.list_am_name_bottom);
				holder.content = (TextView) v
						.findViewById(R.id.list_am_content);
				holder.btnunblock = (Button) v
						.findViewById(R.id.btnunblock);
				holder.img_icon = (RoundedImageView) v
						.findViewById(R.id.img_picture);
				
				holder.btnunblock.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						selecteduserid = v.getTag().toString();
						
						new unblockfriends().execute();
					}
				});
				
				v.setTag(holder);

			} else {
				holder = (ViewHolder) v.getTag();
			}

			holder.name.setText(amList.get(position).getFirstname() + " "
					+ amList.get(position).getLastname());
			holder.name.setTag("individual");
			holder.btnunblock.setTag(amList.get(position).getUserid());
			holder.sub_name.setText(amList.get(position).getStatus());
			holder.sub_name.setTag(amList.get(position).getUserid());
			String logintype = amList.get(position).getLogintype();

			String photourl = DataManager.url
						+ amList.get(position).getProfilepic();

			ImageUtil.displayImage(holder.img_icon, photourl, null);

			holder.content.setText(amList.get(position).getCity() + ", "+ amList.get(position).getCountry());


			return v;
		}

		class ViewHolder {
			TextView name, sub_name, content;
			RoundedImageView img_icon;
			
			Button btnunblock;
		}

		// public void tableChanged(AllUsersActivity arg0) {
		// context.notifyDataSetChanged();
		// }
	}

	public class unblockfriends extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {
			

		}

		@Override
		protected String doInBackground(String... params) {

			response = APIManager.unblockuser(myusername, selecteduserid, deviceid);

			return "";

		}

		@Override
		protected void onPostExecute(String result) {

			if (DataManager.status.equalsIgnoreCase("1")) {
				 
				if(userlist.size() > 0)
				{
					userlist.clear();
				}
			Intent i = new Intent(context, MainActivity.class);
			i.setAction("blockedusers");
			startActivity(i);
				
				
				
			}else if(DataManager.status.equalsIgnoreCase("false"))
			{
				session.logoutUser();
			}else
			{
				Toast.makeText(context, "No User found.", Toast.LENGTH_LONG).show();
			}
				
		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}
}
