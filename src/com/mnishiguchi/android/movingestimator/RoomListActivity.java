package com.mnishiguchi.android.movingestimator;

import android.support.v4.app.Fragment;

public class RoomListActivity extends SingleFragmentActivity implements
	RoomListFragment.ListCallbacks
{
	@Override
	protected Fragment createFragment()
	{
		// Retrieve the customerId from intent.
		// Return an instance of the fragment that the activity is hosting. 
		String customerId = getIntent().getStringExtra(RoomListFragment.EXTRA_CUSTOMER_ID_ROOM);
		return RoomListFragment.newInstance(customerId);
	}

	@Override
	protected int getLayoutResId()
	{
		// an alias resource defined in res/values/refs.xml
		return R.layout.activity_masterdetail; 
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
}