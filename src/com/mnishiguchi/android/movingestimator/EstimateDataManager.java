package com.mnishiguchi.android.movingestimator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.mnishiguchi.android.movingestimator.EstimateContract.EstimateTable;

/**
 * A singleton class.
 */
class EstimateDataManager
{
	private static final String TAG = "movingestimator.EstimateManager";
	
	// Store an instance of the EstimateManager. 
	private static EstimateDataManager sEstimateManager;
	
	private Context mAppContext;
	private EstimateDatabaseHelper mDbHelper;
	//private SharedPreferences mPrefs;
	
	/**
	 * Private constructor.
	 */
	private EstimateDataManager(Context appContext)
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
	static EstimateDataManager get(Context context)
	{
		if (null == sEstimateManager) // Only the first time.
		{
			// Create one and only instance of the FileCabinet.
			sEstimateManager = new EstimateDataManager(context.getApplicationContext());
		}
		return sEstimateManager;
	}
	
	/**
	 * Insert the passed-in estimate item into the database.
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	public void insertItem(EstimateItem item)
	{
		// Configure the task.
		AsyncTask<EstimateItem,Void,Long> insertTask =
				new AsyncTask<EstimateItem, Void, Long>() {

			@Override
			protected Long doInBackground(EstimateItem... params)
			{
				EstimateItem item = params[0];
				
				ContentValues cv = new ContentValues();
				cv.put(EstimateTable.COLUMN_CUSTOMER_ID, item.customerId);
				cv.put(EstimateTable.COLUMN_ITEM_NAME, item.name);
				cv.put(EstimateTable.COLUMN_ITEM_SIZE, item.size);
				cv.put(EstimateTable.COLUMN_QUANTITY, item.quantity);
				cv.put(EstimateTable.COLUMN_SUBTOTAL, item.subtotal);
				cv.put(EstimateTable.COLUMN_ROOM, item.room);
				cv.put(EstimateTable.COLUMN_TRANSPORT_MODE, item.mode);
				cv.put(EstimateTable.COLUMN_COMMENT, item.comment);
				
				// Return the row ID of the newly inserted row, or -1 if an error occurred
				return mDbHelper.getWritableDatabase().insert(
						EstimateTable.TABLE_NAME, null, cv);
			}
			protected void onPostExecute(boolean success)
			{
				Log.d(TAG, "success=>" + success);
				if (!success)
				{
					Log.e(TAG, "Error inserting an estimate item - ");
				}
				
				closeDatabase();
			}
		};
		
		// Execute the task.
		insertTask.execute(item);
	}
	
	/**
	 * Delete the specified row of the estimate data from the database.
	 * @return true if successful.
	 */
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
	 * Delete the specified room's estimate data from the database.
	 * @return true if successful.
	 */
	public boolean deleteRoom(String customerId, String room) 
	{
		// DELETE FROM table_name WHERE some_column=some_value;
		String whereClause =
				EstimateTable.COLUMN_CUSTOMER_ID + " = ? AND " +
				EstimateTable.COLUMN_ROOM + " = ?";
		String[] whereArgs = new String[] {customerId, room};
		
		return mDbHelper.getWritableDatabase().delete(
				EstimateTable.TABLE_NAME,
				whereClause, whereArgs) > 0;
	}
	
	/**
	 * Delete the specified customer's estimate data from the database.
	 * @return true if successful.
	 */
	public boolean deleteCustomer(String customerId) 
	{
		// DELETE FROM table_name WHERE some_column=some_value;
		String whereClause =
				EstimateTable.COLUMN_CUSTOMER_ID + " = ?";
		String[] whereArgs = new String[] {customerId};
		
		return mDbHelper.getWritableDatabase().delete(
				EstimateTable.TABLE_NAME,
				whereClause, whereArgs) > 0;
	}
	
