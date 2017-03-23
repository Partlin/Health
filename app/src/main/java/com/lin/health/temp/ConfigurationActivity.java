package com.lin.health.temp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.lin.health.R;

public class ConfigurationActivity extends Activity {
	private TemperatureData sensor;
	private final String TAG = "ConfigurationActivity";
	EditText sensorName;
	TextView sensorBattery;
	private TemperatureDbAdapter dbAdapter;
	TemperatureListDetailAdapter listDetailsAdaptor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configuration);
        setDbAdapater();       
        setGUI();        
	}
	
	private void setDbAdapater() {
		dbAdapter = TemperatureDbAdapter.getTemperatureDbAdapter();
		dbAdapter.open(getApplicationContext()); 
	}
	
	private void setGUI() {
		Bundle bundle = getIntent().getExtras();
		String SENSOR_VALUE_KEY = "com.lin.health.TemperatureSensorActivity.SENSOR_VALUE_KEY";
		sensor = bundle.getParcelable(SENSOR_VALUE_KEY);
        sensorName = (EditText) findViewById(R.id.edittextConfigurationSensorName);
        sensorBattery = (TextView) findViewById(R.id.textViewBatteryLevel);
        sensorName.setText(sensor.getSensorName());
        sensorBattery.setText(sensor.getBatteryValue()+"%");
        setListView();
	}
	
	private void setListView() {
		Log.e(TAG,"setListView");
		ListView listview = (ListView) findViewById(R.id.listviewTempSensorDetails);
		listDetailsAdaptor = new TemperatureListDetailAdapter(this, getSensorDetailsFromDb());
		listview.setAdapter(listDetailsAdaptor);		
	}
	
	private Cursor getSensorDetailsFromDb() {
		return dbAdapter.getAllTimeStampsOfSensor(sensor.getSensorAddress());				
	}
	
	@Override
	protected void onStart() {
		Log.e(TAG,"onStart()");
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		Log.e(TAG,"onResume()");
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		Log.e(TAG,"onStop()");
		super.onStop();			
	}
	
	@Override
	protected void onDestroy() {
		Log.e(TAG,"onDestroy()");
		super.onDestroy();		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.configuration, menu);
		return true;
	}	
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            	Log.e(TAG,"Menuitem Back key pressed");
            	updateSensorName();
                //NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menuitemDelete:
            	Log.e(TAG,"Menuitem Delete key is pressed");
            	showMessageBox("Remove Temperature Sensor", "Are you sure?");            	
        }
        return super.onOptionsItemSelected(item);
    }
	
	private void showMessageBox(String title, String message) {
		AlertDialog.Builder messageBox = new AlertDialog.Builder(this);
		messageBox
		.setTitle(title)
		.setMessage(message)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.e(TAG,"You pressed Yes!");
				dbAdapter.deleteSensor(sensor);
            	dbAdapter.deleteTimeStampsOfSensor(sensor);
				navigateUpTo(getParentActivityIntent());
			}
		})
		.setNegativeButton("No", null)
		.show();
	}
	

	
	private void updateSensorName() {
		Log.e(TAG,"updating sensor name");
		String name = sensorName.getText().toString();
		if (name.length() != 0) {
			sensor.setSensorName(name);
		}		
		dbAdapter.updateSensorName(sensor);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Log.e(TAG,"Back key pressed");
			updateSensorName();
			Intent intent = new Intent(this, TemperatureSensorsActivity.class);
			finish();
			startActivity(intent);
		}		
		return true;		
	}	
}
