package com.taxiforsure.database;

import android.database.sqlite.SQLiteDatabase;

public class TFSDatabaseUtility {
	private static TFSDatabaseUtility m_instance = null;
	private SQLiteDatabase m_database;

	private TFSDatabaseUtility() {
	}

	public static synchronized TFSDatabaseUtility getInstance() {
		if (m_instance == null) {
			m_instance = new TFSDatabaseUtility();
		}
		return m_instance;
	}

	/*
	 * set database
	 */
	public void setDatabase(SQLiteDatabase database) {
		m_database = database;
	}

	/*
	 * get Database
	 */

	public SQLiteDatabase getDatabase() {
		return m_database;
	}
}
