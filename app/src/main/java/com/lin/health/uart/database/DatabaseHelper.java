package com.lin.health.uart.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DatabaseHelper {
	/** Database file name */
	private static final String DATABASE_NAME = "toolbox_uart.db";
	/** Database version */
	private static final int DATABASE_VERSION = 1;

	private interface Tables {
		/** Configurations table. See {@link ConfigurationContract.Configuration} for column names. */
		public static final String CONFIGURATIONS = "configurations";
	}

    private static final String[] ID_PROJECTION = new String[] { BaseColumns._ID };
	private static final String[] NAME_PROJECTION = new String[] { BaseColumns._ID, NameColumns.NAME };
	private static final String[] XML_PROJECTION = new String[] { BaseColumns._ID, ConfigurationContract.Configuration.XML };
	private static final String[] CONFIGURATION_PROJECTION = new String[] { BaseColumns._ID, NameColumns.NAME, ConfigurationContract.Configuration.XML };

	private static final String ID_SELECTION = BaseColumns._ID + "=?";
	private static final String NAME_SELECTION = NameColumns.NAME + "=?";
	private static final String DELETED_SELECTION = UndoColumns.DELETED + "=1";
	private static final String NOT_DELETED_SELECTION = UndoColumns.DELETED + "=0";

	private static SQLiteHelper mDatabaseHelper;
	private static SQLiteDatabase mDatabase;
	private final ContentValues mValues = new ContentValues();
	private final String[] mSingleArg = new String[1];

	public DatabaseHelper(final Context context) {
		if (mDatabaseHelper == null) {
			mDatabaseHelper = new SQLiteHelper(context);
			mDatabase = mDatabaseHelper.getWritableDatabase();
		}
	}

	/**
	 * Returns number of saved configurations.
	 */
	public int getConfigurationsCount() {
		final Cursor cursor = mDatabase.query(Tables.CONFIGURATIONS, ID_PROJECTION, NOT_DELETED_SELECTION, null, null, null, null);
		try {
			return cursor.getCount();
		} finally {
			cursor.close();
		}
	}

	/**
	 * Returns the list of all saved configurations.
	 * @return cursor
	 */
	public Cursor getConfigurations() {
		return mDatabase.query(Tables.CONFIGURATIONS, CONFIGURATION_PROJECTION, NOT_DELETED_SELECTION, null, null, null, ConfigurationContract.Configuration.NAME + " ASC");
	}

	/**
	 * Returns the list of names of all saved configurations.
	 * @return cursor
	 */
	public Cursor getConfigurationsNames() {
		return mDatabase.query(Tables.CONFIGURATIONS, NAME_PROJECTION, NOT_DELETED_SELECTION, null, null, null, ConfigurationContract.Configuration.NAME + " ASC");
	}

	/**
	 * Returns the XML wth the configuration by id.
	 * @param id the configuration id in the DB
	 * @return the XML with configuration or null
	 */
	public String getConfiguration(final long id) {
		mSingleArg[0] = String.valueOf(id);

		final Cursor cursor = mDatabase.query(Tables.CONFIGURATIONS, XML_PROJECTION, ID_SELECTION, mSingleArg, null, null, null);
		try {
			if (cursor.moveToNext())
				return cursor.getString(1 /* XML */);
			return null;
		} finally {
			cursor.close();
		}
	}

	/**
	 * Adds new configuration to the database.
	 * @param name the configuration name
	 * @param configuration the XML
	 * @return the id or -1 if error occurred
	 */
	public long addConfiguration(final String name, final String configuration) {
		final ContentValues values = mValues;
		values.clear();
		values.put(ConfigurationContract.Configuration.NAME, name);
		values.put(ConfigurationContract.Configuration.XML, configuration);
		values.put(ConfigurationContract.Configuration.DELETED, 0);
		return mDatabase.replace(Tables.CONFIGURATIONS, null, values);
	}

	/**
	 * Updates the configuration with the given name with the new XML
	 * @param name the configuration name to be updated
	 * @param configuration the new XML with configuration
	 * @return number of rows updated
	 */
	public int updateConfiguration(final String name, final String configuration) {
		mSingleArg[0] = name;

		final ContentValues values = mValues;
		values.clear();
		values.put(ConfigurationContract.Configuration.XML, configuration);
		values.put(ConfigurationContract.Configuration.DELETED, 0);
		return mDatabase.update(Tables.CONFIGURATIONS, values, NAME_SELECTION, mSingleArg);
	}

	/**
	 * Marks the configuration with given name as deleted. If may be restored or removed permanently afterwards.
	 * @param name the configuration name
	 * @return id of the deleted configuration
	 */
	public long deleteConfiguration(final String name) {
		mSingleArg[0] = name;

		final ContentValues values = mValues;
		values.clear();
		values.put(ConfigurationContract.Configuration.DELETED, 1);
		mDatabase.update(Tables.CONFIGURATIONS, values, NAME_SELECTION, mSingleArg);

		final Cursor cursor = mDatabase.query(Tables.CONFIGURATIONS, ID_PROJECTION, NAME_SELECTION, mSingleArg, null, null, null);
		try {
			if (cursor.moveToNext())
				return cursor.getLong(0 /* _ID */);
			return -1;
		} finally {
			cursor.close();
		}
	}

	public int removeDeletedServerConfigurations() {
		return mDatabase.delete(Tables.CONFIGURATIONS, DELETED_SELECTION, null);
	}

	/**
	 * Restores deleted configuration. Returns the ID of the first one.
	 * @return the DI of the restored configuration.
	 */
	public long restoreDeletedServerConfiguration(final String name) {
		mSingleArg[0] = name;

		final ContentValues values = mValues;
		values.clear();
		values.put(ConfigurationContract.Configuration.DELETED, 0);
		mDatabase.update(Tables.CONFIGURATIONS, values, NAME_SELECTION, mSingleArg);

		final Cursor cursor = mDatabase.query(Tables.CONFIGURATIONS, ID_PROJECTION, NAME_SELECTION, mSingleArg, null, null, null);
		try {
			if (cursor.moveToNext())
				return cursor.getLong(0 /* _ID */);
			return -1;
		} finally {
			cursor.close();
		}
	}

	/**
	 * Renames the server configuration and replaces its XML (name inside has changed).
	 * @param oldName the old name to look for
	 * @param newName the new configuration name
	 * @param configuration the new XML
	 * @return number of rows affected
	 */
	public int renameConfiguration(final String oldName, final String newName, final String configuration) {
		mSingleArg[0] = oldName;

		final ContentValues values = mValues;
		values.clear();
		values.put(ConfigurationContract.Configuration.NAME, newName);
		values.put(ConfigurationContract.Configuration.XML, configuration);
		return mDatabase.update(Tables.CONFIGURATIONS, values, NAME_SELECTION, mSingleArg);
	}

	/**
	 * Returns true if a configuration with given name was found in the database.
	 * @param name the name to check
	 * @return true if such name exists, false otherwise
	 */
	public boolean configurationExists(final String name) {
		mSingleArg[0] = name;

		final Cursor cursor = mDatabase.query(Tables.CONFIGURATIONS, NAME_PROJECTION, NAME_SELECTION + " AND " + NOT_DELETED_SELECTION, mSingleArg, null, null, null);
		try {
			return cursor.getCount() > 0;
		} finally {
			cursor.close();
		}
	}

	private class SQLiteHelper extends SQLiteOpenHelper {

		/**
		 * The SQL code that creates the Server Configurations:
		 *
		 * <pre>
		 * ----------------------------------------------------------------------------
		 *                            CONFIGURATIONS                           |
		 * ----------------------------------------------------------------------------
		 * | _id (int, pk, auto increment) | name (text) | xml (text) | deleted (int) |
		 * ----------------------------------------------------------------------------
		 * </pre>
		 */
		private static final String CREATE_CONFIGURATIONS = "CREATE TABLE " + Tables.CONFIGURATIONS+ "(" + ConfigurationContract.Configuration._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ ConfigurationContract.Configuration.NAME + " TEXT UNIQUE NOT NULL, " + ConfigurationContract.Configuration.XML + " TEXT NOT NULL, " + ConfigurationContract.Configuration.DELETED +" INTEGER NOT NULL DEFAULT(0))";

		private static final String DROP_IF_EXISTS = "DROP TABLE IF EXISTS ";

		public SQLiteHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(final SQLiteDatabase db) {
			db.execSQL(CREATE_CONFIGURATIONS);
		}

		@Override
		public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
			// This method does nothing for now.
			switch (oldVersion) {
				case 1:
					// do nothing
			}
		}
	}
}
