package com.lin.health.parser;

import android.bluetooth.BluetoothGattCharacteristic;

// TODO this method may be used for developing purposes to log the data from your device using the nRF Logger application.
public class TemplateParser {
	// TODO add some flags, if needed
	private static final byte HEART_RATE_VALUE_FORMAT = 0x01; // 1 bit

	/**
	 * This method converts the value of the characteristic to the String. The String is then logged in the nRF logger log session
	 * @param characteristic the characteristic to be parsed
	 * @return human readable value of the characteristic
	 */
	public static String parse(final BluetoothGattCharacteristic characteristic) {
		int offset = 0;
		final int flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset++);

		/*
		 * In the template we are using the HRM values as an example.
		 * false 	Heart Rate Value Format is set to UINT8. Units: beats per minute (bpm) 
		 * true 	Heart Rate Value Format is set to UINT16. Units: beats per minute (bpm)
		 */
		final boolean value16bit = (flags & HEART_RATE_VALUE_FORMAT) > 0;

		// heart rate value is 8 or 16 bit long
		int value = characteristic.getIntValue(value16bit ? BluetoothGattCharacteristic.FORMAT_UINT16 : BluetoothGattCharacteristic.FORMAT_UINT8, offset++); // bits per minute
		if (value16bit)
			offset++;

		// TODO parse more data

		final StringBuilder builder = new StringBuilder();
		builder.append("Template Measurement: ").append(value).append(" bpm");
		return builder.toString();
	}
}
