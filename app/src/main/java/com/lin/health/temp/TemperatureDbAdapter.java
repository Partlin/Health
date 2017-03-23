package com.lin.health.temp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;




public class TemperatureDbAdapter {
	private final String TAG = "TemperatureDbAdapter";
	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;
	private String[] databaseRow = {SQLiteHelper.KEY_ID,
			SQLiteHelper.KEY_ADDRESS, 
			SQLiteHelper.KEY_NAME,
			SQLiteHelper.KEY_TEMP_VALUE,
			SQLiteHelper.KEY_BATTERY_VALUE,
			SQLiteHelper.KEY_TIMESTAMP};
	
	private String[] databaseDetailRow = {SQLiteHelper.KEY_ID,
			SQLiteHelper.KEY_ADDRESS, 			
			SQLiteHelper.KEY_TEMP_VALUE,			
			SQLiteHelper.KEY_TIMESTAMP};
	
	private static TemperatureDbAdapter adapterInstance;
	public static synchronized TemperatureDbAdapter getTemperatureDbAdapter() {
		if (adapterInstance == null) {
			adapterInstance = new TemperatureDbAdapter();			
		}
		return adapterInstance;
		
	}
	
	public void open(Context context) throws SQLException {
		if (dbHelper == null) {
			dbHelper = new SQLiteHelper(context);
		}
		database = dbHelper.getWritableDatabase();		
	}
	
	public void close() {
		dbHelper.close();
		adapterInstance = null;
	}
	
	public void addSensor(TemperatureData sensor) throws Exception {
		
		if (sensorAlreadyExist(sensor)) {
			updateSensor(sensor);
		}
		else {
			insertSensor(sensor);
		}
		
	}
	
	public boolean sensorAlreadyExist(TemperatureData sensor) {
		String whereClause = SQLiteHelper.KEY_ADDRESS + " = ?";
		String[] whereArgs = new String[] {sensor.getSensorAddress()};
		Cursor cursor = database.query(SQLiteHelper.TABLE_SENSORS,
				databaseRow,
				whereClause,
				whereArgs,
				null, null, null);
		if (cursor.getCount() > 0) {
			//Log.e(TAG,"sensor already exist");
			cursor.close();
			return true;
		}
		//Log.e(TAG,"new sensor");
		cursor.close();
		return false;
	}
	
