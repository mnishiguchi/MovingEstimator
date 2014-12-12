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
