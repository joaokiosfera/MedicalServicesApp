package br.com.medicalservices.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;

public class AddGroup extends Fragment implements OnClickListener {

	public static AddGroup newInstance() {
		return new AddGroup();
	}
	private EditText grpNameEditText;
	private Button grpAddButton;
	String myusername = DataManager.username, deviceid;
	private ProgressDialog progress;
	ListView lv;
	private ArrayList<UserPojo> allusers;
	int listsize;
	SessionManager session;

	Context context;
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.activity_add_group, container, false);

		context = getActivity();
		allusers = new ArrayList<UserPojo>();
		session = new SessionManager(context);
		deviceid = session.getdeviceid();
		myusername = session.getuserid();
		grpNameEditText = (EditText) rootView.findViewById(R.id.grpNameEditText);
		grpAddButton = (Button)rootView. findViewById(R.id.grpAddButton);
		lv = (ListView)rootView. findViewById(R.id.userListView);
		grpAddButton.setOnClickListener(this);
		new getallusers().execute();
		return rootView;

	}

	public class getallusers extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(context, "Carregando Usuários...",
					"Aguarde um momento....");

		}

		@Override
		protected String doInBackground(String... params) {

			response = APIManager.getmyfriends(context, myusername,
					deviceid);

			return "";

		}

		@Override
		protected void onPostExecute(String result) {
			progress.cancel();
			if (DataManager.status.equalsIgnoreCase("1")) {

				allusers = DataManager.alluserlist;
				LoadAllUsersAdapter img = new LoadAllUsersAdapter(
						context, allusers);
				lv.setAdapter(img);
				listsize = allusers.size();
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
		if (v == grpAddButton) {
			// check if empty group name
			if (isEmptyField(grpNameEditText))
				return;
			// check if doctors selected
			if (!isUserSelected())
				return;

			new addGroup().execute();
		}
	}

	

	private boolean isEmptyField(EditText editText) {
		boolean result = editText.getText().toString().length() <= 0;
		if (result)
			Toast.makeText(context,
					"Name of Group is required.", Toast.LENGTH_SHORT).show();
		return result;
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
			Toast.makeText(context, "No Member selected.",
					Toast.LENGTH_SHORT).show();

		return result;
	}

	public class addGroup extends AsyncTask<URL, Integer, Boolean> {

		boolean result = false;

		@Override
		protected void onCancelled() {
		}

		@Override
		protected void onPreExecute() {
			
			progress = ProgressDialog.show(context, "Creating Group...",
					"Please wait....");
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

			stringBuilder.deleteCharAt(0);
			stringBuilder2.deleteCharAt(0);
			String docIds = stringBuilder.toString();
			String username = stringBuilder2.toString();
			System.out.println("username--" + username.toString());
			String grpName = grpNameEditText.getText().toString();

			result = APIManager.addnewgroup(grpName, myusername, docIds,
					username, deviceid, myusername);

			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {

			progress.dismiss();
			if (DataManager.status.equals("false")) {
				session.logoutUser();
			} else {
				grpNameEditText.setText("");
				Intent i = new Intent(context, MainActivity.class);
				i.setAction("splash");
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
