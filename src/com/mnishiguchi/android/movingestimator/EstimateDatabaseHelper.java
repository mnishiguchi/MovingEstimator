package com.mnishiguchi.android.movingestimator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.mnishiguchi.android.movingestimator.EstimateContract.EstimateTable;

public class EstimateDatabaseHelper extends SQLiteOpenHelper
{
	// Database info.
	private static final String DB_NAME = "estimate.sqlite";
	private static final int VERSION = 1;
	
	// Field types.
	private static final String TEXT_TYPE = " TEXT"; // White space is necessary.
	private static final String INTEGER_TYPE = " INTEGER"; // White space is necessary.
	private static final String REAL_TYPE = " REAL"; // White space is necessary.
	private static final String COMMA_SEP = ",";
	
	// SQL for creating a table.
	private static final String SQL_CREATE_ESTIMATES =
		"CREATE TABLE " + EstimateContract.EstimateTable.TABLE_NAME + " (" +
		EstimateTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		EstimateTable.COLUMN_CUSTOMER_ID + TEXT_TYPE + COMMA_SEP +
		EstimateTable.COLUMN_ESTIMATE_DATE + INTEGER_TYPE + COMMA_SEP +
		EstimateTable.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
		EstimateTable.COLUMN_SIZE + REAL_TYPE + COMMA_SEP +
		EstimateTable.COLUMN_QUANTITY + INTEGER_TYPE + COMMA_SEP +
		EstimateTable.COLUMN_SUBTOTAL + REAL_TYPE + COMMA_SEP +
		EstimateTable.COLUMN_ROOM + TEXT_TYPE + COMMA_SEP +
		EstimateTable.COLUMN_MODE + TEXT_TYPE + COMMA_SEP +
		EstimateTable.COLUMN_COMMENT + TEXT_TYPE +
	" )";

	// SQL for dropping a table.
	private static final String SQL_DELETE_ESTIMATES =
		"DROP TABLE IF EXISTS " + EstimateContract.EstimateTable.TABLE_NAME;
	
	/**
	 * Constructor. Initialize underlying database.
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
		// This database is only a cache for online data, so its upgrade policy is
		// to simply to discard the data and start over
		db.execSQL(SQL_DELETE_ESTIMATES);
		onCreate(db);
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		onUpgrade(db, oldVersion, newVersion);
	}

}
