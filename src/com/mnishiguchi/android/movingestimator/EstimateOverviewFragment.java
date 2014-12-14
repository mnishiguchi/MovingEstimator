package com.mnishiguchi.android.movingestimator;

import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SimpleCursorAdapter;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.mnishiguchi.android.movingestimator.EstimateContract.EstimateTable;

public class EstimateOverviewFragment extends Fragment implements
	AdapterView.OnItemClickListener
{
	private static final String TAG = "movingestimator.EstimateOverviewFragment";
	
	// listView.
	private ListView mListView;
	private SimpleCursorAdapter mAdapter;
	private TextView mFooterText;
	private Spinner mSpinner;
	
	// Remember the last click.
	private int mClickedPosition = 0; // Default => 0
	
	// Remember the ActionMode.
	private ActionMode mActionMode;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// If a parent activity is registered in the manifest file, enable the Up button.
		setupActionBarUpButton();
		
		// Enable the options menu callback.
		setHasOptionsMenu(true);
		
		// Set the action bar title.
		getActivity().setTitle(Customer.getCurrentCustomer().toString() + " | ESTIMATE");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreateView()");
		
		// Get reference to the layout.
		View v = inflater.inflate(R.layout.fragment_estimate_overview, parent, false);
	
		// Configure the listView.
		mListView = (ListView)v.findViewById(R.id.listViewEstimateOverview);
		mListView.setEmptyView(v.findViewById(R.id.estimate_overview_empty));
		
		// Header and footer
		// Note: The header becomes the position zero.
		ViewGroup header = (ViewGroup) inflater.inflate(R.layout.header_estimate_overview, mListView, false);
		mListView.addHeaderView(header, null, false);
		ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.footer_estimate_overview, mListView, false);
		mListView.addFooterView(footer, null, false);
		
		mFooterText = (TextView)footer.findViewById(R.id.textViewFooterEstimateOverview);
		
		
		//--- Set up the list adapter ---
		
		String[] columns = {
				EstimateTable.COLUMN_NAME,
				EstimateTable.COLUMN_SIZE,
				EstimateTable.COLUMN_QUANTITY,
				EstimateTable.COLUMN_SUBTOTAL,
				EstimateTable.COLUMN_ROOM,
				EstimateTable.COLUMN_COMMENT,
				EstimateTable._ID,
		};
		
		int[] columnsLayout = {
				R.id.textViewListItemEstimateOverviewName,
				R.id.textViewListItemEstimateOverviewSize,
				R.id.textViewListItemEstimateOverviewQuantity,
				R.id.textViewListItemEstimateOverviewSubtotal,
				R.id.textViewListItemEstimateOverviewRoom,
				R.id.textViewListItemEstimateOverviewComment
		};
		
		mAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.listitem_estimate_overview, // layout file
				null,  // cursor
				columns, // column names
				columnsLayout, 0) { // columns layout
		
			// Disable clicks on the list item.
			public boolean isEnabled(int position) 
			{ 
				return false; 
			} 
		};
		mListView.setAdapter(mAdapter);
		
		// Respond to short clicks for proceeding to estimate.
		mListView.setOnItemClickListener(this);
		
		//--- Spinner ---
		
		mSpinner = (Spinner)v.findViewById(R.id.spinnerEstimateOverview);
		
		// Retrieve data from database for spinner.
		// And set it up.
		EstimateDataManager.get(getActivity()).retrieveModesForCustomer(
				Customer.getCurrentCustomer().getId(),
				this);

		// Return the root view.
		return v;
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		EstimateDataManager.get(getActivity()).closeDatabase();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		// Remember the selected position
		mClickedPosition = position;
		Log.d(TAG, "onItemClick() - position=>" + mClickedPosition);
	}
	
	/**
	 * Creates the options menu and populates it with the items defined
	 * in res/menu/fragment_XXX.xml.
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		
		// Inflate the menu, adding menu items to the action bar.
		inflater.inflate(R.menu.fragment_estimate_overview, menu);
	}
	
	/**
	 * Respond to menu selection.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Check the selected menu item and respond to it.
		switch (item.getItemId() )
		{
			// Respond to the enabled Up icon as if it were an existing options menu item.
			case android.R.id.home:
				
				// If a parent activity is registered in the manifest file, move up the app hierarchy.
				if (NavUtils.getParentActivityName(getActivity()) != null)
				{
					NavUtils.navigateUpFromSameTask(getActivity());
				}
				return true; // Indicate that no further processing is necessary.
	
			case R.id.optionsmenu_edit_estimate:
				
				// Proceed to estimate editing.
				Intent i = new Intent(getActivity(), EstimateRoomListActivity.class);
				startActivity(i);
				return true; // no further processing is necessary.
			
			default:
				return super.onOptionsItemSelected(item);
	 	}
	}
	
	/**
	 * Invoked after query is performed.
	 * Set up the spinner based on the query result.
	 */
	public void setupSpinner(Cursor cursor)
	{
		Log.d(TAG, "setupSpinner(), cursor.getCount()=>" + cursor.getCount());
		ArrayList<String> modes = new ArrayList<String>();
		
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false)
		{
			Log.d(TAG, "setupSpinner(), cursor.getString(0)=>" + cursor.getString(0));
			modes.add(cursor.getString(0));
			cursor.moveToNext();
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				getActivity(),
				android.R.layout.simple_spinner_item, // layout file
				modes); // columns layout
		
		// Set the dropdown layout.
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		
		// Apply the adapter to the spinner
		mSpinner.setAdapter(adapter);
		
		// Set the listener.
		mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id)
			{
				String mode = (String) mSpinner.getItemAtPosition(position);
				
				// Retrieve data from database for the listView.
				EstimateDataManager.get(getActivity()).retrieveDataForMode(
						Customer.getCurrentCustomer().getId(),
						mode,
						EstimateOverviewFragment.this);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{ } // Required, but not used in this implementation.
		});
	}
	
	/**
	 * Refresh the CursorAdapter.
	 */
	public void refreshListView(Cursor cursor)
	{
		Log.d(TAG, "refreshCursorAdapter()");
		mAdapter.changeCursor(cursor);
		
		mFooterText.setText("" + getVolume(cursor));
	}
	

	private double getVolume(Cursor cursor)
	{
		double volume = 0;
		
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast())
		{
			volume += cursor.getDouble(4);
			
			cursor.moveToNext();
		}
		
		return volume;
	}

	/**
	 * If a parent activity is registered in the manifest file,
	 * enable the Up button.
	 */
	private void setupActionBarUpButton()
	{
		if (NavUtils.getParentActivityIntent(getActivity() ) != null)
		{
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		else
		{
			Log.d(TAG, "Couldn't enable the Up button");
		}
	}

}
