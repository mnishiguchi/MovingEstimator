package com.mnishiguchi.android.movingestimator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import android.content.Context;
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
	
	// Instance variables.
	private Context mAppContext;
	private ArrayList<Customer> mCustomers;
	private MovingEstimatorJSONSerializer mSerializer;
	
	/**
	 * Constructor.
	 */
	private FileCabinet(Context appContext)
	{
		Log.d(TAG, "FileCabinet constructor");
		
		mAppContext = appContext;
		mSerializer = new MovingEstimatorJSONSerializer(mAppContext, FILENAME);
		
		mCustomers = new ArrayList<Customer>();
		// initFakeCustomers();
		
		// Load customers from the file system.
		loadCustomers();
	}
	
	/**
	 * Load the customers data from the device's file system.
	 */
	private boolean loadCustomers()
	{
		try
		{
			mCustomers = mSerializer.loadCustomers();
			
			Utils.showToast(mAppContext, "Customers successfully loaded.");
			return true;
		}
		catch (Exception e)
		{
			// Create a new arraylist
			mCustomers = new ArrayList<Customer>();
			
			Log.e(TAG, "Error loading customers", e);
			Utils.showToast(mAppContext, "Error loading customers.");
			return false;
		}
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
		Utils.showToast(mAppContext, "No customer with this id found.");
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
	}
	
	/**
	 * Delete a customer from the list.
	 * Ensure that other things associated with this customer is deleted. (e.g. photo, db...)
	 * @param customer
	 */
	void deleteCustomer(Customer customer)
	{
		// If the customer has a photo, delete it from disk.
		if (customer.getPhoto() != null)
		{
			// Delete the photo file on disk.
			if (!customer.getPhoto().deletePhoto(mAppContext))
			{
				Utils.showToast(mAppContext, "Couldn't delete the photo");
			}
		}
		
		// Delete the specified customer from the list.
		if (mCustomers.remove(customer))
		{
			Utils.showToast(mAppContext, customer.toString() + " deleted");
		}
		else
		{
			Utils.showToast(mAppContext, "Couldn't delete the customer");
		}
	}
	
	/**
	 * Save the customers data to a file on the device's file system.
	 */
	boolean saveCustomers()
	{
		try
		{
			mSerializer.saveCustomers(mCustomers);
			Utils.showToast(mAppContext, "Customers saved to file");
			return true;
		}
		catch (Exception e)
		{
			Utils.showToast(mAppContext, "Error saving customer");
			Log.e(TAG, "Error saving customer", e);
			return false;
		}
	}
	
	@SuppressWarnings("unused")
	private void initFakeCustomers()
	{
		Random r = new Random();
		
		for (int i = 0; i < 12; i++)
		{
			Customer c = new Customer();
			c.setRefNumber(String.valueOf(r.nextInt(999999)));
			c.setFirstName("Masa" + UUID.randomUUID().toString().toLowerCase().substring(0, 4));
			c.setLastName("Nishiguchi" + i);
			String prefix = (i%2 == 0) ? "Mr." : "Ms.";   // Every other one
			c.setPrefix(prefix);
			c.setOrganization("My Company - " + i);
			c.setAddress(String.valueOf(r.nextInt(1000)) + " Random Rd. NW Washington, DC 20123");
			c.setEmail(UUID.randomUUID().toString().substring(0, 6) + "@" + "mnishiguchi.com");
			c.setPhoneHome("" + r.nextInt(999) + "-" + r.nextInt(999) + "-" + r.nextInt(9999));
			c.setPhoneWork("" + r.nextInt(999) + "-" + r.nextInt(999) + "-" + r.nextInt(9999));
			c.setPhoneCell("" + r.nextInt(999) + "-" + r.nextInt(999) + "-" + r.nextInt(9999));
			c.setVolumeOcean(r.nextInt(30));
			c.setVolumeAir(r.nextInt(100));
			//c.setVolumeComment("This is the notes on the moving limit.");
			c.setMovingDate(new Date());
			//c.setMovingDateComment("Some comment on moving date.");
			//c.setHomeDescription("This is the home description.");
			//c.setSpecialOrders("Something special ;)");
			//c.setGeneralComment("This is the GeneralComment.");
			
			mCustomers.add(c);
		}
	}
}
