package com.mnishiguchi.android.movingestimator;

import java.util.ArrayList;
import java.util.UUID;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class CustomerPagerActivity extends FragmentActivity
	implements CustomerDetailFragment.DetailCallbacks
{
	private static final String TAG = "CriminalIntent.PagerActivity";
	
	private int mPosition;
	
	private ViewPager mViewPager;
	
	// Reference to the list of customers stored in the FileCabinet.
	private ArrayList<Customer> mCustomers;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Log.d(TAG, "onCreate()");
		
		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.viewPager);

		// This id is manually defined in res/values/ids.xml
		setContentView(mViewPager);
		
		// Get the list of crimes via the FileCabinet singleton.
		mCustomers = FileCabinet.get(this).getCustomers();
		
		setTitle(R.string.actionbar_title_customer_info);
		
		// Configuration.
		setupPagerAdapter();
		setupInitialPagerItem();
		setupPagerListener();
	}
	
	private void setupPagerAdapter()
	{
		Log.d(TAG, "setUpPagerAdapter()");
		FragmentManager fm = getSupportFragmentManager();
		mViewPager.setAdapter( new FragmentStatePagerAdapter(fm) {
			
			@Override
			public int getCount()
			{
				return mCustomers.size();
			}
			
			/**
			 * Returns a CrimeFragment configured to
			 * display the crime at the specified position.
			 */
			@Override
			public Fragment getItem(int position)
			{
				// Get the customer at the passed-in position.
				Customer customer = mCustomers.get(position);
				
				// Create a new detailFragment with this customer's id.
				return CustomerDetailFragment.newInstance(customer.getId());
			}
		});
	}
	
	/**
	 * Sets the initial page to the selected item on the ListFragment.
	 */
	private void setupInitialPagerItem()
	{
		Log.d(TAG, "setUpInitialPagerItem()");
		String customerId = (String)getIntent()
			.getStringExtra(CustomerDetailFragment.EXTRA_CUSTOMER_ID_DETAIL);
		for (int i = 0; i < mCustomers.size(); i++)
		{
			if (mCustomers.get(i).getId().equals(customerId))
			{
				mViewPager.setCurrentItem(i);
				break;
			}
		}
	}
	
	private void setupPagerListener()
	{
		Log.d(TAG, "setUpEventListener()");
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			// Invoked when a new page becomes selected.
			@Override
			public void onPageSelected(int position)
			{
				Log.d(TAG, "onPageSelected(...)");

				mPosition = position;
				
				// Get the customer at the passed-in position.
				Customer customer = mCustomers.get(position);

				// Set the new page's title.
				if (customer.getLastName() != null || !customer.getLastName().equals(""))
				{
					Log.d(TAG, "onPageSelected");
					setTitle(customer.toString());
				}
			}
			
			// Invoked when the current page is scrolled
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
			{
				// Required but not used in this implementation.
			}
			
			@Override
			public void onPageScrollStateChanged(int state)
			{
				// Required but not used in this implementation.
			}
		});
	}
	
	PagerAdapter getPagerAdapter()
	{
		return mViewPager.getAdapter();
	}

	@Override
	public void onCustomerAdded(Customer customer)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCustomerUpdated(Customer customer)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCustomerDeleted(Customer customer)
	{
		// TODO Auto-generated method stub
		
	}



}
