package com.lin.health.temp;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class TemperatureUtilities {
	public final static int UPDATE_MESSAGE_DURATION_IN_SECONDS = 10;
	private final static String TAG = "TemperatureUtilities";
	public final static String SENSOR_UPDATED = "更新";
	public final static String SENSOR_NOT_UPDATED = "找不到设备";
	
	public static String setSensorUpdateMessage(Calendar oldDate) {
		Date date = new Date();
		Calendar newDate = new GregorianCalendar();
		newDate.setTime(date);		
		long diff = newDate.getTimeInMillis() - oldDate.getTimeInMillis();		
		long diffSeconds = TimeUnit.MILLISECONDS.toSeconds(diff);
		if (diffSeconds > UPDATE_MESSAGE_DURATION_IN_SECONDS) {
			return SENSOR_NOT_UPDATED;
		} 
		return SENSOR_UPDATED;			
	}
	
	public static String getDayOfWeek(int day) {
		switch(day) {
		case Calendar.SUNDAY:
			return "Sun";
		case Calendar.MONDAY:
			return "Mon";
		case Calendar.TUESDAY:
			return "Tue";
		case Calendar.WEDNESDAY:
			return "Wed";
		case Calendar.THURSDAY:
			return "Thu";
		case Calendar.FRIDAY:
			return "Fri";
		case Calendar.SATURDAY:
			return "Sat";
		default:
			return "N/A";
		}
	}
	
	public static String getMonth(int month) {
		switch(month) {
		case Calendar.JANUARY:
			return "Jan";
		case Calendar.FEBRUARY:
			return "Feb";
		case Calendar.MARCH:
			return "Mar";
		case Calendar.APRIL:
			return "Apr";
		case Calendar.MAY:
			return "May";
		case Calendar.JUNE:
			return "Jun";
		case Calendar.JULY:
			return "Jul";
		case Calendar.AUGUST:
			return "Aug";
		case Calendar.SEPTEMBER:
			return "Sep";
		case Calendar.OCTOBER:
			return "Oct";
		case Calendar.NOVEMBER:
			return "Nov";
		case Calendar.DECEMBER:
			return "Dec";
		default:
			return "N/A";		
		}
	}	
}
