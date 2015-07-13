package br.com.medicalservices.app;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.view.LayoutInflater;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GcmMessageHandler extends IntentService {

	NotificationCompat.Builder builder;
	LayoutInflater li;
	Handler handler;
	Context context;
	SessionManager session;
	DbMessage dbmsg;
	DbGroup dbgroup;
	boolean groupalert;
	SharedPreferences prefs;
	public GcmMessageHandler() {
		super("GcmIntentService");
	}

	static final String DISPLAY_MESSAGE_ACTION = "DISPLAY_MESSAGE";

	static final String EXTRA_MESSAGE = "message";
	public static final String TAG = "GCMNotificationIntentService";

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handler = new Handler();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override	
	protected void onHandleIntent(Intent intent) {

		context = getApplicationContext();
		session = new SessionManager(context);
		dbmsg = new DbMessage(context);
		dbgroup = new DbGroup(context);
		final Bundle extras = intent.getExtras();

		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
	
		groupalert = prefs.getBoolean("cbalert", true);
		// notifies user
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		if(session.isLoggedIn())
		{
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				// sendNotification("Send error: " + extras.toString());
				Log.i(TAG, "GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR @ "
						+ GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR);

			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				// sendNotification("Deleted messages on server: "
				// + extras.toString());
				Log.i(TAG, "MESSAGE_TYPE_DELETED@ "
						+ GoogleCloudMessaging.MESSAGE_TYPE_DELETED);

			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {

				Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());

				Log.i(TAG, "Received: " + extras.toString());
				String chattype = "";
				String myuserid = session.getuserid();
				String message = intent.getExtras().getString("message");
				chattype = intent.getExtras().getString("chattype");
				String firstname = intent.getExtras().getString("title"); // firstname
				String senderid = intent.getExtras().getString("senderid");
				String logintype = intent.getExtras().getString("logintype");
				String sender_lname = intent.getExtras().getString("sender_lname");
				String msgtype = intent.getExtras().getString("messagetype");
				System.out.println("msgtype==="+msgtype);
				if(chattype.equals("individual"))
				{
				Date todaydate = new Date();

				String date = String.valueOf(todaydate.getTime());
				System.out.println("senderid==="+senderid);
				dbmsg.addContact(new Message(firstname, sender_lname, message,
						myuserid, senderid, date, logintype, chattype, "no", "", "no", msgtype));
				generateNotification(context, firstname, message, senderid, "individual");
				}else if(chattype.equals("group"))	
				{
					Date todaydate = new Date();

					String date = String.valueOf(todaydate.getTime());
					String groupid = intent.getExtras().getString("groupid");
					
					System.out.println("groupid === "+groupid);
				
					dbmsg.addContact(new Message(firstname, sender_lname, message,
							myuserid, senderid, date, logintype, chattype, "no", groupid, "no", msgtype));
					generateNotification(context, firstname, message, groupid, "group");
				}else if(chattype.equals("alert"))
				{
					Date todaydate = new Date();

					String date = String.valueOf(todaydate.getTime());
					String groupid = intent.getExtras().getString("groupid");
					String memberid =  intent.getExtras().getString("memberid");
					String membername =  intent.getExtras().getString("membername");
					String groupname =  intent.getExtras().getString("title");
					String adminid =  intent.getExtras().getString("adminid");
				
					
					if(memberid.contains(","))
					{
					List<String> memidlist = Arrays.asList(memberid.split("\\s*,\\s*"));
					List<String> memnamelist = Arrays.asList(membername.split("\\s*,\\s*"));
					
					for(int i=0; i < memidlist.size(); i++)
					{
						String memid = memidlist.get(i).toString();		
						String memname = memnamelist.get(i).toString();
						dbgroup.addContact(new GroupPojo(groupid, groupname, memid, memname, date, adminid));
						
					}
					}
					else
					{
						dbgroup.addContact(new GroupPojo(groupid, groupname, memberid, membername, date, adminid));
						
					}
					generateNotification(context, firstname, message, groupid, "group");
//					dbgroup.addContact(new GroupPojo(groupid, groupname, memberid, membername, date, adminid));	
				}else if(chattype.equals("delete"))
				{
					String groupid = intent.getExtras().getString("groupid");
					dbgroup.deletegroup(groupid);
					dbmsg.deletegroup(groupid);
				}else if(chattype.equals("update"))
				{
					Date todaydate = new Date();

					String date = String.valueOf(todaydate.getTime());
					String groupid = intent.getExtras().getString("groupid");
					String memberid =  intent.getExtras().getString("memberid");
					String membername =  intent.getExtras().getString("membername");
					String groupname =  intent.getExtras().getString("title");
					String adminid =  intent.getExtras().getString("adminid");
				
					dbgroup.updategroupname(groupid, groupname);
					
					
					if(memberid.contains(","))
					{
					List<String> memidlist = Arrays.asList(memberid.split("\\s*,\\s*"));
					List<String> memnamelist = Arrays.asList(membername.split("\\s*,\\s*"));
					
					for(int i=0; i < memidlist.size(); i++)
					{
						String memid = memidlist.get(i).toString();		
						String memname = memnamelist.get(i).toString();
						System.out.println("memid---"+memid);
						if(dbgroup.checkgrpuser(groupid, memid) < 1)
						{
							
									
								dbgroup.addContact(new GroupPojo(groupid, groupname, memid, memname, date, adminid));
						}
					}
					}
					else
					{
						if(dbgroup.checkgrpuser(groupid, memberid) < 1)
						{
						dbgroup.addContact(new GroupPojo(groupid, groupname, memberid, membername, date, adminid));
						}
					}
					generateNotification(context, firstname, message, groupid, "group");
//					dbgroup.addContact(new GroupPojo(groupid, groupname, memberid, membername, date, adminid));	
				}
			

				Intent i = new Intent("CHAT_MESSAGE_RECEIVED");
				i.putExtra("message", extras.getString("message"));

				context.sendBroadcast(i);

			}
		}
		}
		WakefulBroadcastReceiver.completeWakefulIntent(intent);

	}

	public void generateNotification(Context context, String title,
			String message, String id, String type) {
		int icon = R.drawable.ic_stat_name;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);

		// title = context.getString(R.string.app_name);

		Intent notificationIntent = new Intent(context, MyMessageActivity.class);
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Play default notification sound
		
		notification.defaults |= Notification.DEFAULT_SOUND;
		int notificationid = 0;
		if(type.equals("individual"))
		{
			notification.defaults |= Notification.DEFAULT_SOUND;
		String substr = id.substring(id.length() - 8);
		// Vibrate if vibrate is enabled
		 notificationid = Integer.parseInt(substr);
		}else if(type.equals("group"))
		{
			
			notificationid= Integer.parseInt(id);
			if(groupalert)
			{
			notification.defaults |= Notification.DEFAULT_SOUND;
			}
		}
		System.out.println("notification id=="+notificationid);
		notificationManager.notify(notificationid, notification);

		// new getdata().execute();

	}

	
}
