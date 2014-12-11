package com.mnishiguchi.android.movingestimator;

import com.mnishiguchi.android.movingestimator.EstimateContract.EstimateTable;
import com.mnishiguchi.android.movingestimator.EstimateListFragment.AddItemDialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
	AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener
{
	private static final String TAG = "movingestimator.EstimateOverviewFragment";
	
	// listView.
	private ListView mListView;
	private SimpleCursorAdapter mAdapter;
	
	// Remember the last click.
	private int mClickedPosition = 0; // Default => 0
	
	// Remember the ActionMode.
	private ActionMode mActionMode;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate() - customerId=>" + Customer.getCurrentCustomer().getId());
		
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
		mListView.setEmptyView(v.findViewById(R.id.estimatelist_empty));
		ViewGroup header = (ViewGroup) inflater.inflate(R.layout.header_estimate, mListView, false);
		
		// Note: The header becomes the position zero.
		mListView.addHeaderView(header, null, false);
		
		String[] columns = {
				EstimateTable.COLUMN_ITEM_NAME,
				EstimateTable.COLUMN_ITEM_SIZE,
				EstimateTable.COLUMN_QUANTITY,
				"Subtotal",
				EstimateTable.COLUMN_TRANSPORT_MODE,
				EstimateTable.COLUMN_COMMENT,
				EstimateTable._ID,
		};
		
		int[] columnsLayout = {
				R.id.textViewListItemEstimateOverviewName,
				R.id.textViewListItemEstimateOverviewSize,
				R.id.textViewListItemEstimateOverviewQuantity,
				R.id.textViewListItemEstimateOverviewSubtotal,
				R.id.textViewListItemEstimateOverviewMode,
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
			.retrieveDataForCustomer(Customer.getCurrentCustomer().getId(), this);
		EstimateDataManager.get(getActivity()).closeDatabase();
		
		// Respond to short clicks for proceeding to estimate.
		mListView.setOnItemClickListener(this);
		
		// Respond to long clicks for contexual action.
		mListView.setOnItemLongClickListener(this);
		
		// Return the root view.
		return v;
	}
	
	/*
	 * Remove the CAB when the pager is swiped.
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser)
	{
		super.setUserVisibleHint(isVisibleToUser);
		if (!isVisibleToUser)
		{
			finishCAB();
		}
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
	
	// Long click => Contextual action for deleting room.
	final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
	
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu)
		{
			// Remember reference to action mode.
			mActionMode = mode;
		
			// Inflate the menu using a special inflater defined in the ActionMode class.
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.context_estimate_overview, menu);
			return true;
		}
				
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu)
		{
			mode.setTitle("Deleting the checked item");
			return false;
		}
		
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item)
		{
			switch (item.getItemId())
			{
				case R.id.contextmenu_delete_estimate_overview: // Delete menu item.
					
					// Retrieve the selected room's position.
					deleteEstimateItem();
	
					// Prepare the action mode to be destroyed.
					mode.finish(); // Action picked, so close the CAB
					return true;
						
				default:
					return false;
			}
		}
	
		@Override
		public void onDestroyActionMode(ActionMode mode)
		{
			// Set it to null because we exited the action mode.
			mActionMode = null;
		}
	};
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id)
	{
		Log.d(TAG, "onLongClick()");
		
		// Ignore the long click if already in the ActionMode.
		if (mActionMode != null) return false;
		
		// Set the list item checked.
		mListView.setItemChecked(position, true);
		
		// Remember the selected position
		mClickedPosition = position;
		Log.d(TAG, "onItemClick() - position: " + mClickedPosition);
		
		// Show the Contexual Action Bar.
		getActivity().startActionMode(actionModeCallback);
	
		return true; // Long click was consumed.
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
	 * Remove the Contextual Action Bar if any.
	 */
	void finishCAB()
	{
		if (mActionMode != null) 
		{
			mActionMode.finish();
			mActionMode = null;
			
			clearListSelection();
		}
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
	
	@SuppressLint("NewApi")
	private void deleteEstimateItem()
	{
		// Get the row id.
		final long rowId = getRowIdAtLastClickedPosition();
		Log.d(TAG, "deleteEstimateItem() - rowId: " + rowId);
		
		// Delete animation.
		final View view = mAdapter.getView(mClickedPosition - 1, null, mListView);
		view.animate()
			.setDuration(1000)
			.alpha (0)
			.withEndAction(new Runnable() {
				
				@Override
				public void run ()
				{
					// Make the list item disappear.
					view.setAlpha(1);
				}
			});
		
		// Delete the item from database.
		boolean success = EstimateDataManager.get(getActivity()).deleteSingleRow(rowId);
		Log.d(TAG, "deleteEstimateItem() - success: " + success);
		
		// Re-query to refresh the CursorAdapter.
		EstimateDataManager.get(getActivity())
			.retrieveDataForCustomer(Customer.getCurrentCustomer().getId(), this);
		
		// Close database.
		EstimateDataManager.get(getActivity()).closeDatabase();
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
				//TODO
				return true; // Indicate that no further processing is necessary.
				
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
