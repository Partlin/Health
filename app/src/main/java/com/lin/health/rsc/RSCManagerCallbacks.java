package com.lin.health.rsc;

import android.bluetooth.BluetoothDevice;

import com.lin.health.profile.BleManagerCallbacks;


public interface RSCManagerCallbacks extends BleManagerCallbacks {
	int NOT_AVAILABLE = -1;
	int ACTIVITY_WALKING = 0;
	int ACTIVITY_RUNNING = 1;

	void onMeasurementReceived(final BluetoothDevice device, float speed, int cadence, float distance, float strideLen, int activity);
}
