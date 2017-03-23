package com.lin.health.temp;


import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.lin.health.R;


public class TemperatureSensorsActivity extends Activity implements TemperatureManagerCallbacks {
	private final String TAG = "TemperatureSensorsActivity";
	private BluetoothAdapter mBluetoothAdapter;
	private TemperatureManager mTemperatureManager;
	static final int REQUEST_ENABLE_BT = 1;
	private TemperatureDbAdapter dbAdapter;
	TemperatureListAdapter listAdaptor;
	private Handler mHandler = new Handler();
	private final String SENSOR_VALUE_KEY = "com.lin.health.TemperatureSensorActivity.SENSOR_VALUE_KEY";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_temperature_sensors);
		setActionBar();
		setBluetoothAdapter();
		isBLESupported();
		isBLEEnabled();			
	}
	private void setActionBar() {
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.rgb(0, 156, 222)));
		bar.setCustomView(R.layout.actionbar_bluetooth_logo);
		bar.setDisplayShowTitleEnabled(false);
		bar.setDisplayShowCustomEnabled(true);
		bar.setDisplayUseLogoEnabled(false);
		bar.setDisplayShowHomeEnabled(false);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Log.e(TAG,"onStart()");
		if (mTemperatureManager != null) {
			updateSensorsListView();		
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.e(TAG,"onResume()");
	}
	
	@Override
	protected void onStop() {
		super.onStop();	
		Log.e(TAG,"onStop()");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.e(TAG,"onDestroy()");
		if (mTemperatureManager != null) {
			stopUpdatingListview();
			mTemperatureManager.Destroy();
		}	
	}
	
	@Override
	public void onContentChanged() {
		super.onContentChanged();
		View emptyList = findViewById(R.id.empty);
		ListView list = (ListView) findViewById(R.id.listviewTempSensors);
		list.setEmptyView(emptyList);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.temperature_sensors, menu);
		return true;
	}
	
	private void initializeTemperatureManager() {
		mTemperatureManager = TemperatureManager.getTemperatureManager();
		mTemperatureManager.setBluetoothAdapter(mBluetoothAdapter);
		mTemperatureManager.setGattCallbacks(this);	
		mTemperatureManager.startScan();
		
		dbAdapter = TemperatureDbAdapter.getTemperatureDbAdapter();
		dbAdapter.open(getApplicationContext());		
		if (mTemperatureManager != null) {
			setListView();			
		}	
	}
	
	private void setListView() {
		ListView listview = (ListView) findViewById(R.id.listviewTempSensors);
		listAdaptor = new TemperatureListAdapter(this, getSensorsFromDb());
		listview.setAdapter(listAdaptor);
		startUpdatingListview();
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
				Intent intent = new Intent(view.getContext(),ConfigurationActivity.class);
        		intent.putExtra(SENSOR_VALUE_KEY, dbAdapter.getSensorById(id));				
        		startActivity(intent);
			}
		}); 
	}	
	
	private Cursor getSensorsFromDb() {
		return dbAdapter.getSensorsCursor();		
	}
	
	private void setBluetoothAdapter() {
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
	}
	
	private void isBLESupported() {
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			showToast("Device don't have BLE support");			
			finish();
		}
		/*else {
			showToast("Device support BLE!");
		}*/
	}
	
	private void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
	
	private void isBLEEnabled() {		
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}
		else {
			initializeTemperatureManager();
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_ENABLE_BT:            
            if (resultCode == Activity.RESULT_OK) {
               showToast("Bluetooth has turned on ");   
               initializeTemperatureManager();
            } else if (resultCode == Activity.RESULT_CANCELED){
                showToast("Please enable Bluetooth ");
                finish();
            } else {                                
                showToast("Problem in BT Turning ON ");
                finish();
            }
            break;
        }
    }
	
	private void updateSensorsListView() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {				
				listAdaptor.changeCursor(dbAdapter.getSensorsCursor());
			}
		});
	}
	
	Runnable mRepeatTask = new Runnable() {
		public void run() {			
			updateSensorsListView();
			int mInterval = 5000;
			mHandler.postDelayed(mRepeatTask, mInterval);
		}    	
    };    
    
    void startUpdatingListview() {
    	mRepeatTask.run();
    }
    
    void stopUpdatingListview() {
    	mHandler.removeCallbacks(mRepeatTask);
    }	

    @Override
	public void onDevicesScaned() {
		Log.e(TAG,"onDevicesScaned()");
		updateSensorsListView();		
	}

}
