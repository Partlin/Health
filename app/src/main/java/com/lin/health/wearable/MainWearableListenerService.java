package com.lin.health.wearable;

import android.content.Intent;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.lin.health.common.Constants;
import com.lin.health.uart.UARTService;


/**
 * The main listener for messages from Wearable devices. There may be only one such service per application so it has to handle messages from all profiles.
 */
public class MainWearableListenerService extends WearableListenerService {

	@Override
	public void onMessageReceived(final MessageEvent messageEvent) {
		switch (messageEvent.getPath()) {
			case Constants.ACTION_DISCONNECT: {
				// A disconnect message was sent. The information which profile should be disconnected is in the data.
				final String profile = new String(messageEvent.getData());

				switch (profile) {
					// Currently only UART profile has Wear support
					case Constants.UART.PROFILE: {
						final Intent disconnectIntent = new Intent(UARTService.ACTION_DISCONNECT);
						disconnectIntent.putExtra(UARTService.EXTRA_SOURCE, UARTService.SOURCE_WEARABLE);
						sendBroadcast(disconnectIntent);
						break;
					}
				}
				break;
			}
			case Constants.UART.COMMAND: {
				final String command = new String(messageEvent.getData());

				final Intent intent = new Intent(UARTService.ACTION_SEND);
				intent.putExtra(UARTService.EXTRA_SOURCE, UARTService.SOURCE_WEARABLE);
				intent.putExtra(Intent.EXTRA_TEXT, command);
				sendBroadcast(intent);
			}
			default:
				super.onMessageReceived(messageEvent);
				break;
		}
	}
}
