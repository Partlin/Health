package com.lin.health.temp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {
	private final String TAG = "SQLiteHelper";
	public static final String DATABASE_NAME = "sensors_database";
	public static final int DATABASE_VERSION = 1;
	public static final String TABLE_SENSORS = "sensors_table";
	public static final String TABLE_SENSOR_DETAILS = "sensor_details_table";
	public static final String KEY_ID = "_id";
	public static final String KEY_ADDRESS = "address";
	public static final String KEY_NAME = "name";
	public static final String KEY_TEMP_VALUE = "tempvalue";
	public static final String KEY_BATTERY_VALUE = "batteryvalue";
	public static final String KEY_TIMESTAMP = "timestamp";
	
	private static final String DATABASE_CREATE = "create table " +
			TABLE_SENSORS + "(" + KEY_ID + " integer primary key autoincrement, " +
			KEY_ADDRESS + " text, " + 
			KEY_NAME + " text, " + 
			KEY_TEMP_VALUE + " real, " +
			KEY_BATTERY_VALUE + " integer, " +
			KEY_TIMESTAMP + " text);";
	
	private static final String DATABASE_CREATE1 = "create table " +
			TABLE_SENSOR_DETAILS + "(" + KEY_ID + " integer primary key autoincrement, " +
			KEY_ADDRESS + " text, " +			
			KEY_TEMP_VALUE + " real, " +			
			KEY_TIMESTAMP + " text);";

	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.e(TAG,"SQLiteHelper(context)");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.e(TAG,"onCreate(db)");
		db.execSQL(DATABASE_CREATE);
		db.execSQL(DATABASE_CREATE1);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.e("SQLiteHelper", "upgrading database from old version " +
				oldVersion + "to new version " + newVersion);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSORS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSOR_DETAILS);
		onCreate(db);
	}

}
