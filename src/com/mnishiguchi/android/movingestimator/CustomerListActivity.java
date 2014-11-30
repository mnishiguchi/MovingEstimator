package com.mnishiguchi.android.movingestimator;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class CustomerListActivity extends SingleFragmentActivity implements
		CustomerListFragment.ListCallbacks,
		CustomerDetailFragment.DetailCallbacks
{
	@Override
	protected Fragment createFragment()
	{
		// Return an instance of the fragment that the activity is hosting. 
		return new CustomerListFragment();
	}
	
	@Override
	protected int getLayoutResId()
	{
		// an alias resource defined in res/values/refs.xml
		return R.layout.activity_masterdetail; 
	}

	@Override
	public void onListItemClicked(Customer customer)
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
			if (customer != null)
			{
				Fragment newDetail = CustomerDetailFragment.newInstance(customer.getId());
				ft.add(R.id.detailFragmentContainer, newDetail);
			}
			
			// Commit the FragmentTransaction.
			ft.commit();
		}
		else // Single-pane
		{
			// Start the CrimePagerActivity.
			Intent i = new Intent(this, CustomerPagerActivity.class);
			i.putExtra(CustomerDetailFragment.EXTRA_CUSTOMER_ID, customer.getId());
			startActivity(i);
		}
	}

	@Override
	public void onListItemsDeleted(Customer[] selectedCustomers)
	{
		// Update the listView.
		FragmentManager fm = getSupportFragmentManager();
		CustomerListFragment listFragment = (CustomerListFragment)
				fm.findFragmentById(R.id.fragmentContainer);
		listFragment.updateListView();
	}

	@Override
	public void onActionMode()
	{
		removeDetailFragment();
	}
	
	private void removeDetailFragment()
	{
		// Remove the detail pane, if any.
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		Fragment oldDetail = fm.findFragmentById(R.id.detailFragmentContainer);
		
		if (oldDetail != null)
		{
			ft.remove(oldDetail);
		}

		ft.commit();
	}

	@Override
	public void onCustomerAdded(Customer customer)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCustomerUpdated(Customer customer)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCustomerDeleted(Customer customer)
	{
		// TODO Auto-generated method stub
		
	}
}
