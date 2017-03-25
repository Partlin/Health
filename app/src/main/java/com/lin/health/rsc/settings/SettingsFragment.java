package com.lin.health.rsc.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.lin.health.R;


public class SettingsFragment extends PreferenceFragment {
	public static final String SETTINGS_UNIT = "settings_rsc_unit";
	public static final int SETTINGS_UNIT_M_S = 0; // [m/s]
	public static final int SETTINGS_UNIT_KM_H = 1; // [m/s]
	public static final int SETTINGS_UNIT_MPH = 2; // [m/s]
	public static final int SETTINGS_UNIT_DEFAULT = SETTINGS_UNIT_KM_H;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.settings_rsc);
	}
}
