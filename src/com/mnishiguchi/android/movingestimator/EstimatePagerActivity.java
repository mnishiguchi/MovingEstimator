package com.mnishiguchi.android.movingestimator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

public class EstimatePagerActivity extends SingleFragmentActivity
{
	private static final String TAG = "com.mnishiguchi.android.movingestimator.EstimatePagerActivity";
	public static final String EXTRA_ROOM = "com.mnishiguchi.android.movingestimator.roomPos";
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Log.d(TAG, "onCreate()");
<<<<<<< HEAD
		
		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.viewPager);
	
		// This id is manually defined in res/values/ids.xml
		setContentView(mViewPager);
		
		// Get the list of crimes via the FileCabinet singleton.
		mRooms = Customer.getCurrentCustomer().getRooms();
		
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
		// Get the selected room via intent.
		String initialRoom = (String)getIntent()
			.getStringExtra(EstimatePagerActivity.EXTRA_ROOM);
		Log.d(TAG, "initialRoom=>" + initialRoom);
		
		for (int i = 0; i < mRooms.size(); i++)
		{
			if (mRooms.get(i).equals(initialRoom))
			{
				mViewPager.setCurrentItem(i);
				
				// Remember the initial position.
				mPosition = i;
				return;
			}
		}
=======
>>>>>>> removing-estimatePager
	}

	@Override
	protected Fragment createFragment()
	{
		String room = (String)getIntent()
				.getStringExtra(EstimatePagerActivity.EXTRA_ROOM);

		// Set the new page's title.
		setTitle(Customer.getCurrentCustomer().toString() + " | " + room);
		
		// Create a new EstimateListFragment for this room.
		return EstimateListFragment.newInstance(room);
	}
}
