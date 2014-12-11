package com.mnishiguchi.android.movingestimator;

import com.mnishiguchi.android.movingestimator.EstimateContract.EstimateTable;
import com.mnishiguchi.android.movingestimator.EstimateListFragment.AddItemDialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

public class EstimateOverviewFragment extends Fragment implements
	AdapterView.OnItemClickListener
{
	private static final String TAG = "movingestimator.EstimateOverviewFragment";
	public static final String EXTRA_MODE = "com.mnishiguchi.android.movingestimator.mode";
	
	// listView.
	private ListView mListView;
	private SimpleCursorAdapter mAdapter;
	private String mMode;
	
	
	// Remember the last click.
	private int mClickedPosition = 0; // Default => 0
	
	// Remember the ActionMode.
	private ActionMode mActionMode;
	
	/**
	 * Creates a new fragment instance.
	 */
	public static EstimateOverviewFragment newInstance(String mode)
	{
		Log.d(TAG, "newInstance() - mode=>" + mode);
		
		// Prepare arguments.
		Bundle args = new Bundle();  // Contains key-value pairs.
		args.putString(EXTRA_MODE, mode);
		
		// Creates a fragment instance and sets its arguments.
		EstimateOverviewFragment fragment = new EstimateOverviewFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// Retrieve the arguments.
		mMode = getArguments().getString(EXTRA_MODE);
		Log.d(TAG, "onCreate() - mRoom=>" + mMode);
		
		// If a parent activity is registered in the manifest file, enable the Up button.
		setupActionBarUpButton();
		
		// Enable the options menu callback.
		setHasOptionsMenu(true);
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
		mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mListView.setEmptyView(v.findViewById(R.id.estimate_overview_empty));
		ViewGroup header = (ViewGroup) inflater.inflate(R.layout.header_estimate_overview, mListView, false);
		
		// Note: The header becomes the position zero.
		mListView.addHeaderView(header, null, false);
		
		String[] columns = {
				EstimateTable.COLUMN_ITEM_NAME,
				EstimateTable.COLUMN_ITEM_SIZE,
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
				R.layout.listitem_estimate_overview, // layout
				null,  // cursor
				columns, // column names
				columnsLayout, 0); // columns layout
				
		mListView.setAdapter(mAdapter);
		
		// Retrieve data from database.
		EstimateDataManager.get(getActivity())
			.retrieveDataForMode(Customer.getCurrentCustomer().getId(), mMode, this);
		EstimateDataManager.get(getActivity()).closeDatabase();
		
		// Respond to short clicks for proceeding to estimate.
		mListView.setOnItemClickListener(this);
		
		// Return the root view.
		return v;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id)
	{
		// Remember the selected position
		mClickedPosition = position;
		
		Log.d(TAG, "onItemClick() - position: " + mClickedPosition);
		getRowIdAtLastClickedPosition();
	}
	
	/**
	 * Clear list selection.
	 */
	void clearListSelection()
	{
		mListView.clearChoices();
		mAdapter.notifyDataSetChanged();
	}

	
	/**
	 * Adjust the cursor position by 1 because the list header takes
	 * the position 0 on the listView. 
	 */
	private long getRowIdAtLastClickedPosition()
	{
		Cursor cursor = mAdapter.getCursor();
		cursor.moveToPosition(mClickedPosition - 1); // Subtract one.
		return cursor.getLong(cursor.getColumnIndex("_id"));
	}
	
	/**
	 * If a parent activity is registered in the manifest file,
	 * enable the Up button.
	 */
	private void setupActionBarUpButton()
	{
		if (!Utils.hasTwoPane(getActivity()) &&
				NavUtils.getParentActivityIntent(getActivity() ) != null)
		{
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		else
		{
			Log.d(TAG, "Couldn't enable the Up button");
		}
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu)
	{
		super.onPrepareOptionsMenu(menu);
		
		// Show delete item only in the two-pane mode.
		// menu.findItem(R.id.optionsmenu_delete).setVisible(Utils.hasTwoPane(getActivity()));
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
		//Intent i;
		
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
	 * Refresh the CursorAdapter.
	 */
	public void refreshCursorAdapter(Cursor cursor)
	{
		mAdapter.changeCursor(cursor);
	}
}
