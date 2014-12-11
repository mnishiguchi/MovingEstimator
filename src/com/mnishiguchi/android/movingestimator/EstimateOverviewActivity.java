package com.mnishiguchi.android.movingestimator;

import android.support.v4.app.Fragment;

public class EstimateOverviewActivity extends SingleFragmentActivity
{
	@Override
	protected Fragment createFragment()
	{
		// Return an instance of the fragment that the activity is hosting. 
		//return new EstimateOverviewFragment();
		return null;
	}
	
	@Override
	protected int getLayoutResId()
	{
		// an alias resource defined in res/values/refs.xml
		return R.layout.activity_masterdetail_1; 
	}

}
