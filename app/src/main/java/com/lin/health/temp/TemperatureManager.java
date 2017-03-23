package com.lin.health.temp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;



public class TemperatureManager {
    private BluetoothAdapter mBluetoothAdapter;
    private TemperatureManagerCallbacks mCallbacks;
    private final static String TAG = "TemperatureManager";
    private final String SENSOR_NAME = "Temp";
    private boolean mIsScanning = false;
    private TemperatureDbAdapter dbAdapter = TemperatureDbAdapter.getTemperatureDbAdapter();
    TemperatureParser tempParser;
    public static final UUID TEMP_SERVICE_UUID = UUID.fromString("00001809-0000-1000-8000-00805f9b34fb");
    public static final int MINUTES_THRESHOLD = 4;

    private static TemperatureManager managerInstance = null;

    public static synchronized TemperatureManager getTemperatureManager() {
        Log.e(TAG,"getTemperatureManager()");
        if (managerInstance == null) {
            managerInstance = new TemperatureManager();
        }
        return managerInstance;
    }

    public void setBluetoothAdapter(BluetoothAdapter adapter) {
        mBluetoothAdapter = adapter;
    }

    public void setGattCallbacks(TemperatureManagerCallbacks callbacks) {
        mCallbacks = callbacks;
    }

    public boolean IsScaning() {
        return mIsScanning;
    }



    public void startScan() {
        mIsScanning = true;
        mBluetoothAdapter.startLeScan(mLEScanCallback);
    }

    private void stopScan() {
        mIsScanning = false;
        mBluetoothAdapter.stopLeScan(mLEScanCallback);
    }

    private BluetoothAdapter.LeScanCallback mLEScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.e(TAG,"Device scanned address: "+device.getAddress()+" name: "+device.getName());
            if (device != null) {
                BluetoothDevice mBluetoothDevice = device;
                Log.e(TAG,"scanRecord length: "+scanRecord.length+ " data: "+scanRecord);
                tempParser = TemperatureParser.getTemperatureParser();
                try {
                    tempParser.decodeTempAdvData(scanRecord);
                    if (tempParser.isValidTempSensor()) {
                        Log.e(TAG,"Temperature value: "+tempParser.getTemperatureValue());
                        String dateString = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
                        TemperatureData sensor = new TemperatureData(mBluetoothDevice.getAddress(),
                                SENSOR_NAME,
                                tempParser.getTemperatureValue(),
                                tempParser.getBatteryValue(),
                                dateString);
                        addTempSensor(sensor);
                        mCallbacks.onDevicesScaned();
                    }
                    else {
                        //Log.e(TAG,"Not Temperature sensor!");
                    }
                } catch (Exception e) {
                    Log.e(TAG,"Invalid data in Advertisement packet "+e.toString());
                }
            }
        }
    };

    public boolean addTempSensor(TemperatureData sensor) throws Exception {
        //Log.e(TAG,"addOrUpdateTempSensor()");
        if (dbAdapter.sensorAlreadyExist(sensor)) {
            return updateSensor(sensor);
        }
        try {
            dbAdapter.addSensor(sensor);
            dbAdapter.addSensorTimeStamp(sensor);
            return true;
        } catch (Exception e) {
            //Log.e(TAG,"Can't insert new sensor in database: " + e.toString());
        }
        return false;
    }

    public boolean updateSensor(TemperatureData sensor) throws Exception {
        //Log.e(TAG,"updateSensor()");
        String savedSensorTimeStamp = dbAdapter.getLastTimeStampOfSensor(sensor.getSensorAddress());
        if(savedSensorTimeStamp != null) {
            Date dateSaved = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(savedSensorTimeStamp);
            Date newDate = new Date();
            long diff = newDate.getTime() - dateSaved.getTime();
            long diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            Log.e(TAG,"Difference in minutes: "+diffMinutes);
            dbAdapter.updateSensor(sensor);
            if (diffMinutes > MINUTES_THRESHOLD) {
                Log.e(TAG,"updating sensor after MINUTES_THRESHOLD +1 minutes");
                dbAdapter.addSensorTimeStamp(sensor);
                return true;
            }
        }
        else {
            //Log.e(TAG,"sensor not found in db!");
        }
        return false;
    }

    public void Destroy() {
        if (mIsScanning) {
            stopScan();
        }
        managerInstance = null;
    }
}
