package com.mnishiguchi.android.movingestimator;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

public class CustomerPagerActivity extends FragmentActivity
	implements CustomerDetailFragment.DetailCallbacks
{
	private int mPosition;
	private ViewPager mViewPager;
	
	// Reference to the list of customers stored in the FileCabinet.
	private ArrayList<Customer> mCustomers;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.viewPager);

		// This id is manually defined in res/values/ids.xml
		setContentView(mViewPager);
		
		// Get the list of crimes via the FileCabinet singleton.
		mCustomers = FileCabinet.get(this).getCustomers();
		
		//setTitle(R.string.actionbar_title_customer_info);
		
		// Configuration.
		setupPagerAdapter();
		setupInitialPagerItem();
		setupPagerListener();
	}
	
	private void setupPagerAdapter()
	{
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
		String customerId = (String)getIntent()
			.getStringExtra(CustomerDetailFragment.EXTRA_CUSTOMER_ID);
		
		for (int i = 0; i < mCustomers.size(); i++)
		{
			if (mCustomers.get(i).getId().equals(customerId))
			{
				mViewPager.setCurrentItem(i);
				
				// Remember the initial position.
				mPosition = i;
				break;
			}
		}
	}
	
	private void setupPagerListener()
	{
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			// Invoked when a new page becomes selected.
			// Position is not reliable because the pager open the adjacent pages.
			@Override
			public void onPageSelected(int position)
			{
				mPosition = position;
				
				// Get the customer at the passed-in position.
				Customer customer = mCustomers.get(mPosition);

				// Set the new page's title.
				if (customer.getLastName() != null || !customer.getLastName().equals(""))
				{
					setTitle(customer.toString());
				}
			}
			
			// Invoked when the current page is scrolled
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
			{ } // Required but not used in this implementation.
			
			@Override
			public void onPageScrollStateChanged(int state)
			{ } // Required but not used in this implementation.
		});
	}
	
	PagerAdapter getPagerAdapter()
	{
		return mViewPager.getAdapter();
	}

	@Override
	public void onCustomerAdded(Customer customer)
	{ } // Required but not used in this implementation.

	@Override
	public void onCustomerUpdated(Customer customer)
	{ } // Required but not used in this implementation.

	@Override
	public void onCustomerDeleted(Customer customer)
	{ } // Required but not used in this implementation.
}
