package com.mnishiguchi.android.movingestimator;

import android.support.v4.app.Fragment;
import android.util.Log;

public class CustomerEditActivity extends SingleFragmentActivity
{
	private static String TAG = "movingestimator.CustomerEditActivity";
	public static final String EXTRA_CUSTOMER_ID = "com.mnishiguchi.android.movingestimator.customer_id";

	protected Fragment createFragment()
	{
		Log.e(TAG, "createFragment()");
		
		String id = getIntent().getStringExtra(EXTRA_CUSTOMER_ID);
		
		return CustomerEditFragment.newInstance(id);
	} 
}
