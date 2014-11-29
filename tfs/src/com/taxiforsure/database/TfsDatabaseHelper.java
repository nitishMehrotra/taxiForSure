package com.taxiforsure.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TfsDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "LearningOutcomes.db";
	private static final int DATABASE_VERSION = 1;
	Context context;

	private static TfsDatabaseHelper instance;

	public TfsDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	public static synchronized TfsDatabaseHelper getHelper(Context context) {
		if (instance == null)
			instance = new TfsDatabaseHelper(context);

		return instance;
	}

	// Method is called during creation of the database
	@Override
	public void onCreate(SQLiteDatabase database) {
		TfsTableClass.onCreate(database, context);
	}

	// Method is called during an upgrade of the database,
	// e.g. if you increase the database version
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		TfsTableClass.onUpgrade(database, oldVersion, newVersion, context);
	}
}
