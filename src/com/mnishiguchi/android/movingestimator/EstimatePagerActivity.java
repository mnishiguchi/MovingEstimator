package com.mnishiguchi.android.movingestimator;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class EstimatePagerActivity extends FragmentActivity implements
		ViewPager.OnPageChangeListener
{
	private static final String TAG = "com.mnishiguchi.android.movingestimator.EstimatePagerActivity";
	public static final String EXTRA_ROOM = "com.mnishiguchi.android.movingestimator.roomPos";
	
	private int mPosition;
	private ViewPager mViewPager;
	
	// Reference to the list of rooms stored in the FileCabinet.
	private ArrayList<String> mRooms;
	
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
		mRooms = Customer.getCurrentCustomer().getRooms();
		
		//setTitle(R.string.actionbar_title_rooms);
		
		// Configuration.
		setupPagerAdapter();
		setupInitialPagerItem();
		mViewPager.setOnPageChangeListener(this);
	}
	
	private void setupPagerAdapter()
	{
		FragmentManager fm = getSupportFragmentManager();
		mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
			
			@Override
			public int getCount()
			{
				return mRooms.size();
			}
			
			/**
			 * Returns a CrimeFragment configured to
			 * display the crime at the specified position.
			 */
			@Override
			public Fragment getItem(int position)
			{
				// Get the room at the passed-in position.
				String room = mRooms.get(position);
				
				// Create a new EstimateListFragment for this room.
				return EstimateListFragment.newInstance(room);
			}
		});
	}
	
	/**
	 * Sets the initial page to the selected item on the ListFragment.
	 */
	private void setupInitialPagerItem()
	{
		String initialRoom = (String)getIntent()
			.getStringExtra(EstimatePagerActivity.EXTRA_ROOM);
		for (int i = 0; i < mRooms.size(); i++)
		{
			if (mRooms.get(i).equals(initialRoom))
			{
				mViewPager.setCurrentItem(i);
				return;
			}
		}
	}

	// Invoked when a new page becomes selected.
	@Override
	public void onPageSelected(int position)
	{
		Log.d(TAG, "onPageSelected(...)");

		mPosition = position;
		
		// Get the customer at the passed-in position.
		String room = mRooms.get(position);

		// Set the new page's title.
		setTitle(room);
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
}
