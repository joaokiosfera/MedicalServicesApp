package br.com.medicalservices.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;

public class UpdateGroup extends ActionBarActivity implements OnClickListener{
	
	private EditText grpNameEditText;
	private Button grpUpdateButton;
	String myusername = DataManager.username, deviceid;
	private ProgressDialog progress;
	String adminid = "", groupname = "", groupid = "";
	ListView lv;
	private ArrayList<UserPojo> allusers;
	private ArrayList<UserPojo> groupusers;
	int listsize;
	SessionManager session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_group);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(
				getResources().getDrawable(R.drawable.back));
		
		allusers = new ArrayList<UserPojo>();
		groupusers = new ArrayList<UserPojo>();
		session = new SessionManager(this);
		deviceid = session.getdeviceid();
		myusername = session.getuserid();
		groupid = DataManager.groupid;
		grpNameEditText = (EditText) findViewById(R.id.grpNameEditText);
		grpUpdateButton = (Button) findViewById(R.id.grpUpdateButton);
		lv = (ListView) findViewById(R.id.userListView);
		grpUpdateButton.setOnClickListener(this);
		new getallusers().execute();

	}
	
	public class getallusers extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(UpdateGroup.this, "Getting Users...",
					"Please wait....");

		}

		@Override
		protected String doInBackground(String... params) {

			response = APIManager.getmyfriends(UpdateGroup.this, myusername,
					deviceid);

			return "";

		}

		@Override
		protected void onPostExecute(String result) {
			progress.cancel();
			if (DataManager.status.equalsIgnoreCase("1")) {

				allusers = DataManager.alluserlist;
				new getgroupusers().execute();
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}

	public class getgroupusers extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(UpdateGroup.this, "Getting Users...",
					"Please wait....");

		}

		@Override
		protected String doInBackground(String... params) {

			response = APIManager.getgroupinfo(UpdateGroup.this, myusername,
					deviceid, groupid);
			
			
			return "";

		}

		@Override
		protected void onPostExecute(String result) {
			progress.cancel();
			if (DataManager.status.equalsIgnoreCase("1")) {

				adminid = DataManager.adminid;
				groupname = DataManager.groupname;
				groupusers = DataManager.alluserlist;
				
				LoadAllUsersAdapter img = null;
				if(groupusers.size() > allusers.size())
				{
					img = new LoadAllUsersAdapter(
							getApplicationContext(), groupusers);	
				}else
				{
					img = new LoadAllUsersAdapter(
							getApplicationContext(), allusers);
				}
				
				
				lv.setAdapter(img);
				listsize = allusers.size();
				grpNameEditText.setText("" + groupname);
				setListViewHeightBasedOnChildren(lv);
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == grpUpdateButton) {
			// check if empty group name
			if (isEmptyField(grpNameEditText))
				return;
			// check if doctors selected
			if (!isUserSelected())
				return;

			new updategroup().execute();
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
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private boolean isEmptyField(EditText editText) {
		boolean result = editText.getText().toString().length() <= 0;
		if (result)
			Toast.makeText(getApplicationContext(),
					"Name of Group is required.", Toast.LENGTH_SHORT).show();
		return result;
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(this, MainActivity.class);
		i.setAction("splash");
		finish();
		startActivity(i);
		overridePendingTransition(0, 0);
	}

	private boolean isUserSelected() {
		boolean result = false;

		for (int i = 0; i < allusers.size(); i++) {
			UserPojo doc = allusers.get(i);
			if (doc.isSelected) {
				result = true;

				if (doc.userid == myusername) {
					result = false;

				}
				break;
			}
		}

		if (!result)
			Toast.makeText(getApplicationContext(), "No Member selected.",
					Toast.LENGTH_SHORT).show();

		return result;
	}

	public class LoadAllUsersAdapter extends BaseAdapter {
		private Context mContext;
		private ArrayList<UserPojo> users;
		String mydocid = DataManager.username;

		public LoadAllUsersAdapter(Context context, ArrayList<UserPojo> userList) {
			super();
			this.mContext = context;
			this.users = userList;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return users.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return users.get(position);
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
			if(convertView == null)
			{
				view = LayoutInflater.from(mContext).inflate(R.layout.activity_group_participants, parent, false);
				final ViewHolder holder = new ViewHolder();
				holder.checkbox = (CheckBox) view.findViewById(R.id.checkBox1);
				holder.checkbox
		          .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

		            @Override
		            public void onCheckedChanged(CompoundButton buttonView,
		                boolean isChecked) {
		            	UserPojo element = (UserPojo) holder.checkbox
		                  .getTag();
		              element.setSelected(buttonView.isChecked());

		            }
		          });
				view.setTag(holder);
				holder.checkbox.setTag(users.get(position));
			}
			else
			{
				view = convertView;
				 ((ViewHolder) view.getTag()).checkbox.setTag(users.get(position));
			}	 
			
			ViewHolder holder = (ViewHolder) view.getTag();
			holder.checkbox.setChecked(users.get(position).isSelected());
			holder.checkbox.setText(users.get(position).getFirstname() + " " +users.get(position).getLastname());
			holder.checkbox.setTextColor(Color.BLACK);
			if(mydocid == users.get(position).getUserid())
			{	
//				DataManager.isowner = true;
				holder.checkbox.setChecked(true);
				holder.checkbox.setClickable(false);
			}
		
			
			for(int i =0; i < groupusers.size(); i++)
			{
				System.out.println("Groupuserid---"+groupusers.get(i).getUserid());
				System.out.println("USerid---"+users.get(position).getUserid());
				if(groupusers.get(i).getUserid().equals(users.get(position).getUserid()))
				{
					holder.checkbox.setChecked(true);
					holder.checkbox.setClickable(true);
					System.out.println("USerid---"+true);
				}
			}
			
			return view;
		}
		
		class ViewHolder
		{
			public CheckBox checkbox;
		}
	}
	
	
	
	public class updategroup extends AsyncTask<URL, Integer, Boolean> {

		boolean result = false;
		String grpName;

		@Override
		protected void onCancelled() {
		}

		@Override
		protected void onPreExecute() {

			grpName = grpNameEditText.getText().toString();

		}

		@Override
		protected Boolean doInBackground(URL... params) {
			// TODO Auto-generated method stub

			StringBuilder stringBuilder = new StringBuilder();
			StringBuilder stringBuilder2 = new StringBuilder();
			for (int i = 0; i < allusers.size(); i++) {
				UserPojo doc = allusers.get(i);

				if (doc.isSelected()) {

					if (doc.getUserid() != myusername) {
						stringBuilder.append(",");
						stringBuilder.append(doc.getUserid());
					}
				}
			}
			
			stringBuilder.append(",");
			stringBuilder.append(myusername);

			for (int i = 0; i < allusers.size(); i++) {
				UserPojo doc = allusers.get(i);

				if (doc.isSelected()) {

					if (doc.getUserid() != myusername) {
						stringBuilder2.append(",");
						stringBuilder2.append(doc.getFirstname()
								+ doc.getLastname());
					}
				}
			}
			
			stringBuilder2.append(",");
			stringBuilder2.append(session.firstname() + " "+ session.lastname());

			stringBuilder.deleteCharAt(0);
			stringBuilder2.deleteCharAt(0);
			String docIds = stringBuilder.toString();
			String username = stringBuilder2.toString();
			System.out.println("username--" + username.toString());

			result = APIManager.updategroup(groupid,grpName, myusername, docIds,
					username, deviceid, myusername);

			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {

			if (DataManager.status.equals("false")) {
				session.logoutUser();
			} else {
				grpNameEditText.setText("");
				Intent i = new Intent(UpdateGroup.this, MainActivity.class);
				i.setAction("splash");
				finish();
				startActivity(i);
			}

		}
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
}