	public void updateSensor(TemperatureData sensor) {
		String whereClause = SQLiteHelper.KEY_ADDRESS + " = ?";
		String[] whereArgs = new String[] {sensor.getSensorAddress()};
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.KEY_TEMP_VALUE, sensor.getTemperatureValue());	
		values.put(SQLiteHelper.KEY_BATTERY_VALUE, sensor.getBatteryValue());
		values.put(SQLiteHelper.KEY_TIMESTAMP, sensor.getTimeStamp());
		database.update(SQLiteHelper.TABLE_SENSORS, values, whereClause, whereArgs);
		//Log.e(TAG,"sensor updated!");
	}
	
	public void updateSensorName(TemperatureData sensor) {
		String whereClause = SQLiteHelper.KEY_ADDRESS + " = ?";
		String[] whereArgs = new String[] {sensor.getSensorAddress()};
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.KEY_NAME, sensor.getSensorName());		
		database.update(SQLiteHelper.TABLE_SENSORS, values, whereClause, whereArgs);
		//Log.e(TAG,"sensor name updated!");
	}
	
	private void insertSensor(TemperatureData sensor) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.KEY_ADDRESS, sensor.getSensorAddress());
		values.put(SQLiteHelper.KEY_NAME, sensor.getSensorName());
		values.put(SQLiteHelper.KEY_TEMP_VALUE, sensor.getTemperatureValue());
		values.put(SQLiteHelper.KEY_BATTERY_VALUE, sensor.getBatteryValue());
		values.put(SQLiteHelper.KEY_TIMESTAMP, sensor.getTimeStamp());
		database.insert(SQLiteHelper.TABLE_SENSORS, null, values);
		//Log.e(TAG,"Sensor added!");
	}
	
	public void addSensorTimeStamp(TemperatureData sensor) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.KEY_ADDRESS, sensor.getSensorAddress());		
		values.put(SQLiteHelper.KEY_TEMP_VALUE, sensor.getTemperatureValue());		
		values.put(SQLiteHelper.KEY_TIMESTAMP, sensor.getTimeStamp());
		database.insert(SQLiteHelper.TABLE_SENSOR_DETAILS, null, values);
	}
	
	public void deleteSensor(TemperatureData sensor) {
		String whereClause = SQLiteHelper.KEY_ADDRESS + " = ?";
		String[] whereArgs = new String[] {sensor.getSensorAddress()};
		database.delete(SQLiteHelper.TABLE_SENSORS,
				whereClause,
				whereArgs);
		Log.e(TAG,"sensor has been removed from database!");
	}
	
	public void deleteTimeStampsOfSensor(TemperatureData sensor) {
		String whereClause = SQLiteHelper.KEY_ADDRESS + " = ?";
		String[] whereArgs = new String[] {sensor.getSensorAddress()};
		database.delete(SQLiteHelper.TABLE_SENSOR_DETAILS,
				whereClause,
				whereArgs);
		Log.e(TAG,"sensor timestamps has been removed from database!");
	}
	
	public void dropTable(String tableName) {
		database.execSQL("DROP TABLE IF EXISTS " + tableName);
		//Log.e(TAG,"table has been removed");
	}
	
	public ArrayList<TemperatureData> getAllSensors() {
		ArrayList<TemperatureData> sensors = new ArrayList<TemperatureData>();
		Cursor cursor = database.query(SQLiteHelper.TABLE_SENSORS, databaseRow, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			//Log.e(TAG,"Some sonsors exist in database");
			cursor.moveToFirst();
			while(!cursor.isAfterLast()) {
				TemperatureData sensor = cursorToTemperatureData(cursor);
				sensors.add(sensor);
				cursor.moveToNext();
			}
		}
		else {
				//Log.e(TAG,"No sensors exist already in database!");
		}		
		cursor.close();
		return sensors;
	}
	
	public TemperatureData getSensor(TemperatureData sensor) {
		String whereClause = SQLiteHelper.KEY_ADDRESS + " = ?";
		String[] whereArgs = new String[] {sensor.getSensorAddress()};
		Cursor cursor = database.query(SQLiteHelper.TABLE_SENSORS,
				databaseRow,
				whereClause,
				whereArgs,
				null, null, null);	
		//Log.e(TAG,"size of cursor returned by query: "+cursor.getCount());
		TemperatureData sensorData =  cursorToTemperatureData(cursor);
		cursor.close();
		return sensorData;
	}
	
	public TemperatureData getSensorById(long id) {
		String whereClause = SQLiteHelper.KEY_ID + " = ?";
		String[] whereArgs = new String[] {String.valueOf(id)};
		Cursor cursor = database.query(SQLiteHelper.TABLE_SENSORS,
				databaseRow,
				whereClause,
				whereArgs,
				null, null, null);	
		//Log.e(TAG,"size of cursor returned by query: "+cursor.getCount());
		TemperatureData sensorData =  cursorToTemperatureData(cursor);
		cursor.close();
		return sensorData;
	}
	
	public Cursor getAllTimeStampsOfSensor(String sensorAddress) {
		String whereClause = SQLiteHelper.KEY_ADDRESS + " = ?";
		String[] whereArgs = new String[] {sensorAddress};
		return database.query(SQLiteHelper.TABLE_SENSOR_DETAILS,
				databaseDetailRow,
				whereClause,
				whereArgs,
				null, null, SQLiteHelper.KEY_TIMESTAMP+" DESC");		
	}
	
	public String getLastTimeStampOfSensor(String sensorAddress) {
		String whereClause = SQLiteHelper.KEY_ADDRESS + " = ?";
		String[] whereArgs = new String[] {sensorAddress};
		Cursor cursor = database.query(SQLiteHelper.TABLE_SENSOR_DETAILS,
				databaseDetailRow,
				whereClause,
				whereArgs,
				null, null, SQLiteHelper.KEY_TIMESTAMP+" DESC");
		cursor.moveToFirst();
		String timeStamp = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_TIMESTAMP));
		cursor.close();
		return timeStamp;
	}
	
	public Cursor getSensorsCursor() {
		return database.query(SQLiteHelper.TABLE_SENSORS,
				databaseRow,
				null,
				null,
				null,
				null,
				SQLiteHelper.KEY_NAME+" DESC");		
	}
	
	private TemperatureData cursorToTemperatureData(Cursor cursor) {
		TemperatureData sensor = new TemperatureData();
		if (cursor.getCount() != 0) {
			//Log.e(TAG,"cursor has elemnets: "+cursor.getCount());
			cursor.moveToFirst();
			sensor.setSensorAddress(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ADDRESS)));
			sensor.setSensorName(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_NAME)));
			sensor.setTemperatureValue(cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.KEY_TEMP_VALUE)));
			sensor.setBatteryValue(cursor.getInt(cursor.getColumnIndex(SQLiteHelper.KEY_BATTERY_VALUE)));
			sensor.setTimeStamp(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_TIMESTAMP)));
		}
		cursor.close();
		return sensor;
	}
	
	
}
