package com.mnishiguchi.android.movingestimator;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class EstimateModeListFragment extends Fragment implements
	AdapterView.OnItemClickListener
{
	private static final String TAG =
			"movingestimator.EstimateOverviewModeListFragment";
	
	// listView.
	private ListView mListView;
	private ArrayAdapter<String> mAdapter;
	
	// Remember the last click.
	private int mClickedPosition = 0; // Default => 0
	
	// Remember the ActionMode.
	private ActionMode mActionMode;
	
	// remember the reference to the hosting activity for callbacks.
	private ListCallbacks mCallbacks;
		
	/**
	 * Required interface for hosting activities.
	 */
	public interface ListCallbacks
	{
		void onListItemClicked(String room);
		void onListItemDeleted(String room);
		void onListReset();
	}
		
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
		// Ensure that the hosting activity has implemented the callbacks.
		try
		{
			mCallbacks = (ListCallbacks)activity;
		}
		catch (ClassCastException e)
		{
			throw new ClassCastException(activity.toString() +
					" must implement " + getClass().getSimpleName() + ".Callbacks");
		}
	}
		
	@Override
	public void onDetach()
	{
		super.onDetach();
		mCallbacks = null;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreate");
		
		super.onCreate(savedInstanceState);
	
		// Notify the FragmentManager that this fragment needs to receive options menu callbacks.
		setHasOptionsMenu(true);
	
		
		// TODO check if estimate is empty.
		Customer customer = Customer.getCurrentCustomer();

		
		// If a parent activity is registered in the manifest file, enable the Up button.
		setupActionBarUpButton();
		
		// Retain this fragment.
		setRetainInstance(true);
	}
	
	// Note:
	// ListFragments come with a default onCreateView() method.
	// The default implementation of a ListFragment inflates a layout that
	// defines a full screen ListView.
	
	// Note:
	// Manually create a listView because <ListView android:id="@android:id/list"... />
	// cannot be used twice.
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState)
	{
		// Inflate a custom layout with list & empty.
		View v = inflater.inflate(R.layout.fragment_modelist, parent, false);
	
		// Configure the listView.
		mListView = (ListView)v.findViewById(R.id.modelist);
		mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		mAdapter = new ArrayAdapter<String> (getActivity(),
				android.R.layout.simple_list_item_single_choice,
				getActivity().getResources().getStringArray(R.array.transport_modes)); // the data source
		mListView.setAdapter(mAdapter);
		
		// Respond to short clicks for proceeding to estimate.
		mListView.setOnItemClickListener(this);
		
		// Return the root view.
		return v;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		// If a parent activity is registered in the manifest file, enable the Up button.
		setupActionBarUpButton();
		
		// Set the customer name on the Actionbar.
		getActivity().setTitle(Customer.getCurrentCustomer().toString() + " | ESTIMATE");
		
		// Reload the list.
		mAdapter.notifyDataSetChanged();
		
		clearListSelection();
	}
	
	private void setActionBarTitle(String mode)
	{
		getActivity().setTitle(Customer.getCurrentCustomer().toString() + " | " + mode);
	}
	
	/**
	 * If a parent activity is registered in the manifest file,
	 * enable the Up button.
	 */
	private void setupActionBarUpButton()
	{
		if (NavUtils.getParentActivityIntent(getActivity()) != null)
		{
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		else
		{
			Log.d(TAG, "Couldn't enable the Up button");
		}
	}
	
	/*
	 * Respond to a short click on a list item.
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		// Set the list item checked.
		mListView.setItemChecked(position, true);
	
		// Get the selected mode.
		final String clickedMode = (String)parent.getItemAtPosition(position);
		
		// Remember the selected position
		mClickedPosition = position;
		
		// Set the room on the Actionbar subtitle.
		setActionBarTitle(clickedMode);
		
		// Notify the hosting Activity.
		mCallbacks.onListItemClicked(clickedMode);
	}
	
	/* Options Menu on the ActionBar.
	 * Creates the options menu and populates it with the items
	 * defined in res/menu/fragment_customerlist.xml.
	 * The setHasOptionsMenu(boolean hasMenu) must be called in the onCreate.
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		
		// Inflate the menu; this adds items to the action bar.
		//inflater.inflate(R.menu.fragment_modelist, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			// Respond to the enabled Up icon as if it were an existing options menu item.
			case android.R.id.home:
				
				// If a parent activity is registered in the manifest file, move up the app hierarchy.
				if (NavUtils.getParentActivityName(getActivity()) != null)
				{
					NavUtils.navigateUpFromSameTask(getActivity());
				}
				return true; // Indicate that no further processing is necessary.
			
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Update the listView's UI based on the updated list of the adapter.
	 */
	void updateListView()
	{
		mAdapter.notifyDataSetChanged();
	}
	
	/**
	 * Set the last list item selected.
	 */
	void clearListSelection()
	{
		getActivity().getActionBar().setSubtitle(null);
		mListView.clearChoices();
		mAdapter.notifyDataSetChanged();
	}
}
