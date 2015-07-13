package br.com.medicalservices.app;

import github.ankushsachdeva.emojicon.EmojiconGridView.OnEmojiconClickedListener;
import github.ankushsachdeva.emojicon.EmojiconTextView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.EmojiconsPopup.OnEmojiconBackspaceClickedListener;
import github.ankushsachdeva.emojicon.EmojiconsPopup.OnSoftKeyboardOpenCloseListener;
import github.ankushsachdeva.emojicon.emoji.Emojicon;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import br.com.medicalservices.app.utils.AndroidMultiPartEntity;
import br.com.medicalservices.app.utils.AndroidMultiPartEntity.ProgressListener;
import br.com.medicalservices.app.views.RoundedImageView;
import chat.demo.app.R;
import android.widget.TextView;
import android.widget.Toast;

import com.applidium.shutterbug.FetchableImageView;


public class GroupChatActivity extends AppCompatActivity {
	private ProgressDialog pDialog;
	Dialog builder;
	public static ArrayList<Message> amList = new ArrayList<Message>();
	public static EditText etmessage;
	public static Button  btnfile;
	ImageButton btnsend;
	public static String myuserid, groupid, message, deviceid,
			msgtype = "text";
	static private ProgressDialog progress;
	SessionManager session;
	static String firstname;
	static String sender_lname;
	String logintype;
	public static DbMessage dbmsg;
	public static ListView list;
	// public static AwesomeAdapter adapter;
	public static TextView txtalert, txtname;
	static RoundedImageView pic;
	public boolean isopened = false;
	public static Context context;

