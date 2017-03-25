

package com.lin.health.rsc;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import com.lin.health.parser.RSCMeasurementParser;
import com.lin.health.profile.BleManager;

import java.util.Deque;
import java.util.LinkedList;
import java.util.UUID;

import no.nordicsemi.android.log.Logger;


public class RSCManager extends BleManager<RSCManagerCallbacks> {
	private static final byte INSTANTANEOUS_STRIDE_LENGTH_PRESENT = 0x01; // 1 bit
	private static final byte TOTAL_DISTANCE_PRESENT = 0x02; // 1 bit
	private static final byte WALKING_OR_RUNNING_STATUS_BITS = 0x04; // 1 bit

	/** Running Speed and Cadence Measurement service UUID */
	public final static UUID RUNNING_SPEED_AND_CADENCE_SERVICE_UUID = UUID.fromString("00001814-0000-1000-8000-00805f9b34fb");
	/** Running Speed and Cadence Measurement characteristic UUID */
	private static final UUID RSC_MEASUREMENT_CHARACTERISTIC_UUID = UUID.fromString("00002A53-0000-1000-8000-00805f9b34fb");

	private BluetoothGattCharacteristic mRSCMeasurementCharacteristic;

	public RSCManager(final Context context) {
		super(context);
	}

	@Override
	protected BleManagerGattCallback getGattCallback() {
		return mGattCallback;
	}

	/**
	 * BluetoothGatt callbacks for connection/disconnection, service discovery, receiving indication, etc
	 */
	private final BleManagerGattCallback mGattCallback = new BleManagerGattCallback() {

		@Override
		protected Deque<Request> initGatt(final BluetoothGatt gatt) {
			final LinkedList<Request> requests = new LinkedList<>();
			requests.add(Request.newEnableNotificationsRequest(mRSCMeasurementCharacteristic));
			return requests;
		}

		@Override
		public boolean isRequiredServiceSupported(final BluetoothGatt gatt) {
			final BluetoothGattService service = gatt.getService(RUNNING_SPEED_AND_CADENCE_SERVICE_UUID);
			if (service != null) {
				mRSCMeasurementCharacteristic = service.getCharacteristic(RSC_MEASUREMENT_CHARACTERISTIC_UUID);
			}
			return mRSCMeasurementCharacteristic != null;
		}

		@Override
		protected void onDeviceDisconnected() {
			mRSCMeasurementCharacteristic = null;
		}

		@Override
		public void onCharacteristicNotified(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
			Logger.a(mLogSession, "\"" + RSCMeasurementParser.parse(characteristic) + "\" received");

			// Decode the new data
			int offset = 0;
			final int flags = characteristic.getValue()[offset]; // 1 byte
			offset += 1;

			final boolean islmPresent = (flags & INSTANTANEOUS_STRIDE_LENGTH_PRESENT) > 0;
			final boolean tdPreset = (flags & TOTAL_DISTANCE_PRESENT) > 0;
			final boolean running = (flags & WALKING_OR_RUNNING_STATUS_BITS) > 0;

			final float instantaneousSpeed = (float) characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset) / 256.0f; // 1/256 m/s in [m/s]
			offset += 2;

			final int instantaneousCadence = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset); // [SPM]
			offset += 1;

			float instantaneousStrideLength = RSCManagerCallbacks.NOT_AVAILABLE;
			if (islmPresent) {
				instantaneousStrideLength = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset); // [cm]
				offset += 2;
			}

			float totalDistance = RSCManagerCallbacks.NOT_AVAILABLE;
			if (tdPreset) {
				totalDistance = (float) characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, offset) / 10.0f; // 1/10 m in [m]
				//offset += 4;
			}

			// Notify listener about the new measurement
			mCallbacks.onMeasurementReceived(gatt.getDevice(), instantaneousSpeed, instantaneousCadence, totalDistance, instantaneousStrideLength,
					running ? RSCManagerCallbacks.ACTIVITY_RUNNING : RSCManagerCallbacks.ACTIVITY_WALKING);
		}
	};
}
