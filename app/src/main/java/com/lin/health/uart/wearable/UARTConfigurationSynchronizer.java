package com.lin.health.uart.wearable;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.lin.health.common.Constants;
import com.lin.health.uart.domain.UartConfiguration;
import com.lin.health.uart.domain.Command;

import java.util.ArrayList;



public class UARTConfigurationSynchronizer {
	private static final String WEAR_URI_PREFIX = "wear:"; // no / at the end as the path already has it

	private static UARTConfigurationSynchronizer mInstance;
	private GoogleApiClient mGoogleApiClient;

	/**
	 * Initializes the synchronizer.
	 * @param context the activity context
	 * @param listener the connection callbacks listener
	 */
	public static UARTConfigurationSynchronizer from(final Context context, final GoogleApiClient.ConnectionCallbacks listener) {
		if (mInstance == null)
			mInstance = new UARTConfigurationSynchronizer();

		mInstance.init(context, listener);
		return mInstance;
	}

	private UARTConfigurationSynchronizer() {
		// private constructor
	}

	private void init(final Context context, final GoogleApiClient.ConnectionCallbacks listener) {
		if (mGoogleApiClient != null)
			return;

		mGoogleApiClient = new GoogleApiClient.Builder(context)
				.addApi(Wearable.API)
				.addConnectionCallbacks(listener)
				.build();
		mGoogleApiClient.connect();
	}

	/**
	 * Closes the synchronizer.
	 */
	public void close() {
		if (mGoogleApiClient != null)
			mGoogleApiClient.disconnect();
		mGoogleApiClient = null;
	}

	/**
	 * Synchronizes the UART configurations between handheld and wearables.
	 * Call this when configuration has been created or altered.
	 * @return pending result
	 */
	public PendingResult<DataApi.DataItemResult> onConfigurationAddedOrEdited(final long id, final UartConfiguration configuration) {
		if (mGoogleApiClient == null || !mGoogleApiClient.isConnected())
			return null;

		final PutDataMapRequest mapRequest = PutDataMapRequest.create(Constants.UART.CONFIGURATIONS + "/" + id);
		final DataMap map = mapRequest.getDataMap();
		map.putString(Constants.UART.Configuration.NAME, configuration.getName());
		final ArrayList<DataMap> commands = new ArrayList<>(UartConfiguration.COMMANDS_COUNT);
		for (Command command : configuration.getCommands()) {
			if (command != null && command.isActive()) {
				final DataMap item = new DataMap();
				item.putInt(Constants.UART.Configuration.Command.ICON_ID, command.getIconIndex());
				item.putString(Constants.UART.Configuration.Command.MESSAGE, command.getCommand());
				item.putInt(Constants.UART.Configuration.Command.EOL, command.getEolIndex());
				commands.add(item);
			}
		}
		map.putDataMapArrayList(Constants.UART.Configuration.COMMANDS, commands);
		final PutDataRequest request = mapRequest.asPutDataRequest();
		return Wearable.DataApi.putDataItem(mGoogleApiClient, request);
	}

	/**
	 * Synchronizes the UART configurations between handheld and wearables.
	 * Call this when configuration has been deleted.
	 * @return pending result
	 */
	public PendingResult<DataApi.DeleteDataItemsResult> onConfigurationDeleted(final long id) {
		if (mGoogleApiClient == null || !mGoogleApiClient.isConnected())
			return null;
		return Wearable.DataApi.deleteDataItems(mGoogleApiClient, id2Uri(id));
	}

	/**
	 * Creates URI without nodeId.
	 * @param id the configuration id in the database
	 * @return Uri that may be used to delete the associated DataMap.
	 */
	private Uri id2Uri(final long id) {
		return Uri.parse(WEAR_URI_PREFIX + Constants.UART.CONFIGURATIONS + "/" + id);
	}
}
