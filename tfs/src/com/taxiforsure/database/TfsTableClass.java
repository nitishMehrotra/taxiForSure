package com.taxiforsure.database;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class TfsTableClass extends Activity {

	/** < School Table */
	private static String USER = "user";
	public static final String CREATE_TABLE = "create table " + USER + "("
			+ "userId" + " integer primary key AUTOINCREMENT, " + "pickup"
			+ " text not null, date text not null, time text not null" + ");";

	public static void onCreate(SQLiteDatabase database, Context context) {
		database.execSQL(CREATE_TABLE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion, Context context) {
		database.execSQL("DROP TABLE IF EXISTS " + USER);
		onCreate(database, context);
	}
}
