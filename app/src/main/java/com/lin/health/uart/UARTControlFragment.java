package com.lin.health.uart;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.lin.health.R;
import com.lin.health.uart.domain.Command;
import com.lin.health.uart.domain.UartConfiguration;


public class UARTControlFragment extends Fragment implements GridView.OnItemClickListener, UARTActivity.ConfigurationListener {
	private final static String TAG = "UARTControlFragment";
	private final static String SIS_EDIT_MODE = "sis_edit_mode";

	private UartConfiguration mConfiguration;
	private UARTButtonAdapter mAdapter;
	private boolean mEditMode;

	@Override
	public void onAttach(final Context context) {
		super.onAttach(context);

		try {
			((UARTActivity)context).setConfigurationListener(this);
		} catch (final ClassCastException e) {
			Log.e(TAG, "The parent activity must implement EditModeListener");
		}
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			mEditMode = savedInstanceState.getBoolean(SIS_EDIT_MODE);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		((UARTActivity)getActivity()).setConfigurationListener(null);
	}

	@Override
	public void onSaveInstanceState(final Bundle outState) {
		outState.putBoolean(SIS_EDIT_MODE, mEditMode);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_feature_uart_control, container, false);

		final GridView grid = (GridView) view.findViewById(R.id.grid);
		grid.setAdapter(mAdapter = new UARTButtonAdapter(mConfiguration));
		grid.setOnItemClickListener(this);
		mAdapter.setEditMode(mEditMode);

		return view;
	}

	@Override
	public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
		if (mEditMode) {
			Command command = mConfiguration.getCommands()[position];
			if (command == null)
				mConfiguration.getCommands()[position] = command = new Command();
			final UARTEditDialog dialog = UARTEditDialog.getInstance(position, command);
			dialog.show(getChildFragmentManager(), null);
		} else {
			final Command command = (Command)mAdapter.getItem(position);
			final Command.Eol eol = command.getEol();
			String text = command.getCommand();
			if (text == null)
				text = "";
			switch (eol) {
				case CR_LF:
					text = text.replaceAll("\n", "\r\n");
					break;
				case CR:
					text = text.replaceAll("\n", "\r");
					break;
			}
			final UARTInterface uart = (UARTInterface) getActivity();
			uart.send(text);
		}
	}

	@Override
	public void onConfigurationModified() {
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onConfigurationChanged(final UartConfiguration configuration) {
		mConfiguration = configuration;
		mAdapter.setConfiguration(configuration);
	}

	@Override
	public void setEditMode(final boolean editMode) {
		mEditMode = editMode;
		mAdapter.setEditMode(mEditMode);
	}
}
