package com.mnishiguchi.android.movingestimator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;

/**
 * Utility class for loading and saving Customer objects to a JSON file
 * that is located in the application's private directory.
 */
public class MovingEstimatorJSONSerializer
{
	//private static final String TAG = "movingestimator.MovingEstimatorJSONSerializer";
	private Context mContext;
	private String mFileName;
	
	/**
	 * Constructor.
	 */
	public MovingEstimatorJSONSerializer(Context context, String fileName)
	{
		mContext = context;
		mFileName = fileName;
	}
	
	/**
	 * Load customers from the file system.
	 * @return a list of Customer objects.
	 */
	public ArrayList<Customer> loadCustomers() throws IOException, JSONException
	{
		ArrayList<Customer> customers = new ArrayList<Customer>();
		BufferedReader reader = null;
		InputStream in = null;
		
		// Open the file , read it, and put it into a StringBuilder.
		try
		{
			in = mContext.openFileInput(mFileName);

			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			
			String line = null;
			while (null != (line = reader.readLine()))  // Read until consuming all.
			{
				// Line breaks are omitted and irrelevant.
				jsonString.append(line);
			}
			
			// Parse the JSON using JSONTokener.
			JSONArray jsonArray = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
			
			// Build the ArrayList of customers from JSONObjects.
			for (int i = 0, size = jsonArray.length();
					i < size; i++)
			{
				// Create a customer object from each JSONObject, and add it to the list.
				customers.add(new Customer(jsonArray.getJSONObject(i)));
			}
		}
		catch (FileNotFoundException e)
		{ } // Ignore this one; it happens when starting fresh.
		finally
		{
			// Ensure that the underlying file handle is freed up even if an error occurs.
			if (reader != null)
			{
				reader.close();
			}
		}
		
		return customers;  // a list of Customer objects.
	}
	
	public void saveCustomers(ArrayList<Customer> customers) throws JSONException, IOException
	{
		// Build an array in JSON.
		JSONArray array = new JSONArray();
		for (Customer each : customers)
		{
			// Convert each crime to JSON and put it in the array.
			array.put(each.toJSON());
		}
		
		// Write the file to disk.
		Writer writer = null;
		OutputStream out = null;
		try
		{
			out = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
			
			writer = new OutputStreamWriter(out);
			writer.write(array.toString());  // Write the array as a compact JSON string.
		}
		finally
		{
			if (writer != null)
			{
				writer.close();
			}
		}
	}
}
