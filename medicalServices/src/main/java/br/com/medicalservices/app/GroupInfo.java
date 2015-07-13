package br.com.medicalservices.app;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import chat.demo.app.R;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GroupInfo extends AppCompatActivity {

	private EditText grpNameEditText;
	String myusername = DataManager.username, deviceid;
	private ProgressDialog progress;
	ListView lv;
	private ArrayList<UserPojo> allusers;
	int listsize;
	SessionManager session;
	String adminid = "", groupname = "", groupid = "";
	TextView txtdeletegroup, txtupdategroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_info);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		session = new SessionManager(this);
		deviceid = session.getdeviceid();
		myusername = session.getuserid();
		groupid = DataManager.groupid;
		grpNameEditText = (EditText) findViewById(R.id.grpNameEditText);
		txtdeletegroup = (TextView)findViewById(R.id.txtdelete);
		txtupdategroup = (TextView)findViewById(R.id.txtupdate);
		txtdeletegroup.setVisibility(View.GONE);
		txtupdategroup.setVisibility(View.GONE);
		grpNameEditText.setFocusable(false);
		lv = (ListView) findViewById(R.id.userListView);
		new getallusers().execute();
		
		txtdeletegroup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(adminid.equals(myusername))
				{
				deletegroup();
				}
				else
				{
					Toast.makeText(getApplicationContext(), "You are not admin to delete this group.", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		txtupdategroup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(GroupInfo.this, UpdateGroup.class);
				DataManager.groupid = groupid;
				finish();
				startActivity(i);
				overridePendingTransition(0, 0);
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				// TODO Auto-generated method stub

				DataManager.senderid = allusers.get(position)
						.getUserid();
				
				if(allusers.get(position)
						.getUserid().equals(myusername))
				{
					Toast.makeText(getApplicationContext(), "You can not message yourself..", Toast.LENGTH_LONG).show();
				}else
				{
				
					Intent i = new Intent(GroupInfo.this,
							IndividualChat.class);
					
					finish();
					startActivity(i);
				}
			}
		});
	}

	public class getallusers extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(GroupInfo.this, "Getting Users...",
					"Please wait....");

		}

		@Override
		protected String doInBackground(String... params) {

			response = APIManager.getgroupinfo(GroupInfo.this, myusername,
					deviceid, groupid);

			return "";

		}

		@Override
		protected void onPostExecute(String result) {
			progress.cancel();
			if (DataManager.status.equalsIgnoreCase("1")) {

				adminid = DataManager.adminid;
				groupname = DataManager.groupname;
				allusers = DataManager.alluserlist;
				LoadAllUsersAdapter img = new LoadAllUsersAdapter(
						getApplicationContext(), allusers);
				lv.setAdapter(img);
				listsize = allusers.size();
				setListViewHeightBasedOnChildren(lv);
				grpNameEditText.setText("" + groupname);
				if(adminid.equals(myusername))
				{
					txtdeletegroup.setVisibility(View.VISIBLE);
					txtupdategroup.setVisibility(View.VISIBLE);
				}
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}
	public void deletegroup() {

		// set the flag to true so the next activity won't start up
		new AlertDialog.Builder(this)
		.setTitle("Delete Group?")
		.setMessage("Are you sure you want to delete this Group?")
		.setNegativeButton(android.R.string.no, null)
		.setPositiveButton(android.R.string.yes,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int arg1) {
						
						new deletegroup().execute();
					}
				}).create().show();
		
	}
	public class deletegroup extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(GroupInfo.this, "Getting Users...",
					"Please wait....");

		}

		@Override
		protected String doInBackground(String... params) {

			response = APIManager.deletrgroup(myusername, groupid, deviceid);

			return "";

		}

		@Override
		protected void onPostExecute(String result) {
			progress.cancel();
			
			Intent i = new Intent(getApplicationContext(), MainActivity.class);
			i.setAction("splash");	
			finish();
			startActivity(i);
			overridePendingTransition(0, 0);
		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		 if (id == android.R.id.home) {
			Intent i = new Intent(this, MainActivity.class);
			i.setAction("splash");
			finish();
			startActivity(i);
			overridePendingTransition(0, 0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {

		Intent i = new Intent(this, MainActivity.class);
		i.setAction("splash");
		finish();
		startActivity(i);
		overridePendingTransition(0, 0);
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(),
				MeasureSpec.AT_MOST);
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();
	}

	public class LoadAllUsersAdapter extends BaseAdapter {
		private Context mContext;
		private ArrayList<UserPojo> allusers;
		String mydocid = DataManager.username;

		public LoadAllUsersAdapter(Context context, ArrayList<UserPojo> userList) {
			super();
			this.mContext = context;
			this.allusers = userList;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return allusers.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return allusers.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = null;
			if (convertView == null) {
				view = LayoutInflater.from(mContext).inflate(
						R.layout.group_members, parent, false);
				final ViewHolder holder = new ViewHolder();
				holder.txtmembername = (TextView) view.findViewById(R.id.txtmembername);
				holder.txtstatus = (TextView) view.findViewById(R.id.txtstatus);
				holder.txtadmin = (TextView) view.findViewById(R.id.txtadmin);
				view.setTag(holder);
				
			} else {
				view = convertView;
				
			}

			ViewHolder holder = (ViewHolder) view.getTag();
			
			holder.txtmembername.setTag(allusers.get(position).getUserid());
			holder.txtmembername.setText(allusers.get(position).getFirstname() + " "
					+ allusers.get(position).getLastname());
			
			holder.txtstatus.setText(allusers.get(position).getStatus());
			holder.txtmembername.setTextColor(Color.BLACK);
			holder.txtstatus.setTextColor(Color.BLACK);
			if(allusers.get(position).getUserid().equals(adminid))
			{
				holder.txtadmin.setText("Admin");
				holder.txtadmin.setTextColor(getResources().getColor(R.color.material_red_500));
			}
			if(allusers.get(position)
					.getUserid().equals(myusername))
			{
				holder.txtadmin.setText("MySelf");
				holder.txtadmin.setTextColor(mContext.getResources().getColor(R.color.headercolor));
			}

			return view;
		}

		class ViewHolder {
			public TextView txtmembername, txtstatus, txtadmin;
		}
	}
}
