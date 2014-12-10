package com.mnishiguchi.android.movingestimator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.mnishiguchi.android.movingestimator.EstimateContract.EstimateTable;

/**
 * A singleton class.
 */
class EstimateManager
{
	private static final String TAG = "movingestimator.EstimateManager";
	
	// Store an instance of the EstimateManager. 
	private static EstimateManager sEstimateManager;
	
	private Context mAppContext;
	private EstimateDatabaseHelper mDbHelper;
	//private SharedPreferences mPrefs;
	//private long mCurrentId;
	
	/**
	 * Private constructor.
	 */
	private EstimateManager(Context appContext)
	{
		Log.d(TAG, "FileCabinet constructor");
		
		// Remember the application context.
		mAppContext = appContext;
		
		mDbHelper = new EstimateDatabaseHelper(mAppContext);
		//mPrefs = mAppContext.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
	}
	
	/**
	 * Get a reference to the EstimateManager singleton.
	 * @param context This could be an Activity or another Context object like Service.
	 * @return the EstimateManager singleton.
	 */
	static EstimateManager get(Context context)
	{
		if (null == sEstimateManager) // Only the first time.
		{
			// Create one and only instance of the FileCabinet.
			sEstimateManager = new EstimateManager(context.getApplicationContext());
		}
		return sEstimateManager;
	}
	
	public long insertItem(String customerId, EstimateItem item)
	{
		ContentValues cv = new ContentValues();
		cv.put(EstimateTable.COLUMN_CUSTOMER_ID, customerId);
		cv.put(EstimateTable.COLUMN_ITEM_NAME, item.name);
		cv.put(EstimateTable.COLUMN_ITEM_SIZE, item.size);
		cv.put(EstimateTable.COLUMN_QUANTITY, item.quantity);
		cv.put(EstimateTable.COLUMN_ROOM, item.room);
		cv.put(EstimateTable.COLUMN_TRANSPORT_MODE, item.mode);
		cv.put(EstimateTable.COLUMN_COMMENT, item.comment);
		
		// Return the row ID of the newly inserted row, or -1 if an error occurred
		return mDbHelper.getWritableDatabase().insert(
				EstimateTable.TABLE_NAME, null, cv);
	}
	
	public boolean deleteSingleRow(long rowId) 
	{
		Log.d(TAG, "deleteSingleRow() - rowId: " + rowId);
		
		// DELETE FROM table_name WHERE some_column=some_value;
		String whereClause = EstimateTable._ID + " = ?";
		String[] whereArgs = new String[] {String.valueOf(rowId)};
		
		return mDbHelper.getWritableDatabase().delete(
				EstimateTable.TABLE_NAME,
				whereClause, whereArgs) > 0;
	}
	
	/**
	 * Retrieve estimate data for the specified room of the specified customer.
	 * Update the fragment's listView after completing the loading.
	 */
	void retrieveDataForRoom(String customerId, String room,
			final EstimateListFragment fragment)
	{
		// Prepare the params.
		String[] params = {customerId, room};
		
		new AsyncTask<String[], Void, Cursor>() {

			@Override
			protected Cursor doInBackground(String[]... params)
			{
				String[] columns = {
						EstimateTable._ID,
						EstimateTable.COLUMN_ITEM_NAME,
						EstimateTable.COLUMN_ITEM_SIZE,
						EstimateTable.COLUMN_QUANTITY,
						EstimateTable.COLUMN_TRANSPORT_MODE,
						EstimateTable.COLUMN_COMMENT,
				};
				String whereClause =
						EstimateTable.COLUMN_CUSTOMER_ID + " = ? AND " +
						EstimateTable.COLUMN_ROOM + " = ?";
				String[] whereArgs = params[0];
				String orderBy = columns[4] + " ASC";
						
				return mDbHelper.getWritableDatabase().query(
						EstimateTable.TABLE_NAME,
						columns, whereClause, whereArgs, null, null, orderBy);
			}
			
			protected void onPostExecute(Cursor result)
			{
				fragment.refreshCursorAdapter(result);
			}
			
		}.execute(params);
	}
	
	public void closeDatabase()
	{
		mDbHelper.close();
	}

}
