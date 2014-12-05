package com.mnishiguchi.android.movingestimator;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class EstimateDatabaseHelper extends SQLiteOpenHelper
{
	// Database info.
	private static final String DB_NAME = "estimate.sqlite";
	private static final int VERSION = 1;
	
	// Field types.
	private static final String TEXT_TYPE = " TEXT";
	private static final String INTEGER_TYPE = "INTEGER";
	private static final String REAL_TYPE = " REAL";
	private static final String COMMA_SEP = ",";
	
	// SQL for creating a table.
	private static final String SQL_CREATE_ESTIMATES =
		"CREATE TABLE " + EstimateContract.EstimateTable.TABLE_NAME + " (" +
		EstimateContract.EstimateTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		EstimateContract.EstimateTable.COLUMN_CUSTOMER_ID + TEXT_TYPE + COMMA_SEP +
		EstimateContract.EstimateTable.COLUMN_ESTIMATE_DATE + INTEGER_TYPE + COMMA_SEP +
		EstimateContract.EstimateTable.COLUMN_ITEM_NAME + TEXT_TYPE + COMMA_SEP +
		EstimateContract.EstimateTable.COLUMN_ITEM_SIZE + REAL_TYPE + COMMA_SEP +
		EstimateContract.EstimateTable.COLUMN_QUANTITY + INTEGER_TYPE + COMMA_SEP +
		EstimateContract.EstimateTable.COLUMN_ROOM + TEXT_TYPE + COMMA_SEP +
		EstimateContract.EstimateTable.COLUMN_TRANSPORT_MODE + TEXT_TYPE + COMMA_SEP +
		EstimateContract.EstimateTable.COLUMN_COMMENT + TEXT_TYPE + COMMA_SEP +
	" )";

	// SQL for dropping a table.
	private static final String SQL_DELETE_ESTIMATES =
		"DROP TABLE IF EXISTS " + EstimateContract.EstimateTable.TABLE_NAME;
	
	/**
	 * Constructor.
	 */
	public EstimateDatabaseHelper(Context context)
	{
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		// Create the estimate table.
		db.execSQL(SQL_CREATE_ESTIMATES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// Implement schema changes and data message here when upgrading.
	}
	
	public long insertMovingItem(MovingItem item)
	{
		ContentValues cv = new ContentValues();
		//cv.put(key, value);
		//cv.put(key, value);
		//cv.put(key, value);
		
		// Return the row ID of the newly inserted row, or -1 if an error occurred
		return getWritableDatabase().insert(
				EstimateContract.EstimateTable.TABLE_NAME, null, cv);
	}
}
