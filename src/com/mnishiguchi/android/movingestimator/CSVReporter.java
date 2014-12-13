package com.mnishiguchi.android.movingestimator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import com.mnishiguchi.android.movingestimator.EstimateContract.EstimateTable;
import com.opencsv.CSVWriter;

class CSVReporter
{
	private static final String TAG = "movingestimator.CSVReporter";
	
	/**
	 * Create a sub-directory under public DOWNLOAD directory.
	 * @param dirname
	 * @return
	 */
	private static File getCSVStorageDir(String dirname)
	{
		// Get the directory for the user's public pictures directory. 
		File file = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DOWNLOADS),
				dirname);
		
		if (!file.mkdirs())
		{
			Log.e(TAG, "Directory not created");
		}
		return file;
	}
	
	static File createCSVReport(Context context, String customerId, Cursor cursor) throws IOException
	{
		String filename = "report_" +
				new SimpleDateFormat("yyyMMdd_HHmm_ss_SSS", Locale.US).format(new Date())
				+ ".csv";

		// Create a directory with the app name.
		File dir = getCSVStorageDir(context.getResources().getString(R.string.app_name));
		File file = new File(dir,filename);
		
		if (null == dir || null == file)
		{
			Log.e(TAG, "createCSVReport(), dir=>" + dir + ", file=>" + file);
		}
		CSVWriter writer = new CSVWriter(new FileWriter(file));

		// Temporary storage.
		List<String[]> data = new ArrayList<String[]>();
		
		// Customer name.
		data.add(new String[] {FileCabinet.get(context).getCustomer(customerId).toString()});
		data.add(new String[] {""}); // Empty line.
		
		// CSV header.
		data.add(new String[] {
				EstimateTable.COLUMN_MODE,
				EstimateTable.COLUMN_NAME,
				EstimateTable.COLUMN_SIZE,
				EstimateTable.COLUMN_QUANTITY,
				EstimateTable.COLUMN_SUBTOTAL,
				EstimateTable.COLUMN_ROOM,
				EstimateTable.COLUMN_COMMENT,
		});
		
		// CSV body based on the query result.
		String mode, name, size, quantity, subtotal, room, comment;
		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			// Extract data from cursor.
			mode = cursor.getString(0);
			name = cursor.getString(1);
			size = cursor.getString(2);
			quantity = cursor.getString(3);
			subtotal = cursor.getString(4);
			room = cursor.getString(5);
			comment = cursor.getString(6);
			
			// Add to the temporary list.
			data.add(new String[] {mode, name, size, quantity, subtotal, room, comment});
			
			// Move the cursor to next.
			cursor.moveToNext();
		}
		
		writer.writeAll(data);
		writer.close();
		
		return file;
	}
}
