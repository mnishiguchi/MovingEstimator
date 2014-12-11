package com.mnishiguchi.android.movingestimator;

import android.support.v4.app.Fragment;
import android.util.Log;

public class CustomerEditActivity extends SingleFragmentActivity
{
	//private static String TAG = "movingestimator.CustomerEditActivity";

	protected Fragment createFragment()
	{
		// Retrieve the customer id from extra.
		String id = getIntent().getStringExtra(CustomerEditFragment.EXTRA_CUSTOMER_ID);
		
		return CustomerEditFragment.newInstance(id);
	}
}
