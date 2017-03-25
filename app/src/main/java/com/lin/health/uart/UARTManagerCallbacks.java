package com.lin.health.uart;

import android.bluetooth.BluetoothDevice;

import com.lin.health.profile.BleManagerCallbacks;


public interface UARTManagerCallbacks extends BleManagerCallbacks {

	void onDataReceived(final BluetoothDevice device, final String data);

	void onDataSent(final BluetoothDevice device, final String data);
}
