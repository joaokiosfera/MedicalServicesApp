package br.com.medicalservices.app;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbUsers extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "userchat";

	// Contacts table name
	private static final String TABLE_CONTACTS = "usertable";

	// Contacts Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_USERID = "userid";
	private static final String KEY_FIRSTNAME = "firstname";
	private static final String KEY_LASTNAME = "lastname";
	private static final String KEY_PROFILEPIC = "profilepic";
	private static final String KEY_LOGINTYPE = "logintype";

	public DbUsers(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_USERID + " TEXT, "
				+ KEY_FIRSTNAME + " TEXT, " + KEY_LASTNAME + " TEXT, "
				+ KEY_PROFILEPIC + " TEXT, " + KEY_LOGINTYPE + " TEXT" + ")";

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
	void addContact(UserPojo user) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(KEY_USERID, user.getUserid());
		values.put(KEY_FIRSTNAME, user.getFirstname());
		values.put(KEY_LASTNAME, user.getLastname());
		values.put(KEY_PROFILEPIC, user.getProfilepic());
		values.put(KEY_LOGINTYPE, user.getLogintype());

		db.insert(TABLE_CONTACTS, null, values);
		db.close(); // Closing database connection
	}

	 
    public String getprofileurl(String userid) {
    	String profilepicurl = "";
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " WHERE "+KEY_USERID+"=? LIMIT 1";
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {userid});
 
        if (cursor.moveToFirst()) {
            do {
                GroupPojo grp = new GroupPojo();

                profilepicurl = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PROFILEPIC));


            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
 
        // return contact list
        return profilepicurl;
    }

    public String getlogintype(String userid) {
    	String logintype = "";
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " WHERE "+KEY_USERID+"=? LIMIT 1";
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {userid});
 
        if (cursor.moveToFirst()) {
            do {
                GroupPojo grp = new GroupPojo();
                

                logintype = cursor.getString(cursor.getColumnIndexOrThrow(KEY_LOGINTYPE));
                  
                            

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
 
        // return contact list
        return logintype;
    }

    
	public List<UserPojo> getallusers() {
		List<UserPojo> messageList = new ArrayList<UserPojo>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " ORDER BY "
				+ KEY_FIRSTNAME;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				UserPojo user = new UserPojo();

				user.setFirstname(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_FIRSTNAME)));
				user.setLastname(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_LASTNAME)));
				user.setUserid(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_USERID)));
				user.setLogintype(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_LOGINTYPE)));
				user.setProfilepic(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_PROFILEPIC)));

				messageList.add(user);
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
		        KEY_USERID + "=?",
		        new String[] {userid});

		db.close();

	}

	// Getting All Contacts
	public List<UserPojo> getuserdetail(String userid) {
		List<UserPojo> messageList = new ArrayList<UserPojo>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " WHERE "
				+ KEY_USERID + "=? ORDER BY " + KEY_FIRSTNAME;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, new String[] { userid });

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				UserPojo user = new UserPojo();

				user.setFirstname(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_FIRSTNAME)));
				user.setLastname(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_LASTNAME)));
				user.setUserid(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_USERID)));
				user.setLogintype(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_LOGINTYPE)));
				user.setProfilepic(cursor.getString(cursor
						.getColumnIndexOrThrow(KEY_PROFILEPIC)));

				messageList.add(user);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();

		// return contact list
		return messageList;
	}
	
	
	public int usercounter() {
		int counter = 0;
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS ;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		counter = cursor.getCount();
		cursor.close();
		db.close();

		// return contact list
		return counter;
	}
	
	public int checkuser(String userid) {
		int counter = 0;
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " WHERE "
				+ KEY_USERID + "=?";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, new String[] { userid });

		counter = cursor.getCount();
		cursor.close();
		db.close();

		// return contact list
		return counter;
	}

}