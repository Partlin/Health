package com.lin.health.temp;

import android.os.Parcel;
import android.os.Parcelable;

public class TemperatureData implements Parcelable {
	private String mSensorAddress;
	private String mSensorName;
	private double mTemperatureValue;
	private int mBatteryValue;	
	private String mTimeStamp;
	
	public TemperatureData() {
		
	}
	
	public TemperatureData(String address, String name, double tempValue, int batteryValue, String timeStamp) {
		mSensorAddress = address;
		mSensorName = name;
		mTemperatureValue = tempValue;
		mBatteryValue = batteryValue;
		mTimeStamp = timeStamp;
	}
	
	public void setSensorAddress(String address) {
		mSensorAddress = address;
	}
	public void setSensorName(String name) {
		mSensorName = name;
	}
	public void setTemperatureValue(double tempValue) {
		mTemperatureValue = tempValue;
	}
	public void setBatteryValue(int batteryValue) {
		mBatteryValue = batteryValue;
	}
	public void setTimeStamp(String timeStamp) {
		mTimeStamp = timeStamp;
	}
	
	public String getSensorAddress() {
		return mSensorAddress;
	}
	public String getSensorName() {
		return mSensorName;
	}
	public double getTemperatureValue() {
		return mTemperatureValue;
	}
	public int getBatteryValue() {
		return mBatteryValue;
	}
	public String getTimeStamp() {
		return mTimeStamp;
	}

/********** Parcelable Implementation **********/
	
	public TemperatureData(Parcel in) {
		readFromPracel(in);
	}

	private void readFromPracel(Parcel in) {
		mSensorAddress = in.readString();
		mSensorName = in.readString();
		mTemperatureValue = in.readDouble();
		mBatteryValue = in.readInt();
		mTimeStamp = in.readString();		
	}

	public int describeContents() {		
		return 0;
	}	

	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeString(mSensorAddress);
		dest.writeString(mSensorName);
		dest.writeDouble(mTemperatureValue);
		dest.writeInt(mBatteryValue);
		dest.writeString(mTimeStamp);		
	}
	
	public static final Parcelable.Creator<TemperatureData> CREATOR =
	    	new Parcelable.Creator<TemperatureData>() {
	            public TemperatureData createFromParcel(Parcel in) {
	                return new TemperatureData(in);
	            }
	 
	            public TemperatureData[] newArray(int size) {
	                return new TemperatureData[size];
	            }
	        };
	
}
