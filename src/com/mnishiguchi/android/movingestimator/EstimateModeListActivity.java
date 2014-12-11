package com.mnishiguchi.android.movingestimator;

import android.support.v4.app.Fragment;

public class EstimateModeListActivity extends SingleFragmentActivity
implements EstimateModeListFragment.ListCallbacks
{
	private static final String TAG = "movingestimator.EstimateOverviewModeListActivity";

	@Override
	protected Fragment createFragment()
	{
		return new EstimateModeListFragment();
	}
	
	@Override
	protected int getLayoutResId()
	{
		// an alias resource defined in res/values/refs.xml
		return R.layout.activity_masterdetail_2; 
	}

	@Override
	public void onListItemClicked(String room)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onListItemDeleted(String room)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onListReset()
	{
		// TODO Auto-generated method stub
		
	}

}
