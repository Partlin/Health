package com.lin.health.parser;

import android.bluetooth.BluetoothGattCharacteristic;

public class AlertLevelParser {
	/**
	 * Parses the alert level.
	 * 
	 * @param characteristic
	 * @return alert level in human readable format
	 */
	public static String parse(final BluetoothGattCharacteristic characteristic) {
		final int value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);

		switch (value) {
		case 0:
			return "No Alert";
		case 1:
			return "Mild Alert";
		case 2:
			return "High Alert";
		default:
			return "Reserved value (" + value + ")";
		}
	}
}
