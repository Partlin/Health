package com.lin.health.temp;

import android.content.Context;
import android.database.Cursor;
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

public class TemperatureListDetailAdapter extends SimpleCursorAdapter {
	static final String[] dbColumns = new String[] {
			SQLiteHelper.KEY_TEMP_VALUE,
			SQLiteHelper.KEY_TIMESTAMP
	};
	static final int[] tableColumns = new int[] {
			R.id.textviewDetailTempValue,
			R.id.textviewDetailTimeStamp
	};	
	public TemperatureListDetailAdapter(Context context, Cursor cursor) {
		super(context, R.layout.sensor_details_row, cursor, dbColumns, tableColumns, 0);		
	}	
	
	@Override
	public void bindView(View row, Context context, Cursor cursor) {
		super.bindView(row, context, cursor);
		setTableRow(row, cursor);		
	}
	
	private void setTableRow(View rowView, Cursor cursor) {
		TextView tempValue = (TextView) rowView.findViewById(R.id.textviewDetailTempValue);
		TextView tempTimeStamp = (TextView) rowView.findViewById(R.id.textviewDetailTimeStamp);
		
		DecimalFormat formatedTemp = new DecimalFormat("#0.0");
		String TEMP_UNIT_CELSIUS = " ℃";
		String stringTemp = formatedTemp.format(cursor.getDouble
				(cursor.getColumnIndex(SQLiteHelper.KEY_TEMP_VALUE))) 
				+ TEMP_UNIT_CELSIUS;		
		tempValue.setText(stringTemp);		
		tempTimeStamp.setText(getFormattedDate(cursor));
	}
	
	private String getFormattedDate(Cursor cursor) {
		try {
			Date dateSaved = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").
					parse(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_TIMESTAMP)));
			Calendar formattedDate = new GregorianCalendar();
			formattedDate.setTime(dateSaved);
			String date = String.format("%s, %02d-%s-%02d at %02d:%02d ",
					TemperatureUtilities.getDayOfWeek(formattedDate.get(Calendar.DAY_OF_WEEK)),
					formattedDate.get(Calendar.DAY_OF_MONTH),
					TemperatureUtilities.getMonth(formattedDate.get(Calendar.MONTH)),
					formattedDate.get(Calendar.YEAR),
					formattedDate.get(Calendar.HOUR_OF_DAY),
					formattedDate.get(Calendar.MINUTE));
			return date;
			
		} catch (ParseException e) {
			Log.e("TemperatureListDetalAdapter","Date parse error: "+e.toString());
			return "N/A";
		}			
	}
}
