package com.mnishiguchi.android.movingestimator;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class EstimateRoomListActivity extends SingleFragmentActivity implements
	EstimateRoomListFragment.ListCallbacks
{
	//private static final String TAG = "movingestimator.RoomListActivity";
	
	@Override
	protected Fragment createFragment()
	{
		return new EstimateRoomListFragment();
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
			// Start the EstimateListActivity..
			Intent i = new Intent(this, EstimateListActivity.class);
			i.putExtra(EstimateListActivity.EXTRA_ROOM, room);
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
	{ } // Required but not used in this implementation.

	@Override
	public void onActionMode()
	{ } // Required but not used in this implementation.
	
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