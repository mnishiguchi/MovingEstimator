package com.mnishiguchi.android.movingestimator;

import android.support.v4.app.Fragment;

public class EstimateActivity extends SingleFragmentActivity
{
	@Override
	protected Fragment createFragment()
	{
		String customerId = getIntent().getStringExtra(EstimateListFragment.EXTRA_CUSTOMER_ID);
		String room = getIntent().getStringExtra(EstimateListFragment.EXTRA_ROOM);
		return EstimateListFragment.newInstance(customerId, room);
	}
}