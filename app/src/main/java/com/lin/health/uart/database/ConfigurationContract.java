package com.lin.health.uart.database;

import android.provider.BaseColumns;

public class ConfigurationContract {

	protected interface ConfigurationColumns {
		/** The XML with configuration. */
		public final static String XML = "xml";
	}

	public final class Configuration implements BaseColumns, NameColumns, ConfigurationColumns, UndoColumns {
		private Configuration() {
			// empty
		}
	}
}