	String messagetype = "", uriPrefix = "", item = "", fileExtension = "",
			post_to = "";;
	boolean hasAttachment = false;
	int millis = 0;
	Uri selectedattachment;
	long totalSize = 0;
	private String filePath = "", filename = "";

	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	private static final int IMAGE_PICK_REQUEST_CODE = 200;
	private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 300;
	private static final int AUDIO_REQUEST_CODE = 400;
	public static ImageView btnsmiley ;
	public static final int progress_bar_type = 0;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	public static final int MEDIA_TYPE_SOUND = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_individual_chat);

		context = this;
		dbmsg = new DbMessage(this);
		etmessage = (EditText) findViewById(R.id.etmessage);
		btnsend = (ImageButton) findViewById(R.id.btnsend);
		btnfile = (Button) findViewById(R.id.btnfile);
		btnsmiley = (ImageView) findViewById(R.id.btnsmiley);
		list = (ListView) findViewById(R.id.list);
		registerForContextMenu(list);
		isopened = true;

		registerReceiver(broadcastReceiver, new IntentFilter(
				"CHAT_MESSAGE_RECEIVED"));

		session = new SessionManager(this);
		deviceid = session.getdeviceid();
		groupid = DataManager.groupid;
		System.out.println("groupiddddddd--" + groupid);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		LayoutInflater mInflater = LayoutInflater.from(this);


		View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
		txtalert = (TextView) mCustomView.findViewById(R.id.txtalert);
		pic = (RoundedImageView) mCustomView.findViewById(R.id.img);

		txtname = (TextView) mCustomView.findViewById(R.id.txtname);


		txtalert = (TextView) findViewById(R.id.txtalert);
		pic = (RoundedImageView) findViewById(R.id.img);

		
		txtname.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(GroupChatActivity.this, GroupInfo.class);
				DataManager.groupid = groupid;
				startActivity(i);
			}
		});
		toolbar.addView(mCustomView);
		myuserid = session.getuserid();

		int seencount = dbmsg.getunseengroupcount(groupid, "no");
		if (seencount > 0) {
			dbmsg.updategroupseen(groupid);
		}

		btnsend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addNewMessage();
			}
		});

		getmessage();
		btnfile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectImage();
			}
		});
		smileyintegration();
	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			getmessage();

		}
	};
	
	@Override public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Choose");
		menu.add(Menu.NONE, 1, 0, "Copy");
		menu.add(Menu.NONE, 2, 1, "Share");
		}


		@Override public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case 1:
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();

		//The textview which holds the parsed xml
		CharSequence selectedTexts = ((EmojiconTextView)menuInfo.targetView.findViewById(R.id.other_text)).getText();
		ClipboardManager clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("Intent", (CharSequence) selectedTexts);
		((android.content.ClipboardManager) clipboard).setPrimaryClip(clip);
		Toast.makeText(getApplicationContext(), "Selected text is copied",Toast.LENGTH_SHORT).show();
		break;

		case 2:
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		CharSequence textToShare = ((EmojiconTextView) info.targetView.findViewById(R.id.other_text)).getText();
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
		shareIntent.setType("text/plain");
		startActivity(Intent.createChooser(shareIntent, "Hiaite zangin share in"));
		}
		return super.onContextItemSelected(item); }


	public void smileyintegration()
	{
		final View rootView = findViewById(R.id.root_view);
		final EmojiconsPopup popup = new EmojiconsPopup(rootView, this);

		//Will automatically set size according to the soft keyboard size        
		popup.setSizeForSoftKeyboard();

		//Set on emojicon click listener
		popup.setOnEmojiconClickedListener(new OnEmojiconClickedListener() {

			@Override
			public void onEmojiconClicked(Emojicon emojicon) {
				etmessage.append(emojicon.getEmoji());
			}
		});

		//Set on backspace click listener
		popup.setOnEmojiconBackspaceClickedListener(new OnEmojiconBackspaceClickedListener() {

			@Override
			public void onEmojiconBackspaceClicked(View v) {
				KeyEvent event = new KeyEvent(
						0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
				etmessage.dispatchKeyEvent(event);
			}
		});

		//If the emoji popup is dismissed, change btnsmiley to smiley icon
		popup.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				changeEmojiKeyboardIcon(btnsmiley, R.drawable.smiley);
			}
		});

		//If the text keyboard closes, also dismiss the emoji popup
		popup.setOnSoftKeyboardOpenCloseListener(new OnSoftKeyboardOpenCloseListener() {

			@Override
			public void onKeyboardOpen(int keyBoardHeight) {

			}

			@Override
			public void onKeyboardClose() {
				if(popup.isShowing())
					popup.dismiss();
			}
		});

		//On emoji clicked, add it to edittext
		popup.setOnEmojiconClickedListener(new OnEmojiconClickedListener() {

			@Override
			public void onEmojiconClicked(Emojicon emojicon) {
				etmessage.append(emojicon.getEmoji());
			}
		});

		//On backspace clicked, emulate the KEYCODE_DEL key event
		popup.setOnEmojiconBackspaceClickedListener(new OnEmojiconBackspaceClickedListener() {

			@Override
			public void onEmojiconBackspaceClicked(View v) {
				KeyEvent event = new KeyEvent(
						0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
				etmessage.dispatchKeyEvent(event);
			}
		});
		
		// To toggle between text keyboard and emoji keyboard keyboard(Popup)
		btnsmiley.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				//If popup is not showing => emoji keyboard is not visible, we need to show it
				if(!popup.isShowing()){
					
					//If keyboard is visible, simply show the emoji popup
					if(popup.isKeyBoardOpen()){
						popup.showAtBottom();
						changeEmojiKeyboardIcon(btnsmiley, R.drawable.ic_action_keyboard);
					}
					
					//else, open the text keyboard first and immediately after that show the emoji popup
					else{
						etmessage.setFocusableInTouchMode(true);
						etmessage.requestFocus();
						popup.showAtBottomPending();
						final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						inputMethodManager.showSoftInput(etmessage, InputMethodManager.SHOW_IMPLICIT);
						changeEmojiKeyboardIcon(btnsmiley, R.drawable.ic_action_keyboard);
					}
				}
				
				//If popup is showing, simply dismiss it to show the undelying text keyboard 
				else{
					popup.dismiss();
				}
			}
		});	
	}
	
	public void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId){
		iconToBeChanged.setImageResource(drawableResourceId);
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

	@Override
	public void onBackPressed() {
		Intent i = new Intent(this, MainActivity.class);
		i.setAction("splash");
		finish();
		startActivity(i);
		overridePendingTransition(0, 0);
	}

	public void selectImage() {

		builder = new Dialog(GroupChatActivity.this,
				android.R.style.Theme_Translucent_NoTitleBar);
		builder.setContentView(R.layout.dialog_row);
		builder.setCanceledOnTouchOutside(true);
		Window window = builder.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();

		wlp.gravity = Gravity.BOTTOM;
		wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(wlp);

		builder.show();
		TextView opt1 = (TextView) builder.findViewById(R.id.txtnewphoto);
		TextView opt2 = (TextView) builder.findViewById(R.id.txtexistphoto);
		TextView opt3 = (TextView) builder.findViewById(R.id.txtnewVideo);
		TextView opt4 = (TextView) builder.findViewById(R.id.txtnewAudio);
		TextView cancel = (TextView) builder.findViewById(R.id.txtcancel);

		opt1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

				selectedattachment = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

				intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedattachment);

				// start the image capture Intent
				startActivityForResult(intent,
						CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
			}
		});

		opt2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				// selectedattachment = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
				//
				// intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedattachment);

				// start the image capture Intent
				startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE);

				builder.dismiss();

			}
		});

		opt3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

				selectedattachment = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

				// set video quality
				intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

				intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedattachment); // set
																				// the
																				// image
																				// file
				// name

				// start the video capture Intent
				startActivityForResult(intent,
						CAMERA_CAPTURE_VIDEO_REQUEST_CODE);

				builder.dismiss();
			}
		});

		opt4.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(
						MediaStore.Audio.Media.RECORD_SOUND_ACTION);

				startActivityForResult(intent, 400);
				builder.dismiss();
				builder.dismiss();
			}
		});

		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				builder.dismiss();

			}
		});

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// save file url in bundle as it will be null on screen orientation
		// changes
		outState.putParcelable("file_uri", selectedattachment);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		// get the file url
		selectedattachment = savedInstanceState.getParcelable("file_uri");
	}

	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		@SuppressWarnings("deprecation")
		android.database.Cursor cursor = managedQuery(contentUri, proj, // Which
																		// columns
																		// to
																		// return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {

			if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {

				messagetype = "photo";
				hasAttachment = true;
				filePath = selectedattachment.getPath();

				new UploadFileToServer().execute();

			} else if (requestCode == IMAGE_PICK_REQUEST_CODE) {

				Uri selectedImageUri = data.getData();

				filePath = getRealPathFromURI(selectedImageUri);

				messagetype = "photo";
				hasAttachment = true;

				filename = new String(new File(filePath).getName());
				System.out.println("filename---" + filename);
				new UploadFileToServer().execute();

			}

			else if (requestCode == 300) {

				messagetype = "video";
				MediaPlayer mp = MediaPlayer.create(this, selectedattachment);
				millis = mp.getDuration();
				mp.release();

				if (millis < 45000) {
					hasAttachment = true;
					filePath = selectedattachment.getPath();
					new UploadFileToServer().execute();
				} else {
					hasAttachment = false;
					filealert("Video");
				}

			}

			else if (requestCode == 400) {
				Uri selectedImageUri = data.getData();

				filePath = getRealPathFromURI(selectedImageUri);

				messagetype = "audio";
				MediaPlayer mp = MediaPlayer.create(this, selectedImageUri);

				millis = mp.getDuration();
				mp.release();

				if (millis < 45000) {
					filename = new String(new File(filePath).getName());
					System.out.println("filepath---" + filePath);
					new UploadFileToServer().execute();
				} else {
					filealert("Audio");
				}

			}
		}
	}

	public Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	public void filealert(String filetype) {
		AlertDialog.Builder confirm = new AlertDialog.Builder(this);
		confirm.setTitle(filetype + " is too long.");
		confirm.setMessage("Select new " + filetype);

		confirm.setNegativeButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

				selectImage();
			}
		});

		confirm.show().show();
	}

	public File getOutputMediaFile(int type) {

		// External sdcard location
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				DataManager.IMAGE_DIRECTORY_NAME);

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("Image", "Oops! Failed create "
						+ DataManager.IMAGE_DIRECTORY_NAME + " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			filename = "IMG_" + timeStamp + ".jpg";
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			filename = "VID_" + timeStamp + ".mp4";
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
		} else if (type == MEDIA_TYPE_SOUND) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "AUD_" + timeStamp + ".m4a");
		} else {
			return null;
		}
		System.out.println("mediafile---" + mediaFile);
		return mediaFile;
	}

	public static void getmessage() {
		if (dbmsg.getallgroupmsgcount(groupid) > 0) {
			List<Message> messagelist = dbmsg.getallgroupmsg(groupid);

			if (messagelist.size() > 0) {
				amList.clear();
				Message messages;
				for (Message msg : messagelist) {
					messages = new Message();
					firstname = msg.getFname();
					sender_lname = msg.getLname();
					String tstamp = msg.getTimestamp();
					String message = msg.getMessage();
					String me = msg.getIsmine();
					String msgtype = "";

					if (msg.getMsgtype() != null) {

						msgtype = msg.getMsgtype();

					} else {
						msgtype = "";
					}
					messages.setMessage(message);
					messages.setFname(firstname);
					messages.setLname(sender_lname);
					messages.setTimestamp(tstamp);
					messages.setIsmine(me);
					messages.setMsgtype(msgtype);
					amList.add(messages);
				}

				DbGroup db = new DbGroup(context);
				String groupname = db.getgroupname(groupid);
				System.out.println("groupname--"+groupname);
				txtname.setText("" + groupname);

				txtalert.setVisibility(View.INVISIBLE);

				AwesomeAdapter adapter = new AwesomeAdapter(context, amList);
				adapter.notifyDataSetChanged();
				list.setAdapter(adapter);
				list.setSelection(amList.size() - 1);

			}
		}

		int seencount = dbmsg.getunseengroupcount(groupid, "no");
		if (seencount > 0) {
			dbmsg.updategroupseen(groupid);
		}
		cancelnotification();
	}

	public static void update() {
		List<Message> messagelist = dbmsg.getallgroupmsg(groupid);

		if (messagelist.size() > 0) {
			amList.clear();
			Message messages;
			for (Message msg : messagelist) {
				messages = new Message();
				String fname = msg.getFname();
				String lname = msg.getLname();
				String tstamp = msg.getTimestamp();
				String message = msg.getMessage();
				String me = msg.getIsmine();
				System.out.println(message);
				messages.setMessage(message);
				messages.setFname(fname);
				messages.setLname(lname);
				messages.setTimestamp(tstamp);
				messages.setIsmine(me);

				amList.add(messages);
			}

			// txtalert.setVisibility(View.INVISIBLE);

			AwesomeAdapter adapter = new AwesomeAdapter(context, amList);
			adapter.notifyDataSetChanged();

		}

	}

	public class sendgroupmessage extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... params) {

			response = APIManager.sendgroupmessage(myuserid, groupid, message,
					deviceid, messagetype);

			return "";

		}

		@Override
		protected void onPostExecute(String result) {

			if (DataManager.status.equals("2")) {
				session.logoutUser();
			}

		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}

	public static void cancelnotification() {

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.cancel(Integer.valueOf(groupid));
	}

	void addNewMessage() {

		messagetype = "text";
		message = etmessage.getText().toString();
		if (message.length() > 0) {
			new sendgroupmessage().execute();
		}
		etmessage.setText("");
		Date todaydate = new Date();

		String date = String.valueOf(todaydate.getTime());
		dbmsg.addContact(new Message(firstname, sender_lname, message,
				myuserid, myuserid, date, logintype, "group", "yes", groupid,
				"yes", messagetype));
		getmessage();
	}

	@Override
	public void onResume() {
		super.onResume();
		DataManager.isindividualopen = true;
	}

	@Override
	public void onPause() {
		super.onPause();
		DataManager.isindividualopen = false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		DataManager.isindividualopen = false;
	}

	public static class AwesomeAdapter extends BaseAdapter {
		public static Context mContext;
		public static ArrayList<Message> mMessages;

		public AwesomeAdapter(Context context, ArrayList<Message> amList) {
			super();
			AwesomeAdapter.mContext = context;
			AwesomeAdapter.mMessages = amList;
		}

		@Override
		public int getCount() {

			return mMessages.size();
		}

		@Override
		public Object getItem(int position) {
			return mMessages.get(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Message message1 = (Message) this.getItem(position);

			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.othersms, parent, false);
				holder.message = (EmojiconTextView) convertView
						.findViewById(R.id.other_text);
				holder.txtsender= (TextView) convertView
						.findViewById(R.id.txtsender);
				holder.thumb = (FetchableImageView) convertView
						.findViewById(R.id.thumb);
				
				holder.msgLL = (LinearLayout) convertView
						.findViewById(R.id.otherSMSLL);
				holder.llthumb = (LinearLayout) convertView
						.findViewById(R.id.llthumb);
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();

			if (message1.getMsgtype().equalsIgnoreCase("text")
					|| message1.getMsgtype().equalsIgnoreCase(""))
			{
				holder.message.setVisibility(View.VISIBLE);
				holder.llthumb.setVisibility(View.GONE);

//				holder.message.setText(message1.getFname() + " "
//						+ message1.getLname() + ": \n" + message1.getMessage());
				LayoutParams lp = (LayoutParams) holder.message
						.getLayoutParams();
				if (message1.getIsmine().equals("yes")) {
					holder.message.setText(message1.getMessage());
					holder.message
							.setBackgroundResource(R.drawable.message_you);
					holder.message.setPadding(15, 15, 15,15);
					lp.gravity = Gravity.RIGHT;
					lp.leftMargin = 50;
					lp.rightMargin = 20;
				}
				else {
					holder.message.setText(message1.getFname() + " "
							+ message1.getLname() + ": \n" + message1.getMessage());
					holder.message
							.setBackgroundResource(R.drawable.message_from);
					holder.message.setPadding(15, 15, 15,15);
					lp.gravity = Gravity.LEFT;
					lp.rightMargin = 50;
					lp.leftMargin = 20;
				}
				holder.message.setLayoutParams(lp);
				holder.message.setTextColor(mContext.getResources().getColor(R.color.Black));
			} 
			else 
			{
				LayoutParams lp1 = (LayoutParams) holder.txtsender
						.getLayoutParams();
				holder.llthumb.setVisibility(View.VISIBLE);
				holder.llthumb.setPadding(15, 15, 15,15);
				holder.message.setVisibility(View.GONE);
				holder.thumb.setVisibility(View.VISIBLE);
				holder.txtsender.setText(""+message1.getFname() + " "
						+ message1.getLname());
				if (message1.getMsgtype().equals("video")) {

				
					holder.thumb.setImage(message1.getMessage(),
							R.drawable.placeholder_video);
					final String url = DataManager.FILE_PATH
							+ message1.getMessage().toString();
					holder.thumb.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.setDataAndType(Uri.parse(url), "video/*");
							mContext.startActivity(intent);
						}
					});
				} else if (message1.getMsgtype().equals("audio")) {
					holder.thumb.setImage(message1.getMessage(),
							R.drawable.placeholder_audio);
					final String url = DataManager.FILE_PATH
							+ message1.getMessage();
					holder.thumb.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Uri myUri = Uri.parse(url);
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.setDataAndType(myUri, "audio/*");
							mContext.startActivity(intent);
						}
					});
				} else if (message1.getMsgtype().equals("photo")) {
					holder.thumb.setImage(
							DataManager.FILE_PATH + message1.getMessage(),
							R.drawable.placeholder_image);
					final String url = DataManager.FILE_PATH
							+ message1.getMessage();
					holder.thumb.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.setDataAndType(Uri.parse(url), "image/*");
							mContext.startActivity(intent);
						}
					});
				}
				if (message1.getIsmine().equals("yes")) {
					holder.llthumb.setBackgroundResource(R.drawable.message_you);
					holder.llthumb.setPadding(15, 15, 15,15);
					lp1.gravity = Gravity.RIGHT;
					lp1.topMargin = 10;
					lp1.bottomMargin = 10;
					lp1.leftMargin = 50;
					lp1.rightMargin = 20;
				}
				// If not mine then it is from sender to show orange background
				// and
				// align to left
				else {
					holder.llthumb.setBackgroundResource(R.drawable.message_from);
					holder.llthumb.setPadding(15, 15, 15,15);
					lp1.gravity = Gravity.LEFT;
					lp1.topMargin = 10;
					lp1.bottomMargin = 10;
					lp1.rightMargin = 50;
					lp1.leftMargin = 20;
				}
				holder.llthumb.setLayoutParams(lp1);
			}
			LayoutParams lp = (LayoutParams) holder.message.getLayoutParams();
			LayoutParams lp1 = (LayoutParams) holder.llthumb.getLayoutParams();

			// check if it is a status message then remove background, and
			// change text color.

			// Check whether message is mine to show green background and align
			// to right
			if (message1.getIsmine().equalsIgnoreCase("yes")) {
				holder.message.setBackgroundResource(R.drawable.message_you);
				holder.llthumb.setBackgroundResource(R.drawable.message_you);
				holder.llthumb.setPadding(15, 15, 15, 15);
				holder.msgLL.setGravity(Gravity.RIGHT);
				lp1.gravity = Gravity.RIGHT;
				lp.gravity = Gravity.RIGHT;
				lp1.leftMargin = 50;
				lp1.topMargin = 5;
				lp1.bottomMargin = 5;
				lp1.rightMargin = 5;
				lp.leftMargin = 50;
				lp.rightMargin = 5;

			}
			// If not mine then it is from sender to show orange background and
			// align to left
			else {
				holder.message.setBackgroundResource(R.drawable.message_from);
				holder.llthumb.setBackgroundResource(R.drawable.message_from);

				holder.msgLL.setGravity(Gravity.LEFT);
				lp.gravity = Gravity.LEFT;
				lp1.gravity = Gravity.LEFT;
				lp1.rightMargin = 50;
				lp1.topMargin = 5;
				lp1.bottomMargin = 5;
				lp1.leftMargin = 5;
				lp.rightMargin = 50;
				lp.leftMargin = 5;
			}
			holder.message.setLayoutParams(lp);
			holder.thumb.setLayoutParams(lp1);
			holder.message.setTextColor(mContext.getResources().getColor(R.color.Black));

			return convertView;
		}

		class ViewHolder {
			EmojiconTextView message;
			TextView txtsender;
			FetchableImageView thumb;
			LinearLayout msgLL, llthumb;
		}

		@Override
		public long getItemId(int position) {
			// Unimplemented, because we aren't using Sqlite.
			return 0;
		}
	}

	private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(progress_bar_type);
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			// Making progress bar visible
			pDialog.setProgress(progress[0]);

		}

		@Override
		protected String doInBackground(Void... params) {
			return uploadFile();
		}

		@SuppressWarnings("deprecation")
		private String uploadFile() {
			String responseString = null;

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(DataManager.FILE_UPLOAD_URL);

			try {
				AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
						new ProgressListener() {

							@Override
							public void transferred(long num) {
								publishProgress((int) ((num / (float) totalSize) * 100));
							}
						});

				File sourceFile = new File(filePath);

				// Adding file data to http body
				entity.addPart("image", new FileBody(sourceFile));

				totalSize = entity.getContentLength();
				httppost.setEntity(entity);

				// Making server call
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity r_entity = response.getEntity();

				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					// Server response
					responseString = EntityUtils.toString(r_entity);
				} else {
					responseString = "Error occurred! Http Status Code: "
							+ statusCode;
				}

			} catch (ClientProtocolException e) {
				responseString = e.toString();
			} catch (IOException e) {
				responseString = e.toString();
			}

			return responseString;

		}

		@Override
		protected void onPostExecute(String result) {
			Log.e("Response", "Response from server: " + result);
			super.onPostExecute(result);
			pDialog.dismiss();
			result = result.trim();
			if (result.equalsIgnoreCase("success")) {
				message = filename;

				new sendgroupmessage().execute();

				etmessage.setText("");
				Date todaydate = new Date();

				String date = String.valueOf(todaydate.getTime());
				dbmsg.addContact(new Message(firstname, sender_lname, message,
						myuserid, myuserid, date, logintype, "group", "yes",
						groupid, "yes", messagetype));
				getmessage();

			}
		}

	}

	/**
	 * Method to show alert dialog
	 * */
	private void showAlert(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message).setTitle("Response from Servers")
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// do nothing
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case progress_bar_type: // we set this to 0
			pDialog = new ProgressDialog(this);
			pDialog.setMessage("Sending file....");
			pDialog.setIndeterminate(false);
			pDialog.setMax(100);
			pDialog.setCanceledOnTouchOutside(false);
			pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pDialog.setCancelable(false);
			pDialog.show();
			return pDialog;
		default:
			return null;
		}
	}
}
