package com.mnishiguchi.android.movingestimator;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class EstimateRoomListActivity extends SingleFragmentActivity implements
	EstimateRoomListFragment.ListCallbacks
{
	private static final String TAG = "movingestimator.RoomListActivity";
	
	String mCustomerId;
	
	@Override
	protected Fragment createFragment()
	{
		// Retrieve the customerId from intent.
		// Return an instance of the fragment that the activity is hosting. 
		mCustomerId = getIntent().getStringExtra(EstimateRoomListFragment.EXTRA_CUSTOMER_ID_ROOM);
		return EstimateRoomListFragment.newInstance(mCustomerId);
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
		if (Utils.hasTwoPane(this)) // Two-pane
		{
			Log.d(TAG, "onListItemClicked - Two pane");
			
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			
			Fragment oldDetail = fm.findFragmentById(R.id.detailFragmentContainer);
			
			// Remove the old detailFragment if one exists.
			if (oldDetail != null)
			{
				ft.remove(oldDetail);
			}
			
			// Add a new detailFragment for the passed-in customer.
			if (room != null)
			{
				Fragment newDetail = EstimateContentFragment.newInstance(mCustomerId, room);
				ft.add(R.id.detailFragmentContainer, newDetail);
			}
			
			// Commit the FragmentTransaction.
			ft.commit();
		}
		else // Single-pane
		{
			// Start the EstimateFragment.
			//Intent i = new Intent(this, EstimateActivity.class);
			//i.putExtra(EstimateFragment.EXTRA_CUSTOMER_ID, mCustomerId);
			//i.putExtra(EstimateFragment.EXTRA_ROOM, room);
			//startActivity(i);
		}
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

	@Override
	public void onActionMode()
	{
		// TODO Auto-generated method stub
		
	}
}