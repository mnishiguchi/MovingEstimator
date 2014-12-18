package com.mnishiguchi.android.movingestimator;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * A singleton class to keep the customer data available no matter what happens
 * with activities, fragments and their lifecycles.
 */
class FileCabinet
{
	private static final String TAG = "movingestimator.FileCabinet";
	
	private static final String FILENAME = "customers.json";
	
	// Store an instance of the  FileCabinet. 
	private static FileCabinet sFileCabinet;
	
	private Context mAppContext;
	private ArrayList<Customer> mCustomers;
	private MovingEstimatorJSONSerializer mSerializer;
	
	// Remember the reference to CustomerListFragment so that we can update its
	// UI when loading is completed.
	private CustomerListFragment mListFragment;
	
	void registerForLoadingCustomers(CustomerListFragment f)
	{
		mListFragment = f;
	}
	
	/**
	 * Private constructor.
	 */
	private FileCabinet(Context appContext)
	{
		// Remember the application context.
		mAppContext = appContext;
		
		// The reference to JSONSerializer, used to save and read data.
		mSerializer = new MovingEstimatorJSONSerializer(mAppContext, FILENAME);
		
		// Initialize the customer master list.
		mCustomers = new ArrayList<Customer>();
		
		// Load customers from the file system.
		new LoadCustomersTask().execute();
	}
	
	/**
	 * Get a reference to the FileCabinet singleton.
	 * @param context This could be an Activity or another Context object like Service.
	 * @return the FileCabinet singleton.
	 */
	static FileCabinet get(Context context)
	{
		if (null == sFileCabinet) // Only the first time.
		{
			// Create one and only instance of the FileCabinet.
			sFileCabinet = new FileCabinet(context.getApplicationContext());
		}
		return sFileCabinet;
	}
	
	Customer getCustomer(String id)
	{
		for (Customer customer : mCustomers)
		{
			if (customer.getId().equals(id))
			{
				return customer;
			}
		}
		Utils.showToast(mAppContext, mAppContext.getString(R.string.no_customer_with_this_id_found));
		return null;
	}
	
	/**
	 * @return an ArrayList of all the Customers stored in the FileCabinet.
	 */
	ArrayList<Customer> getCustomers()
	{
		return mCustomers;
	}
	
	/**
	 * Adds a new customer to the ArrayList that stores all the customers.
	 * @param customer
	 */
	void addCustomer(Customer customer)
	{
		mCustomers.add(customer);
		
		Log.e(TAG, customer + "(" + customer.getId() + ") added");
	}
	
	/**
	 * Delete a customer from the list.
	 * Ensure that other things associated with this customer is deleted. (e.g. photo, db...)
	 */
	void deleteCustomer(Customer customer)
	{
		// If the customer has a photo, delete it from disk.
		if (customer.getPhoto() != null)
		{
			// Delete the photo file on disk.
			if (!customer.getPhoto().deletePhoto(mAppContext))
			{
				Utils.showToast(mAppContext, mAppContext.getString(R.string.couldnt_delete_photo));
			}
		}
		
		// Delete this customer's estimate data from database.
		EstimateDataManager.get(mAppContext).deleteCustomer(customer.getId());
		
		// Delete this customer from the list.
		if (mCustomers.remove(customer))
		{
			Utils.showToast(mAppContext, mAppContext.getString(
					R.string.toast_deleted_customer,
					customer.toString()));
		}
		else
		{
			Utils.showToast(mAppContext, mAppContext.getString(R.string.couldnt_delete_customer));
		}
	}
	
	private class LoadCustomersTask extends AsyncTask<Void, Void, ArrayList<Customer>>
	{
		protected ArrayList<Customer> doInBackground(Void... params)
		{
			ArrayList<Customer> customers = null;
			
			try
			{
				// Load the customer from disk.
				customers = mSerializer.loadCustomers();
			}
			catch (Exception e)
			{
				Log.e(TAG, "Error loading customers", e);
			}
			return customers;
		}
		
		protected void onPostExecute(ArrayList<Customer> customers)
		{
			if (customers != null) // Success.
			{
				// Add all the loaded customers to mCustomers.
				mCustomers.addAll(customers);
				
				// Update the listView.
				mListFragment.updateListView();
			}
			else // Failure.
			{
				Utils.showToast(mAppContext,
						mAppContext.getString(R.string.error_loading_customers));
			}
		}
	}

	private class SaveCustomersTask extends AsyncTask<Void, Void, Boolean>
	{
		protected Boolean doInBackground(Void... params)
		{
			try
			{
				mSerializer.saveCustomers(mCustomers);
			}
			catch (Exception e)
			{
				Log.e(TAG, "Error saving customer", e);
				return false;
			}
			return true;
		}
		
		protected void onPostExecute(Boolean success)
		{
			if (!success)
			{
				Utils.showToast(mAppContext,
						mAppContext.getString(R.string.error_saving_customers));
			}
		}
	}
	
	/**
	 * Save the customers data to a file on the device's file system.
	 */
	void saveCustomers()
	{
		new SaveCustomersTask().execute();
	}
	
}
