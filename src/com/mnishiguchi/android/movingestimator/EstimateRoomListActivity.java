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
	
	// Remember the current customer id as a static field.
	private String mCurrentCustomerId;
	
	@Override
	protected Fragment createFragment()
	{
		mCurrentCustomerId = Customer.getCurrentCustomer().getId();
		Log.d(TAG, "mCustomerId: " + mCurrentCustomerId);
		
		return new EstimateRoomListFragment();
	}

	@Override
	protected int getLayoutResId()
	{
		// an alias resource defined in res/values/refs.xml
		return R.layout.activity_masterdetail_2; 
	}

	@Override
	public void onPause()
	{
		super.onPause();
		mCurrentCustomerId = Customer.getCurrentCustomer().getId();
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
				Fragment newDetail = EstimateListFragment.newInstance(room);
				ft.add(R.id.detailFragmentContainer, newDetail);
			}
			
			// Commit the FragmentTransaction.
			ft.commit();
		}
		else // Single-pane
		{
			// Start the EstimatePagerActivity..
			Intent i = new Intent(this, EstimatePagerActivity.class);
			i.putExtra(EstimatePagerActivity.EXTRA_ROOM, room);
			startActivity(i);
		}
	}

	@Override
	public void onListItemDeleted(String room)
	{
		// Clear the detailFragmentContainer.
		removeEstimateListFragment();
		
		EstimateRoomListFragment listFragment = getListFragment();
		
		// Clear the selection.
		listFragment.clearListSelection();
		
		// Update the listView
		listFragment.updateListView();
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
	
	private void removeEstimateListFragment()
	{
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		Fragment oldDetail = fm.findFragmentById(R.id.detailFragmentContainer);
		
		if (oldDetail != null)
		{
			ft.remove(oldDetail);
		}

		ft.commit();
	}
	
	private EstimateRoomListFragment getListFragment()
	{
		FragmentManager fm = getSupportFragmentManager();
		return (EstimateRoomListFragment)fm.findFragmentById(R.id.fragmentContainer);
	}
}