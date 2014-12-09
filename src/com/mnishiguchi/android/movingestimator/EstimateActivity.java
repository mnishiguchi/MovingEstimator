package com.mnishiguchi.android.movingestimator;

import android.support.v4.app.Fragment;

public class EstimateActivity extends SingleFragmentActivity
{
	@Override
	protected Fragment createFragment()
	{
		String customerId = getIntent().getStringExtra(EstimateContentFragment.EXTRA_CUSTOMER_ID);
		String room = getIntent().getStringExtra(EstimateContentFragment.EXTRA_ROOM);
		return EstimateContentFragment.newInstance(customerId, room);
	}
}