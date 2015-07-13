package br.com.medicalservices.app;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbGroup extends SQLiteOpenHelper {
 
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "groupchat";
 
    // Contacts table name
    private static final String TABLE_CONTACTS = "grouptable";
 
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_GROUPID = "groupid";
    private static final String KEY_GROUPNAME = "groupname";
    private static final String KEY_MEMBERID = "memberid";
    private static final String KEY_MEMBERNAME = "membername";
    private static final String KEY_ADMINID = "adminid";
    private static final String TIMESTAMP = "timestamp";
     
    
    public DbGroup(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_GROUPID + " TEXT, " 
                + KEY_GROUPNAME + " TEXT, " +  KEY_MEMBERID + " TEXT, " 
                +  KEY_ADMINID + " TEXT, " +  KEY_MEMBERNAME + " TEXT, "
                +  TIMESTAMP + " INTEGER" + ")";
        
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
    void addContact(GroupPojo grp) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
 
        values.put(KEY_GROUPID, grp.getGroupid()); 
        values.put(KEY_GROUPNAME, grp.getGroupname());
        values.put(KEY_MEMBERID, grp.getMemberid());
        values.put(KEY_MEMBERNAME, grp.getMembername());
        values.put(KEY_ADMINID, grp.getAdminid());
        values.put(TIMESTAMP, grp.getTimestamp());
       
        
        
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection
    }
    
	public void updategroupname(String groupid, String groupname) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(KEY_GROUPNAME, groupname);

		db.update(TABLE_CONTACTS, values, KEY_GROUPID + " = ?",
				new String[] { groupid });
	}
 
    public void deletegroup(String groupid) {

		// Select All Query
		System.out.println("title--"+groupid);
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CONTACTS,
		        KEY_GROUPID + "=?",
		        new String[] {groupid});

		db.close();

	}


    // Getting All Contacts
    public List<GroupPojo> getallmembers(String groupid) {
    	List<GroupPojo> messageList = new ArrayList<GroupPojo>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " WHERE "+KEY_GROUPID+"=? ORDER BY TIMESTAMP";
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {groupid});
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                GroupPojo grp = new GroupPojo();
                
                grp.setGroupid(cursor.getString(cursor.getColumnIndexOrThrow(KEY_GROUPID)));
                grp.setGroupname(cursor.getString(cursor.getColumnIndexOrThrow(KEY_GROUPNAME)));
                grp.setMemberid(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MEMBERID)));
                grp.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(TIMESTAMP)));
                grp.setMembername(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MEMBERNAME)));
                            
                messageList.add(grp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
 
        // return contact list
        return messageList;
    }
    
    public String getgroupname(String groupid) {
    	String groupname = "";
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " WHERE "+KEY_GROUPID+"=? GROUP BY "+KEY_GROUPID;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {groupid});
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                GroupPojo grp = new GroupPojo();
                
//                  grp.setGroupname(cursor.getString(cursor.getColumnIndexOrThrow(KEY_GROUPNAME)));
                  groupname = cursor.getString(cursor.getColumnIndexOrThrow(KEY_GROUPNAME));
                  
                            
//                messageList.add(grp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
 
        // return contact list
        return groupname;
    }
    
    public int checkgrpuser(String groupid, String memberid) {
    	int count = 0;
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " WHERE "+KEY_GROUPID+"=? AND "+KEY_MEMBERID +"=?";
        System.out.println("select groupid--"+groupid);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {groupid, memberid});
 
        count = cursor.getCount();
      
    	cursor.close();
		db.close();

        // return contact list
        return count;
    }
    
    
    
    public List<GroupPojo> getallhistory() {
    	List<GroupPojo> groupList = new ArrayList<GroupPojo>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " ORDER BY TIMESTAMP";
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                GroupPojo grp = new GroupPojo();
                
                grp.setGroupid(cursor.getString(cursor.getColumnIndexOrThrow(KEY_GROUPID)));
                grp.setGroupname(cursor.getString(cursor.getColumnIndexOrThrow(KEY_GROUPNAME)));
                grp.setMemberid(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MEMBERID)));
                grp.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(TIMESTAMP)));
                grp.setMembername(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MEMBERNAME)));
                
                
                groupList.add(grp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
 
        // return contact list
        return groupList;
    }
 
 
}