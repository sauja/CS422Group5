package com.cs442.group5.feedback.database.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cs442.group5.feedback.Constants;

/**
 * Created by sauja7 on 10/16/16.
 */

public class UserDBHelper extends SQLiteOpenHelper
{
	private static final String TAG = UserDBHelper.class.getSimpleName();

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "feedback";
	private static final String TABLE_USER = "user";

	private static final String KEY_ID = "_id";
	private static final String KEY_USERNAME = "userName";
	private static final String KEY_PASSWORD = "password";
	private static final String KEY_FNAME = "fname";
	private static final String KEY_LNAME = "lname";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_ADDRESS = "address";
	private static final String KEY_CONTACTNO = "contactNo";

	public static String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER
			+ "("
			+ KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_USERNAME + " TEXT UNIQUE,"
			+ KEY_PASSWORD + " TEXT,"
			+ KEY_FNAME + " TEXT,"
			+ KEY_LNAME + " TEXT,"
			+ KEY_EMAIL + " TEXT,"
			+ KEY_ADDRESS + " TEXT,"
			+ KEY_CONTACTNO + " TEXT"
			+ ")";


	public UserDBHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{

		db.execSQL(CREATE_USER_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
		onCreate(db);
	}

	public int getCount()
	{
		SQLiteDatabase db1 = this.getReadableDatabase();
		return (int) DatabaseUtils.queryNumEntries(db1, TABLE_USER);
	}

	public User checkCredential(User user)
	{
		SQLiteDatabase db1 = this.getReadableDatabase();
		String countQuery = "select * from  " + TABLE_USER
				+ " where " + KEY_USERNAME + "='" + user.getUserName() + "' AND " + KEY_PASSWORD + "='" + user.getPassword() + "'";
		Cursor cursor = db1.rawQuery(countQuery, null);
		if (cursor != null && cursor.moveToFirst())
		{
			user.setFname(cursor.getString(cursor.getColumnIndex(KEY_FNAME)));
			user.setLname(cursor.getString(cursor.getColumnIndex(KEY_LNAME)));
			user.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
			user.setContactNo(cursor.getString(cursor.getColumnIndex(KEY_CONTACTNO)));
			user.setAddress(cursor.getString(cursor.getColumnIndex(KEY_ADDRESS)));
			cursor.close();

		} else
			user = null;
		cursor.close();
		//db1.close();
		return user;
	}

	public int addUser(User user)
	{
		SQLiteDatabase db1 = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(KEY_USERNAME, user.getUserName());
		values.put(KEY_PASSWORD, user.getPassword());
		values.put(KEY_FNAME, user.getFname());
		values.put(KEY_LNAME, user.getLname());
		values.put(KEY_EMAIL, user.getEmail());
		values.put(KEY_ADDRESS, user.getAddress());
		values.put(KEY_CONTACTNO, user.getContactNo());

		// Inserting Row
		int returnCode = -1;
		try
		{
			db1.insert(TABLE_USER, null, values);

			Log.d(TAG, "Order inserted: " + user);
			returnCode = Constants.USER_ADDED;
		} catch (SQLiteConstraintException e)
		{
			returnCode = Constants.USER_ALREADY_REGISTERED;
			e.printStackTrace();
		} finally
		{
			db1.close(); // Closing database connection
		}
		return returnCode;
	}
}