	void retrieveModesForCustomer(String customerId, final EstimateOverviewFragment fragment)
	{
		// Prepare the params.
		String[] params = {customerId};
		
		// Validation
		for (String s : params)
		{
			if (null == s)
			{
				Log.e(TAG, "retrieveDataForMode() - params[0]=>" + params[0]);
				return;
			}
		}
		
		// Configure the task.
		new AsyncTask<String[], Void, Cursor>() {

			@Override
			protected Cursor doInBackground(String[]... params)
			{
				String[] columns = {
						EstimateTable.COLUMN_TRANSPORT_MODE
				};
				String whereClause = EstimateTable.COLUMN_CUSTOMER_ID + " = ?";
				String[] whereArgs = params[0];
				String groupBy = columns[0];
				String orderBy = columns[0] + " ASC";
						
				return mDbHelper.getWritableDatabase().query(
						EstimateTable.TABLE_NAME,
						columns, whereClause, whereArgs, groupBy, null, orderBy);
			}
			
			protected void onPostExecute(Cursor result)
			{
				fragment.setupSpinner(result);
				closeDatabase();
			}
		
		}.execute(params); // Execute the task.
	}

	/**
	 * Retrieve estimate data for the specified mode.
	 * Update the fragment's listView after completing the loading.
	 */
	void retrieveDataForMode(String customerId, String mode, final EstimateOverviewFragment fragment)
	{
		// Prepare the params.
		String[] params = {customerId, mode};
		
		// Validation
		for (String s : params)
		{
			if (null == s)
			{
				Log.e(TAG, "retrieveDataForMode() - params[0]=>" + params[0] +
						" - params[1]=>" + params[1]);
				return;
			}
		}
		
		// Configure the task.
		new AsyncTask<String[], Void, Cursor>() {

			@Override
			protected Cursor doInBackground(String[]... params)
			{
				String[] columns = {
						EstimateTable._ID,
						EstimateTable.COLUMN_ITEM_NAME,
						EstimateTable.COLUMN_ITEM_SIZE,
						EstimateTable.COLUMN_QUANTITY,
						EstimateTable.COLUMN_SUBTOTAL,
						EstimateTable.COLUMN_ROOM,
						EstimateTable.COLUMN_COMMENT,
				};
				String whereClause =
						EstimateTable.COLUMN_CUSTOMER_ID + " = ? AND " +
						EstimateTable.COLUMN_TRANSPORT_MODE + " = ?";
				String[] whereArgs = params[0];
				String orderBy = columns[5] + " ASC";
						
				return mDbHelper.getWritableDatabase().query(
						EstimateTable.TABLE_NAME,
						columns, whereClause, whereArgs, null, null, orderBy);
			}
			
			protected void onPostExecute(Cursor result)
			{
				fragment.refreshListView(result);
				closeDatabase();
			}
		
		}.execute(params); // Execute the task.
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
		
		// Validation
		for (String s : params)
		{
			if (null == s)
			{
				Log.e(TAG, "retrieveDataForMode() - params[0]=>" + params[0] +
						" - params[1]=>" + params[1]);
				return;
			}
		}
		
		// Configure the task.
		new AsyncTask<String[], Void, Cursor>() {

			@Override
			protected Cursor doInBackground(String[]... params)
			{
				String[] columns = {
						EstimateTable._ID,
						EstimateTable.COLUMN_ITEM_NAME,
						EstimateTable.COLUMN_ITEM_SIZE,
						EstimateTable.COLUMN_QUANTITY,
						EstimateTable.COLUMN_SUBTOTAL,
						EstimateTable.COLUMN_TRANSPORT_MODE,
						EstimateTable.COLUMN_COMMENT,
				};
				String whereClause =
						EstimateTable.COLUMN_CUSTOMER_ID + " = ? AND " +
						EstimateTable.COLUMN_ROOM + " = ?";
				String[] whereArgs = params[0];
				String orderBy = columns[5] + " ASC";
						
				return mDbHelper.getWritableDatabase().query(
						EstimateTable.TABLE_NAME,
						columns, whereClause, whereArgs, null, null, orderBy);
			}
			
			protected void onPostExecute(Cursor result)
			{
				fragment.refreshCursorAdapter(result);
				closeDatabase();
			}
			
		}.execute(params);
	}
	
	/**
	 * Close any open database object.
	 */
	public void closeDatabase()
	{
		mDbHelper.close();
	}

}
