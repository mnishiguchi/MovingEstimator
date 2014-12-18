package com.mnishiguchi.android.movingestimator;

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

import com.mnishiguchi.android.movingestimator.EstimateContract.EstimateTable;

public class EstimateListFragment extends Fragment implements
	AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener
{
	private static final String TAG = "movingestimator.EstimateListFragment";
	
	private static final String DIALOG_ADD_ITEM = "addItemDialog";
	
	public static final String EXTRA_CUSTOMER_ID = "com.mnishiguchi.android.movingestimator.id";
	public static final String EXTRA_ROOM = "com.mnishiguchi.android.movingestimator.room";
	
	//private static String sCustomerId;
	private String mRoom;
	
	// listView.
	private ListView mListView;
	private SimpleCursorAdapter mAdapter;
	
	// Remember the last click.
	private int mClickedPosition = 0; // Default => 0
	
	// Remember the ActionMode.
	private ActionMode mActionMode;
	
	/**
	 * Creates a new fragment instance and set the specified id as fragment's arguments.
	 */
	public static EstimateListFragment newInstance(String room)
	{
		// Prepare arguments.
		Bundle args = new Bundle();  // Contains key-value pairs.
		args.putString(EXTRA_ROOM, room);
		
		// Creates a fragment instance and sets its arguments.
		EstimateListFragment fragment = new EstimateListFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// Retrieve the arguments.
		mRoom = getArguments().getString(EXTRA_ROOM);
		
		// If a parent activity is registered in the manifest file, enable the Up button.
		setupActionBarUpButton();
		
		// Enable the options menu callback.
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState)
	{
		// Get reference to the layout.
		View v = inflater.inflate(R.layout.fragment_estimatelist, parent, false);

		// Configure the listView.
		mListView = (ListView)v.findViewById(R.id.listViewEstimateTable);
		mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mListView.setEmptyView(v.findViewById(R.id.estimatelist_empty));
		ViewGroup header = (ViewGroup) inflater.inflate(R.layout.header_estimate, mListView, false);
		
		// Note: The header becomes the position zero.
		mListView.addHeaderView(header, null, false);
		
		String[] columns = {
				EstimateTable.COLUMN_NAME,
				EstimateTable.COLUMN_SIZE,
				EstimateTable.COLUMN_QUANTITY,
				EstimateTable.COLUMN_SUBTOTAL,
				EstimateTable.COLUMN_MODE,
				EstimateTable.COLUMN_COMMENT,
				EstimateTable._ID
		};
		
		int[] columnsLayout = {
				R.id.textViewListItemEstimateName,
				R.id.textViewListItemEstimateSize,
				R.id.textViewListItemEstimateQuantity,
				R.id.textViewListItemEstimateSubtotal,
				R.id.textViewListItemEstimateMode,
				R.id.textViewListItemEstimateComment
		};
		
		mAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.listitem_estimate, // layout
				null,  // cursor
				columns, // column names
				columnsLayout, 0); // columns layout
				
		mListView.setAdapter(mAdapter);
		
		// Retrieve data from database.
		EstimateDataManager.get(getActivity())
			.retrieveDataForRoom(Customer.getCurrentCustomer().getId(), mRoom, this);
		EstimateDataManager.get(getActivity()).closeDatabase();
		
		// Respond to short clicks for proceeding to estimate.
		mListView.setOnItemClickListener(this);
		
		// Respond to long clicks for contexual action.
		mListView.setOnItemLongClickListener(this);
		
		// Return the root view.
		return v;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		// Set the new page's title.
		getActivity().setTitle(Customer.getCurrentCustomer().toString() + " | " + mRoom);
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
			inflater.inflate(R.menu.context_estimatecontent, menu);
			return true;
		}
				
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu)
		{
			mode.setTitle(R.string.deleting_checked_item);
			return false;
		}
		
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item)
		{
			switch (item.getItemId())
			{
				case R.id.contextmenu_delete_estimateitem: // Delete menu item.
					
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
		// Ignore the long click if already in the ActionMode.
		if (mActionMode != null) return false;
		
		// Set the list item checked.
		mListView.setItemChecked(position, true);
		
		// Remember the selected position
		mClickedPosition = position;
		//Log.d(TAG, "onItemClick() - position: " + mClickedPosition);
		
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
		EstimateDataManager.get(getActivity()).deleteSingleRow(rowId);
		
		// Re-query to refresh the CursorAdapter.
		EstimateDataManager.get(getActivity())
			.retrieveDataForRoom(Customer.getCurrentCustomer().getId(), mRoom, this);
		
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
		inflater.inflate(R.menu.fragment_estimatelist, menu);
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
				return true; // no further processing is necessary.

			case R.id.optionsmenu_new_item:
				
				// Show the delete dialog.
				new AddItemDialog().show(getFragmentManager(), DIALOG_ADD_ITEM);
				return true; // no further processing is necessary.
				
			default:
				return super.onOptionsItemSelected(item);
	 	}
	}
	
	/**
	 * Insert into database the specified item.
	 * Update the listView's UI.
	 */
	private void addEstimateItem(EstimateItem item)
	{
		EstimateDataManager manager = EstimateDataManager.get(getActivity());
		
		// Insert this item to database.
		manager.insertItem(item);
		
		// Re-query to refresh the CursorAdapter.
		EstimateDataManager.get(getActivity())
			.retrieveDataForRoom(Customer.getCurrentCustomer().getId(), mRoom, this);
		
		// Close database.
		manager.closeDatabase();
	}
	
	/**
	 * Refresh the CursorAdapter.
	 */
	public void refreshCursorAdapter(Cursor cursor)
	{
		mAdapter.changeCursor(cursor);
	}
	
	/**
	 * Add the selected item to the estimate.
	 */
	class AddItemDialog extends DialogFragment
	{
		private AutoCompleteTextView mAutoCompleteItemName;
		private EditText mEditTextSize;
		private EditText mEditTextQuantity;
		private Spinner mSpinnerMode;
		private EditText mEditTextComment;
		
		// For validation.
		private String mName;
		private double mSize;
		private int mQuantity;
		
		// The room list for autoComplete.
		private final String[] mMovingItems = EstimateListFragment.this.getActivity()
				.getResources().getStringArray(R.array.moving_items);
		
		/*
		 * Configure the dialog.
		 */
		@SuppressLint("InflateParams")
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			// Define the response to buttons.
			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
			{ 
				public void onClick(DialogInterface dialog, int which) 
				{ 
					switch (which) 
					{ 
						case DialogInterface.BUTTON_POSITIVE:
							
							// Validation for the input.
							if (!isInputValid())
							{
								Utils.showToast(getActivity(),
										getActivity().getString(R.string.invalid_room));
								return;
							}
							
							// Prepare the user-inputed data.
							EstimateItem item = new EstimateItem(
								Customer.getCurrentCustomer().getId(),
								mName,       // validated
								mSize,       // validated
								mQuantity,   // validated
								mSize * mQuantity,
								mRoom,       // from parent fragment
								mSpinnerMode.getSelectedItem().toString(),
								mEditTextComment.getText().toString()
							);
							
							// Delegate it to addMovingItem method.
							EstimateListFragment.this.addEstimateItem(item);
							break; 
							
						case DialogInterface.BUTTON_NEGATIVE: 
							// do nothing 
							break; 
					} 
				}
			};
			
			// Inflate the dialog's root view.
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View v = inflater.inflate(R.layout.dialog_additem, null);
			
			// Configure the AutoCompleteTextView.
			mAutoCompleteItemName = (AutoCompleteTextView)v.findViewById(R.id.AutoCompleteTextViewMovingItems);
			mAutoCompleteItemName.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_dropdown_item_1line,
					this.mMovingItems));
			
			// Configure the Spinner.
			mSpinnerMode = (Spinner)v.findViewById(R.id.spinnerTransportMode);
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
					getActivity(),
					R.array.transport_modes, // a string-array defined in res/values/strings.xml
					android.R.layout.simple_spinner_item); // the default layout
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
			mSpinnerMode.setAdapter(adapter);
			
			// The others widgets.
			mEditTextSize = (EditText)v.findViewById(R.id.editTextItemSize);
			mEditTextQuantity = (EditText)v.findViewById(R.id.editTextItemQuantity);
			mEditTextComment = (EditText)v.findViewById(R.id.editTextItemComment);
			
			// Create and return a dialog.
			return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.add_new_item)
				.setView(v)
				.setPositiveButton(R.string.add, listener)
				.setNegativeButton(android.R.string.cancel, listener)
				.create();
		}
		
		/**
		 * Validate the user's input and store data in instance variables.
		 */
		private boolean isInputValid()
		{
			// Check that size and quantity are in correct format.
			try
			{
				this.mSize = Double.parseDouble(mEditTextSize.getText().toString());
				this.mQuantity = Integer.parseInt(mEditTextQuantity.getText().toString());
			}
			catch(NumberFormatException e)
			{
				Log.e(TAG, "isInputValid() - ", e);
				return false;
			}
			
			// Check that name is filled out.
			this.mName = mAutoCompleteItemName.getText().toString();
			return !mName.equals("");
		}
		
		@Override
		public void onPause()
		{
			// Close the dialog as soon as the device orientation changes.
			dismiss();
			super.onPause();
		}
	}

}
