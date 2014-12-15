package com.mnishiguchi.android.movingestimator;

import android.support.v4.app.Fragment;

public class EstimateListActivity extends SingleFragmentActivity
{
	public static final String EXTRA_ROOM = "com.mnishiguchi.android.movingestimator.room";
	
	@Override
	protected Fragment createFragment()
	{
		String room = (String)getIntent()
				.getStringExtra(EstimateListActivity.EXTRA_ROOM);

		// Create a new EstimateListFragment for this room.
		return EstimateListFragment.newInstance(room);
	}
}
