package com.lin.health.temp;

//import android.util.Log;

public class TemperatureParser {
	private final String TAG = "TemperatureParser";
	public static final int MAX_VALID_TEMPERATURE_VALUE = 99;
	public static final int MIN_VALID_TEMPERATURE_VALUE = -99;
	private final int FLAG = 1;
	private final int LOCAL_NAME = 9;
	private final int SERVICE_DATA = 22; // 0x16
	private final String BATTERY_SERVICE_UUID = "2415"; // 0x180F
	private final String TEMPERATURE_SERVICE_UUID = "249"; // 0x1809
	private final int GET_BIT24 = 0x00400000;
	private final int HIDE_MSB_8BITS_OUT_OF_32BITS = 0x00FFFFFF;
	private final int HIDE_MSB_8BITS_OUT_OF_16BITS = 0x00FF;
	private final int SHIFT_LEFT_8BITS = 8;
	private final int SHIFT_LEFT_16BITS = 16;
	
	private int batteryValue = 0;
	private int packetLength = 0;
	private double temperatureValue = 0;
	private String sensorName = "";
	private boolean isValidTempSensor = false;
	
	private static TemperatureParser mParserInstance;
	public static synchronized TemperatureParser getTemperatureParser() {
		if (mParserInstance == null) {
			mParserInstance = new TemperatureParser();
		}
		return mParserInstance;
	}
	
	public int getBatteryValue() {
		return batteryValue;
	}
	
	public int getPacketLength() {
		return packetLength;
	}
	
	public double getTemperatureValue() {
		return temperatureValue;
	}
	
	public String getSensorName() {
		return sensorName;
	}
	
	public boolean isValidTempSensor() {
		return isValidTempSensor;
	}

	public void decodeTempAdvData(byte[] data) throws Exception {
		isValidTempSensor = false;
		if (data != null) {			
			int fieldLength, fieldName;			
			packetLength = data.length;
			for (int index=0; index<packetLength; index++) {
				fieldLength = data[index];
				if (fieldLength == 0) {
					//Log.e(TAG,"index: "+index+" No more data exist in Temp Adv packet");
					return;
				}
				fieldName = data[++index];
				
				if (fieldName == FLAG) {					
					//Log.e(TAG,"index: "+index+ " Flag exist");
					index += fieldLength -1;
				}
				else if (fieldName == LOCAL_NAME) {						
					//Log.e(TAG,"index: "+index+ " Local Name exist");
					decodeLocalName(data, index+1, fieldLength-1);					
					index += fieldLength -1;
				}
				else if (fieldName == SERVICE_DATA) {					
					//Log.e(TAG,"index: "+index+ " Service Data exist");
					decodeServiceData(data, index+1, fieldLength-1);
					index += fieldLength -1;
				}
				else {
					// Other Field Name						
					index += fieldLength -1;
				}
			}
		}
		else {
			//Log.e(TAG,"data is null!");
			return;
		}		
	}
	
	private void decodeLocalName(byte[] data, int startPosition, int nameLength) throws Exception {
		sensorName = "";
		for (int nameIndex = startPosition; nameIndex < startPosition+nameLength; nameIndex++) {
			//Log.e(TAG,"Name: "+data[nameIndex]+ " = "+ (char)data[nameIndex]);
			sensorName += (char)data[nameIndex];
		}
		//Log.e(TAG,"Name of sensor: "+getSensorName());		
	}
	
	private void decodeServiceData(byte[] data, int startPosition, int serviceDataLength) throws Exception {
		String ServiceUUID = Byte.toString(data[startPosition+1]) + Byte.toString(data[startPosition]);
		if (ServiceUUID.equals(BATTERY_SERVICE_UUID)) {
			//Log.e(TAG,"Battery service exist!");
			batteryValue = data[startPosition+2];
			//Log.e(TAG,"Battery Value: "+batteryValue);
		}
		else if (ServiceUUID.equals(TEMPERATURE_SERVICE_UUID)) {
			//Log.e(TAG,"Temperature service exist!");
			isValidTempSensor = true;
			decodeTemperature(data, startPosition+2);
		}
		else {
			//Log.e(TAG,"Other than Battery and Temp service");
		}		
	}
	
	private void decodeTemperature(byte[] data, int startPosition) throws Exception {
		byte exponential = (byte)data[startPosition + 3];		
		short firstOctet = convertNegativeByteToPositiveShort(data[startPosition]);
		short secondOctet = convertNegativeByteToPositiveShort(data[startPosition+1]);
		short thirdOctet = convertNegativeByteToPositiveShort(data[startPosition+2]);
		
		int mantissa = ((int) ((thirdOctet << SHIFT_LEFT_16BITS)
		                | (secondOctet << SHIFT_LEFT_8BITS)
		                | (firstOctet)))		               
		                & HIDE_MSB_8BITS_OUT_OF_32BITS;		
		
		mantissa = getTwosComplimentOfNegativeMantissa(mantissa);		
		temperatureValue = (double)(mantissa* Math.pow(10, exponential));
		int temp = (int)temperatureValue;
		if (temp > MAX_VALID_TEMPERATURE_VALUE || temp < MIN_VALID_TEMPERATURE_VALUE) {
			throw new Exception("Invalid temperature value");
		}		
	}
	
	private short convertNegativeByteToPositiveShort(byte octet) {
		if (octet < 0) {
			return (short) (octet & HIDE_MSB_8BITS_OUT_OF_16BITS) ;
		}
		else {
			return (short) octet;
		}
	}
	
	private int getTwosComplimentOfNegativeMantissa(int mantissa) {
		if ((mantissa & GET_BIT24) != 0) {		
			return ((((~mantissa) & HIDE_MSB_8BITS_OUT_OF_32BITS) +1) * (-1));
		}
		else {
			return mantissa;
		}
	}
}
