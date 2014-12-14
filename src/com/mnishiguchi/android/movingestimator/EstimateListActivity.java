package com.mnishiguchi.android.movingestimator;

import android.support.v4.app.Fragment;

public class EstimateListActivity extends SingleFragmentActivity
{
	private static final String TAG = "com.mnishiguchi.android.movingestimator.EstimatePagerActivity";
	public static final String EXTRA_ROOM = "com.mnishiguchi.android.movingestimator.roomPos";
	
	@Override
	protected Fragment createFragment()
	{
		String room = (String)getIntent()
				.getStringExtra(EstimateListActivity.EXTRA_ROOM);

		// Set the new page's title.
		setTitle(Customer.getCurrentCustomer().toString() + " | " + room);
		
		// Create a new EstimateListFragment for this room.
		return EstimateListFragment.newInstance(room);
	}
}