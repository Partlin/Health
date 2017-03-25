package com.lin.health.uart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lin.health.R;
import com.lin.health.uart.domain.UartConfiguration;
import com.lin.health.uart.domain.Command;



public class UARTButtonAdapter extends BaseAdapter {
	private UartConfiguration mConfiguration;
	private boolean mEditMode;

	public UARTButtonAdapter(final UartConfiguration configuration) {
		mConfiguration = configuration;
	}

	public void setEditMode(final boolean editMode) {
		mEditMode = editMode;
		notifyDataSetChanged();
	}

	public void setConfiguration(final UartConfiguration configuration) {
		mConfiguration = configuration;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mConfiguration != null ? mConfiguration.getCommands().length : 0;
	}

	@Override
	public Object getItem(final int position) {
		return mConfiguration.getCommands()[position];
	}

	@Override
	public long getItemId(final int position) {
		return position;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		final Command command = (Command) getItem(position);
		return mEditMode || (command != null && command.isActive());
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			view = inflater.inflate(R.layout.feature_uart_button, parent, false);
		}
		view.setEnabled(isEnabled(position));
		view.setActivated(mEditMode);

		// Update image
		final Command command = (Command) getItem(position);
		final ImageView image = (ImageView) view;
		final boolean active = command != null && command.isActive();
		if (active) {
			final int icon = command.getIconIndex();
			image.setImageResource(R.drawable.uart_button);
			image.setImageLevel(icon);
		} else
			image.setImageDrawable(null);

		return view;
	}
}
