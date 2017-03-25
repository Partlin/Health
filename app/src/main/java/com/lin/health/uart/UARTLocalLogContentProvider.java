

package com.lin.health.uart;

import android.net.Uri;

import no.nordicsemi.android.log.localprovider.LocalLogContentProvider;

public class UARTLocalLogContentProvider extends LocalLogContentProvider {
	/** The authority for the contacts provider. */
	public final static String AUTHORITY = "com.lin.health.uart.log";
	/** A content:// style uri to the authority for the log provider. */
	public final static Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

	@Override
	protected Uri getAuthorityUri() {
		return AUTHORITY_URI;
	}
}
