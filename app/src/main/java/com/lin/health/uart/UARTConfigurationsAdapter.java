package com.lin.health.uart;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.lin.health.R;


public class UARTConfigurationsAdapter extends CursorAdapter {
	final Context mContext;
	final ActionListener mListener;

	public interface ActionListener {
		public void onNewConfigurationClick();
		public void onImportClick();
	}

	public UARTConfigurationsAdapter(final Context context, final ActionListener listener, final Cursor c) {
		super(context, c, 0);
		mContext = context;
		mListener = listener;
	}

	@Override
	public int getCount() {
		return super.getCount() + 1; // One for buttons at the top
	}

	@Override
	public boolean isEmpty() {
		return super.getCount() == 0;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public long getItemId(final int position) {
		if (position > 0)
			return super.getItemId(position - 1);
		return 0;
	}

	public int getItemPosition(final long id) {
		final Cursor cursor = getCursor();
		if (cursor == null)
			return 1;

		if (cursor.moveToFirst())
			do {
				if (cursor.getLong(0 /* _ID */) == id)
					return cursor.getPosition() + 1;
			} while (cursor.moveToNext());
		return 1; // should never happen
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		if (position == 0) {
			// This empty view should never be visible. Only positions 1+ are valid. Position 0 is reserved for action buttons.
			// It is only created temporally when activity is created.
			return LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
		}
		return super.getView(position - 1, convertView, parent);
	}

	@Override
	public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
		if (position == 0) {
			return newToolbarView(mContext, parent);
		}
		if (convertView instanceof ViewGroup)
			return super.getDropDownView(position - 1, null, parent);
		return super.getDropDownView(position - 1, convertView, parent);
	}

	@Override
	public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
		return LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
	}

	@Override
	public View newDropDownView(final Context context, final Cursor cursor, final ViewGroup parent) {
		return LayoutInflater.from(mContext).inflate(R.layout.feature_uart_dropdown_item, parent, false);
	}

	public View newToolbarView(final Context context, final ViewGroup parent) {
		final View view = LayoutInflater.from(context).inflate(R.layout.feature_uart_dropdown_title, parent, false);
		view.findViewById(R.id.action_add).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				mListener.onNewConfigurationClick();
			}
		});
		view.findViewById(R.id.action_import).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				mListener.onImportClick();
			}
		});
		return view;
	}

	@Override
	public void bindView(final View view, final Context context, final Cursor cursor) {
		final String name = cursor.getString(1 /* NAME */);
		((TextView) view).setText(name);
	}
}
