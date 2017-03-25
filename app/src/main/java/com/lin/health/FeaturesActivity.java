package com.lin.health;

import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.lin.health.adapter.AppAdapter;

public class FeaturesActivity extends AppCompatActivity {

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_features);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);

		 //ensure that Bluetooth exists
//		if (!ensureBLEExists())
//			finish();

		final DrawerLayout drawer = mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		// Set the drawer toggle as the DrawerListener
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerSlide(final View drawerView, final float slideOffset) {
                // Disable the Hamburger icon animation
                super.onDrawerSlide(drawerView, 0);
            }
        };
		drawer.addDrawerListener(mDrawerToggle);

		// setup plug-ins in the drawer
		//setupPluginsInDrawer((ViewGroup) drawer.findViewById(R.id.plugin_container));

		// configure the app grid
		final GridView grid = (GridView) findViewById(R.id.grid);
		grid.setAdapter(new AppAdapter(this));
		grid.setEmptyView(findViewById(android.R.id.empty));

		getSupportActionBar().setDisplayHomeAsUpEnabled(false);


	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.help, menu);
		return true;
	}

	@Override
	protected void onPostCreate(final Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(final Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		switch (item.getItemId()) {
		case R.id.action_about:
			final AppHelpFragment fragment = AppHelpFragment.getInstance(R.string.about_text, true);
			fragment.show(getSupportFragmentManager(), null);
			break;
		}
		return true;
	}


	private boolean ensureBLEExists() {
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, R.string.no_ble, Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
}
