package com.mnishiguchi.android.movingestimator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

public class CustomerEditActivity extends FragmentActivity
{
	private static String TAG = "movingestimator.CustomerEditActivity";
	
	private String mId;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);
		
		mId = getIntent().getStringExtra(CustomerDetailFragment.EXTRA_CUSTOMER_ID_DETAIL);
		
		Log.e(TAG, "mId: " + mId);
		
		// Note:
		// FragmentManager - Maintains a back stack of fragment transactions.
		// FragmentTransaction - Used to add, remove, attach, detach,
		// or replace fragments in the fragment list.
		
		// Get a FragmentManager
		FragmentManager fm = getSupportFragmentManager();
		
		// Get a reference to the fragment list associated with the fragment_container.
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
		
		// Check if there is already a fragment in the fragment list.
		if (fragment == null)
		{
			fragment = createFragment();  // Create a new Fragment.
			
			// Add the Fragment to the list.
			fm.beginTransaction()
					.add(R.id.fragmentContainer, fragment)
					.commit();
		}
	}

	protected Fragment createFragment()
	{
		Log.e(TAG, "createFragment()");
		return CustomerEditFragment.newInstance(mId);
	} 
}
