package com.mnishiguchi.android.movingestimator;

import android.support.v4.app.Fragment;

public class EstimateOverviewActivity extends SingleFragmentActivity
{
	@Override
	protected Fragment createFragment()
	{
		return new EstimateOverviewFragment();
	}
}
