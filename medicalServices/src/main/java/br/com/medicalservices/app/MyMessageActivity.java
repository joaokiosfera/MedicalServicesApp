package br.com.medicalservices.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.medicalservices.app.utils.ImageUtil;
import br.com.medicalservices.app.views.RoundedImageView;
import github.ankushsachdeva.emojicon.EmojiconTextView;

public class MyMessageActivity extends Fragment {

	public static MyMessageActivity newInstance() {
		return new MyMessageActivity();
	}
	ListView lv;
	String myusername;
	SessionManager session;
	DbMessage dbmsg;
	ArrayList<Message> msglist = new ArrayList<Message>();
	TextView tv;
	DbUsers dbuser;
	SharedPreferences prefs;
	String status = "", logintype = "";
	View footerView;
	Context context;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_my_message, container, false);
		
		context = getActivity();
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		


		prefs = PreferenceManager.getDefaultSharedPreferences(context);

		status = prefs.getString("status", "Available");

		dbuser = new DbUsers(context);
		dbmsg = new DbMessage(context);
		lv = (ListView) rootView.findViewById(R.id.peerMsgList);
		session = new SessionManager(context);
		myusername = session.getuserid();
		logintype = session.getLogintype();
		context.registerReceiver(broadcastReceiver, new IntentFilter(
				"CHAT_MESSAGE_RECEIVED"));

		DataManager.fullname = "";
		DataManager.senderid = "";
		DataManager.groupid = "";
		getdata();

		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				// TODO Auto-generated method stub

				if (msglist.get(position).getChattype().equals("individual")) {
					Intent i = new Intent(context,
							IndividualChat.class);
					DataManager.senderid = msglist.get(position)
							.getFromuserid();
		
					startActivity(i);
				}
				if (msglist.get(position).getChattype().equals("group")) {
					Intent i = new Intent(context,
							GroupChatActivity.class);
					DataManager.groupid = msglist.get(position).getGroupid();
					System.out.println("groupid---" + DataManager.groupid);
					startActivity(i);
				}

			}
		});


		tv = (TextView)rootView. findViewById(R.id.txtinstfoot);

		tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				// ImageButton btn =
				// (ImageButton)view.findViewById(R.id.txtbtn);

				tv.setTextColor(getResources().getColor(R.color.headercolor));

				Intent i = new Intent(context,
						MainActivity.class);
				i.setAction("search");
				DataManager.action = "search";
				startActivity(i);
				
			}
		});
		return rootView;

	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			getdata();
		}
	};


	public void getdata() {
		List<Message> messagelist = dbmsg.getallhistory();

		if (msglist.size() > 0) {
			msglist.clear();
		}
		Message ms;
		for (Message msg : messagelist) {
			ms = new Message();
			String groupid = "", senderid = "";
			String chattype = msg.getChattype();

			if (chattype.equals("group")) {
				groupid = msg.getGroupid();

				String firstname = msg.getFname();

				DbGroup db = new DbGroup(context);
				String groupname = "";

				groupname = db.getgroupname(groupid);

				String lastname = msg.getLname();

				String logintype = "group";
				String msgtype = msg.getMsgtype();
				String message  = "";
				if(msgtype != null)
				{
				if(msgtype.equals("text") )
				{
					message = msg.getMessage();	
				}else if(msgtype.equals("audio"))
				{
					message = "Audio";	
				}else if(msgtype.equals("video"))
				{
					message = "Video";	
				}else if(msgtype.equals("photo"))
				{
					message = "Photo";	
				}
				}else
				{
					msgtype = "text";
					message = msg.getMessage();	
				}
				
				
				String timestamp = msg.getTimestamp();
				String isseen = msg.getIsseen();
				ms.setIsseen(isseen);
				ms.setGroupid(groupid);
				ms.setChattype(chattype);
				ms.setFname(firstname);
				ms.setLname(lastname);
				ms.setLogintype(logintype);
				ms.setMessage(message);
				ms.setTimestamp(timestamp);
				ms.setGroupname(groupname);
				msglist.add(ms);
			}

			else if (chattype.equals("individual")) {
				senderid = msg.getFromuserid();

				String firstname = msg.getFname();

				String lastname = msg.getLname();

				String logintype = "";

				if(msg.getLogintype() != null)
				{
				logintype = msg.getLogintype();
				}
				else
				{
					System.out.println("firstname-- "+firstname);
					logintype = "";
				}
				
				String msgtype = msg.getMsgtype();
				String message  = "";
				
				if(msgtype != null)
				{
				if(msgtype.equals("text") )
				{
					message = msg.getMessage();	
				}else if(msgtype.equals("audio"))
				{
					message = "Audio";	
				}else if(msgtype.equals("video"))
				{
					message = "Video";	
				}else if(msgtype.equals("photo"))
				{
					message = "Photo";	
				}
				}else
				{
					msgtype = "text";
					message = msg.getMessage();	
				}
				
				

				String timestamp = msg.getTimestamp();
				String isseen = msg.getIsseen();
				ms.setIsseen(isseen);
				ms.setFromuserid(senderid);
				ms.setChattype(chattype);
				ms.setFname(firstname);
				ms.setLname(lastname);
				ms.setLogintype(logintype);
				ms.setMessage(message);

				ms.setTimestamp(timestamp);
				msglist.add(ms);
			}

		}

		if (msglist.size() > 0) {
			ImageAdapter img = new ImageAdapter(context);
			lv.setAdapter(img);
		}
		
	}

	public class ImageAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		ArrayList<Message> amList = msglist;

		public ImageAdapter(Context c) {

			mInflater = LayoutInflater.from(c);

		}

		public int getCount() {

			return amList.size();

		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View v, ViewGroup parent) {

			ViewHolder holder = null;

			if (v == null) {
				holder = new ViewHolder();
				v = mInflater.inflate(R.layout.list_peer_message, null);

				holder.name = (TextView) v.findViewById(R.id.list_am_name_top);
				holder.sub_name = (TextView) v
						.findViewById(R.id.list_am_name_bottom);
				holder.content = (EmojiconTextView) v
						.findViewById(R.id.list_am_content);
				holder.time = (TextView) v.findViewById(R.id.list_am_time);
				holder.seencount = (TextView) v.findViewById(R.id.seencount);
				holder.img_icon = (RoundedImageView) v
						.findViewById(R.id.img_picture);
				
				v.setTag(holder);

			} else {
				holder = (ViewHolder) v.getTag();
			}

			if (amList.get(position).getChattype().equalsIgnoreCase("group")) {

				holder.name.setText(amList.get(position).getGroupname());
				holder.name.setTag("group");

				holder.sub_name.setTag(amList.get(position).getGroupid());
				holder.sub_name.setText("");
				
				holder.content.setText(amList.get(position).getMessage());

				int seencount = dbmsg.getunseengroupcount(amList.get(position)
						.getGroupid(), "no");
				if (seencount > 0) {
					holder.seencount.setText("" + seencount);
					holder.seencount.setVisibility(View.VISIBLE);
					holder.content.setTextColor(getResources().getColor(
							R.color.headercolor));
				} else {
					holder.seencount.setVisibility(View.INVISIBLE);
					holder.seencount.setText("");
				}
			}

			if (amList.get(position).getChattype()
					.equalsIgnoreCase("individual")) {
				holder.name.setText(amList.get(position).getFname() + " "
						+ amList.get(position).getLname());
				holder.name.setTag("individual");

				holder.sub_name.setText("");
				holder.sub_name.setTag(amList.get(position).getFromuserid());


				int seencount = dbmsg.getunseeenindividual(amList.get(position)
						.getFromuserid(), "no");
				if (seencount > 0) {
					holder.seencount.setText("" + seencount);
					holder.seencount.setVisibility(View.VISIBLE);
					holder.content.setTextColor(getResources().getColor(
							R.color.headercolor));

				} else {
					holder.seencount.setText("");
					holder.seencount.setVisibility(View.INVISIBLE);
				}
			}
				
			String logintype = amList.get(position)
					.getLogintype();
			System.out.println("logintype-fb--"+logintype);
			 if(logintype.equals("group")){
				ImageUtil.displayImage(holder.img_icon, "", null);
			}else if (logintype.equals("email")) {
				String userid = amList.get(position).getFromuserid();

				String photourl = DataManager.url + dbuser.getprofileurl(userid);

				ImageUtil.displayImage(holder.img_icon, photourl, null);
			} 
			

			holder.content.setText(amList.get(position).getMessage());
			String time = amList.get(position).getTimestamp();
			// time = getCurrentTimeStamp(time);
			time = gettime(Long.valueOf(time));
			holder.time.setText("" + time);

			return v;
		}

		class ViewHolder {
			EmojiconTextView content;
			TextView name, sub_name, time, seencount;
			RoundedImageView img_icon;
			
		}

	}

	public String gettime(long timestamp) {
		String time = "";
		if (DateUtils.isToday(timestamp)) {
			SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm");
			time = sdf1.format(new Date(timestamp));

		} else {
			SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd/yyyy");
			time = sdf1.format(new Date(timestamp));
		}
		return time;
	}

}