package br.com.medicalservices.app;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbMessage extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "chat";

	// Contacts table name
	private static final String TABLE_CONTACTS = "messagetable";

	// Contacts Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_SENDER_FNAME = "senderfname";
	private static final String KEY_SENDER_LNAME = "senderlname";
	private static final String KEY_MESSAGE = "message";
	private static final String KEY_SENDER_USERID = "senderuserid";
	private static final String KEY_MY_USERID = "myuserid";
	private static final String KEY_CHATTYPE = "chattype";
	private static final String KEY_LOGINTYPE = "logintype";
	private static final String KEY_ISMINE = "ismine";
	private static final String TIMESTAMP = "timestamp";
	private static final String GROUPID = "groupid";
	private static final String ISSEEN = "isseen";
	private static final String MESSAGETYPE = "msgtype";
	
	public DbMessage(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_SENDER_FNAME
				+ " TEXT, " + KEY_SENDER_LNAME + " TEXT, " + KEY_MESSAGE
				+ " TEXT, " + KEY_ISMINE + " TEXT, " + KEY_SENDER_USERID
				+ " TEXT, " + KEY_MY_USERID + " TEXT, " + KEY_LOGINTYPE
				+ " TEXT, " + ISSEEN + " TEXT, " + KEY_CHATTYPE + " TEXT, "
				+ MESSAGETYPE + " TEXT, " + GROUPID + " TEXT, " + TIMESTAMP + " INTEGER" + ")";

		db.execSQL(CREATE_CONTACTS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed

	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Adding new contact
	void addContact(Message message) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(KEY_SENDER_FNAME, message.getFname());
		values.put(KEY_SENDER_LNAME, message.getLname());
		values.put(KEY_MESSAGE, message.getMessage());
		values.put(KEY_SENDER_USERID, message.getFromuserid());
		values.put(KEY_LOGINTYPE, message.getLogintype());
		values.put(KEY_CHATTYPE, message.getChattype());
		values.put(KEY_MY_USERID, message.getMyuserid());
		values.put(KEY_ISMINE, message.getIsmine());
		values.put(TIMESTAMP, message.getTimestamp());
		values.put(GROUPID, message.getGroupid());
		values.put(ISSEEN, message.getIsseen());
		values.put(MESSAGETYPE, message.getMsgtype());
		db.insert(TABLE_CONTACTS, null, values);
		db.close(); // Closing database connection
	}

	// Getting All Contacts
	public List<Message> getindividualuserid(String senderid) {
		List<Message> messageList = new ArrayList<Message>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " WHERE "
				+ KEY_SENDER_USERID + "=? AND " + KEY_CHATTYPE
				+ " =? ORDER BY TIMESTAMP";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, new String[] { senderid,
				"individual" });

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Message message = new Message();

				message.setFname(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_SENDER_FNAME)));
				message.setLname(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_SENDER_LNAME)));
				message.setGroupid(cursor.getString(cursor
						.getColumnIndexOrThrow(GROUPID)));
				message.setFromuserid(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_SENDER_USERID)));
				message.setMessage(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_MESSAGE)));
				message.setTimestamp(cursor.getString(cursor
						.getColumnIndexOrThrow(TIMESTAMP)));
				message.setIsmine(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_ISMINE)));
				message.setLogintype(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_LOGINTYPE)));
				message.setMsgtype(cursor.getString(cursor
						.getColumnIndexOrThrow(MESSAGETYPE)));
				messageList.add(message);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();

		// return contact list
		return messageList;
	}
	
	public void deleteuser(String userid) {

		// Select All Query
		System.out.println("title--"+userid);
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CONTACTS,
		        KEY_SENDER_USERID + "=?",
		        new String[] {userid});

		db.close();

	}
	
	public void deletegroup(String groupid) {

		// Select All Query
		System.out.println("title--"+groupid);
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CONTACTS,
		        GROUPID + "=?",
		        new String[] {groupid});

		db.close();

	}

	public String getname(String senderid) {
		String name = "";
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " WHERE "
				+ KEY_SENDER_USERID + "=?";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, new String[] { senderid });

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				GroupPojo grp = new GroupPojo();

				// grp.setGroupname(cursor.getString(cursor.getColumnIndexOrThrow(KEY_GROUPNAME)));
				String fname = cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_SENDER_FNAME));
				String lname = cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_SENDER_LNAME));

				name = fname + " " + lname;
				// messageList.add(grp);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();

		// return contact list
		return name;
	}

	public List<Message> getallgroupmsg(String groupid) {
		List<Message> messageList = new ArrayList<Message>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " WHERE "
				+ GROUPID + "=? AND " + KEY_CHATTYPE + "=? ORDER BY TIMESTAMP";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, new String[] { groupid,
				"group" });

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Message message = new Message();

				message.setFname(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_SENDER_FNAME)));
				message.setLname(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_SENDER_LNAME)));
				message.setGroupid(cursor.getString(cursor
						.getColumnIndexOrThrow(GROUPID)));
				message.setFromuserid(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_SENDER_USERID)));
				message.setMessage(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_MESSAGE)));
				message.setTimestamp(cursor.getString(cursor
						.getColumnIndexOrThrow(TIMESTAMP)));
				message.setIsmine(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_ISMINE)));
				message.setLogintype(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_LOGINTYPE)));
				message.setMsgtype(cursor.getString(cursor
						.getColumnIndexOrThrow(MESSAGETYPE)));
				messageList.add(message);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();

		// return contact list
		return messageList;
	}

	public void updategroupseen(String groupid) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(ISSEEN, "yes");

		db.update(TABLE_CONTACTS, values, GROUPID + " = ?",
				new String[] { groupid });
	}

	public void updateindividualseen(String senderid) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(ISSEEN, "yes");
		db.update(TABLE_CONTACTS, values, KEY_SENDER_USERID + " = ?",
				new String[] { senderid });

	}

	public int getallgroupmsgcount(String groupid) {

		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " WHERE "
				+ GROUPID + "=? ORDER BY TIMESTAMP";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, new String[] { groupid });

		// return contact list
		return cursor.getCount();
	}

	public List<Message> getallhistory() {
		List<Message> messageList = new ArrayList<Message>();
		// Select All Query
		// String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS +
		// " GROUP BY "+GROUPID + ", "+ KEY_SENDER_USERID +
		// " ORDER BY TIMESTAMP DESC";
		// String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS +
		// " GROUP BY "
		// + GROUPID + " , " + KEY_SENDER_USERID
		// + " ORDER BY TIMESTAMP DESC";

		String selectQuery = "SELECT  * FROM messagetable WHERE chattype='individual' GROUP BY  senderuserid UNION SELECT  * FROM messagetable WHERE chattype='group' GROUP BY  groupid  ORDER BY TIMESTAMP DESC;";

		System.out.println("query---" + selectQuery);
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Message message = new Message();

				message.setFname(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_SENDER_FNAME)));
				message.setLname(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_SENDER_LNAME)));
				message.setMessage(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_MESSAGE)));
				message.setTimestamp(cursor.getString(cursor
						.getColumnIndexOrThrow(TIMESTAMP)));
				message.setIsmine(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_ISMINE)));
				message.setChattype(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_CHATTYPE)));
				message.setLogintype(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_LOGINTYPE)));
				message.setGroupid(cursor.getString(cursor
						.getColumnIndexOrThrow(GROUPID)));
				message.setFromuserid(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_SENDER_USERID)));
				message.setIsseen(cursor.getString(cursor
						.getColumnIndexOrThrow(ISSEEN)));
				message.setMsgtype(cursor.getString(cursor
						.getColumnIndexOrThrow(MESSAGETYPE)));
				messageList.add(message);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();

		// return contact list
		return messageList;
	}

	public int getunseengroupcount(String groupid, String flag) {
		int count = 0;
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " WHERE "
				+ GROUPID + "=? AND " + ISSEEN + "=? AND " + KEY_CHATTYPE
				+ "=?";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, new String[] { groupid, flag,
				"group" });

		count = cursor.getCount();

		cursor.close();
		db.close();

		// return contact list
		return count;
	}

	public int getunseeenindividual(String senderid, String flag) {
		int count = 0;
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " WHERE "
				+ KEY_SENDER_USERID + "=? AND " + ISSEEN + "=? AND "
				+ KEY_CHATTYPE + "=?";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, new String[] { senderid, flag,
				"individual" });

		count = cursor.getCount();

		cursor.close();
		db.close();

		// return contact list
		return count;
	}

}