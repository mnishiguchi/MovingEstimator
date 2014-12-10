package com.mnishiguchi.android.movingestimator;

import java.util.concurrent.ExecutionException;

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
	public long insertItem(EstimateItem item)
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
				cv.put(EstimateTable.COLUMN_ROOM, item.room);
				cv.put(EstimateTable.COLUMN_TRANSPORT_MODE, item.mode);
				cv.put(EstimateTable.COLUMN_COMMENT, item.comment);
				
				// Return the row ID of the newly inserted row, or -1 if an error occurred
				return mDbHelper.getWritableDatabase().insert(
						EstimateTable.TABLE_NAME, null, cv);
			}
		};
		// Execute the task.
		insertTask.execute(item);
		
		// Get the result and return it.
		long rowId = -1;
		try
		{
			rowId = insertTask.get();
		}
		catch (InterruptedException e)
		{
			Log.e(TAG, "insertItem() - ", e);
		}
		catch (ExecutionException e)
		{
			Log.e(TAG, "insertItem() - ", e);
		}
		return rowId;
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
	
	/**
	 * Retrieve estimate data for the specified room of the specified customer.
	 * Update the fragment's listView after completing the loading.
	 */
	Cursor retrieveDataForRoom(String customerId, String room,
			final EstimateListFragment fragment)
	{
		// Prepare the params.
		String[] params = {customerId, room};
		
		// Configure the task.
		AsyncTask<String[], Void, Cursor> queryTask =
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
		};
		// Execute the task.
		queryTask.execute(params);
		
		// Get the result and return it.
		Cursor cursor = null;
		try
		{
			cursor =  (Cursor)queryTask.get();
		}
		catch (InterruptedException e)
		{
			Log.e(TAG, "retrieveDataForRoom() - ", e);
		}
		catch (ExecutionException e)
		{
			Log.e(TAG, "retrieveDataForRoom() - ", e);
		}
		return cursor;
	}
	
	/**
	 * Close any open database object.
	 */
	public void closeDatabase()
	{
		mDbHelper.close();
	}

}
