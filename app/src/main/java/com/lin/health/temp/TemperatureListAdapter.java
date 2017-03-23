package com.lin.health.temp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.lin.health.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class TemperatureListAdapter extends SimpleCursorAdapter {
	private final int MAX_NAME_CHARACTERS = 15;
	private final String EXTENSION_DOTS = "...";
	static final String[] dbColumns = new String[] {
			SQLiteHelper.KEY_NAME,
			SQLiteHelper.KEY_TEMP_VALUE
	};
	static final int[] tableColumns = new int[] {
			R.id.textviewRowSensorName,
			R.id.textviewRowTempValue
	};	
	public TemperatureListAdapter(Context context, Cursor cursor) {
		super(context, R.layout.list_row, cursor, dbColumns, tableColumns, 0);		
		Log.e("TemperatureListAdapter","TemperatureListAdapter()");
	}	
	
	@Override
	public void bindView(View row, Context context, Cursor cursor) {
		super.bindView(row, context, cursor);
		setTableRow(row, cursor);		
	}
	
	private void setTableRow(View rowView, Cursor cursor) {
		String updateMessage;
		TextView sensorName = (TextView) rowView.findViewById(R.id.textviewRowSensorName);
		TextView tempValue = (TextView) rowView.findViewById(R.id.textviewRowTempValue);
		TextView tempUpdated = (TextView) rowView.findViewById(R.id.textviewRowSensorUpdated);
		
		String name = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_NAME));
		if (name.length() > MAX_NAME_CHARACTERS) {
			name = name.substring(0, MAX_NAME_CHARACTERS) + EXTENSION_DOTS;
		}
		sensorName.setText(name);		
		DecimalFormat formatedTemp = new DecimalFormat("#0.0");
		String TEMP_UNIT_CELSIUS = " °C";
		String stringTemp = formatedTemp.format(cursor.getDouble
				(cursor.getColumnIndex(SQLiteHelper.KEY_TEMP_VALUE))) 
				+ TEMP_UNIT_CELSIUS;		
		tempValue.setText(stringTemp);		
				
		try {
			Date dateSaved = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").
					parse(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_TIMESTAMP)));
			Calendar oldDate = new GregorianCalendar();
			oldDate.setTime(dateSaved);
			updateMessage = TemperatureUtilities.setSensorUpdateMessage(oldDate);
			if (updateMessage.equals(TemperatureUtilities.SENSOR_UPDATED)) {
				tempUpdated.setTextColor(Color.BLACK);
			}
			else {
				tempUpdated.setTextColor(Color.RED);
			}
			tempUpdated.setText(updateMessage);			
		} catch (ParseException e) {
			Log.e("TemperatureListAdapter","Date parse error: "+e.toString());
			
		}		
	}
}
