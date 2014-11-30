package com.mnishiguchi.android.movingestimator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import android.content.Context;

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
	//private MovingEstimatorJSONSerializer mSerializer;
	
	/**
	 * Constructor.
	 */
	private FileCabinet(Context appContext)
	{
		mAppContext = appContext;
		//mSerializer = new MovingEstimatorJSONSerializer(mAppContext, FILENAME);
		
		mCustomers = new ArrayList<Customer>();
		initFakeCustomers();
		
		// Load customers from the file system.
		// TODO
		
	}
	
	/**
	 * Get a reference to the FileCabinet singleton.
	 * @param context This could be an Activity or another Context object like Service.
	 * @return the FileCabinet singleton.
	 */
	static FileCabinet get(Context context)
	{
		if (null == sFileCabinet)  // Only the first time.
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
		// Delete the specified customer from the list.
		mCustomers.remove(customer);
	}
	
	/**
	 * Load the customers data from the device's file system.
	 */
	boolean loadCustomers()
	{
		// TODO
		return true;
	}
	
	/**
	 * Save the customers data to a file on the device's file system.
	 */
	boolean saveCustomers()
	{
		// TODO
		return true;
	}
	
	@SuppressWarnings("unused")
	private void initFakeCustomers()
	{
		Random r = new Random();
		
		for (int i = 0; i < 12; i++)
		{
			Customer c = new Customer();
			c.setRefNumber(String.valueOf(r.nextInt(1000)));
			c.setFirstName("Customer #" + i);
			c.setLastName(UUID.randomUUID().toString().substring(0, 12));
			String prefix = (i%2 == 0) ? "Mr." : "Ms.";   // Every other one
			c.setPrefix(prefix);
			c.setCompanyName("MasaTech - " + new SimpleDateFormat("yyyMMdd_HHmm_ss_SSS", Locale.US).format(new Date()));
			c.setAddress("123456 River Rd. NW Washington, DC 20123");
			c.setPhoneNumber(String.valueOf(r.nextLong()));
			c.setLimitOcean(r.nextInt(30));
			c.setLimitAir(r.nextInt(100));
			c.setLimitNotes("This is the notes on the moving limit.");
			c.setMovingDate(new Date());
			c.setHomeDescription("This is the home description.");
			c.setRemarks("This is the remarks.");
			
			mCustomers.add(c);
		}
	}
}
