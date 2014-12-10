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

public class EstimateListFragment extends Fragment implements
	AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener
{
	private static final String TAG = "movingestimator.EstimateTableFragment";
	
	private static final String DIALOG_ADD_ITEM = "addItemDialog";
	
	public static final String EXTRA_CUSTOMER_ID = "com.mnishiguchi.android.movingestimator.id";
	public static final String EXTRA_ROOM = "com.mnishiguchi.android.movingestimator.room";
	
	private String mCustomerId;
	private String mRoom;
	
	// listView.
	private ListView mListView;
	private SimpleCursorAdapter mAdapter;
	//private Cursor mCursor;
	
	// Remember the last click.
	private int mClickedPosition = 0; // Default => 0
	
	// Remember the ActionMode.
	private ActionMode mActionMode;
	
	/**
	 * Creates a new fragment instance and set the specified id as fragment's arguments.
	 */
	public static EstimateListFragment newInstance(String customerId, String room)
	{
		// Prepare arguments.
		Bundle args = new Bundle();  // Contains key-value pairs.
		args.putString(EXTRA_CUSTOMER_ID, customerId);
		args.putString(EXTRA_ROOM, room);
		
		// Creates a fragment instance and sets its arguments.
		EstimateListFragment fragment = new EstimateListFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	/**
	 * Private constructor.
	 */
	//private EstimateTableFragment()
	//{ }
		
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// Retrieve the arguments.
		mCustomerId = getArguments().getString(EXTRA_CUSTOMER_ID);
		mRoom = getArguments().getString(EXTRA_ROOM);

		Log.d(TAG, "onCreate() - mRoom: " + mRoom);
		
		// Enable the options menu callback.
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreateView()");
		
		// Get reference to the layout.
		View v = inflater.inflate(R.layout.fragment_estimatelist, parent, false);
		
		// If a parent activity is registered in the manifest file, enable the Up button.
		setupActionBarUpButton();

		// Configure the listView.
		mListView = (ListView)v.findViewById(R.id.listViewEstimateTable);
		mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mListView.setEmptyView(v.findViewById(R.id.estimatelist_empty));
		ViewGroup header = (ViewGroup) inflater.inflate(R.layout.header_estimate, mListView, false);
		
		// Note: The header becomes the position zero.
		mListView.addHeaderView(header, null, false);
		
		String[] columns = {
				EstimateContract.EstimateTable.COLUMN_ITEM_NAME,
				EstimateContract.EstimateTable.COLUMN_ITEM_SIZE,
				EstimateContract.EstimateTable.COLUMN_QUANTITY,
				EstimateContract.EstimateTable.COLUMN_TRANSPORT_MODE,
				EstimateContract.EstimateTable._ID,
				EstimateContract.EstimateTable.COLUMN_COMMENT // TODO
		};
		
		int[] columnsLayout = {
				R.id.textViewListItemEstimateItemName,
				R.id.textViewListItemEstimateSize,
				R.id.textViewListItemEstimateQuantity,
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
		EstimateManager.get(getActivity())
			.retrieveDataForRoom(mCustomerId, mRoom, this);
		
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
	
	/*
	 * Have the photo ready as soon as this Fragment's view becomes visible to the user.
	 */
	@Override
	public void onStart()
	{
		super.onStart();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		Log.d(TAG, "onResume()");
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		Log.d(TAG, "onPause()");
	}
	
	/*
	 * Unload the photo as soon as this Fragment's view becomes invisible to the user.
	 */
	@Override
	public void onStop()
	{
		super.onStop();
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
			inflater.inflate(R.menu.context_estimatecontent, menu);
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
	 * Remove the Contextual Action Bar if any.
	 */
	void finishCAB()
	{
		if (mActionMode != null) 
		{
			mActionMode.finish();
			mActionMode = null;
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
		boolean success = EstimateManager.get(getActivity()).deleteSingleRow(rowId);
		Log.d(TAG, "deleteEstimateItem() - success: " + success);
		
		// Re-query to refresh the CursorAdapter.
		EstimateManager.get(getActivity())
			.retrieveDataForRoom(mCustomerId, mRoom, this);
		
		// Close database.
		EstimateManager.get(getActivity()).closeDatabase();
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

			case R.id.optionsmenu_new_item:
				
				// Show the delete dialog.
				new AddItemDialog().show(getFragmentManager(), DIALOG_ADD_ITEM);
				return true; // Indicate that no further processing is necessary.
				
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
		EstimateManager manager = EstimateManager.get(getActivity());
		
		// Insert this item to database.
		manager.insertItem(mCustomerId, item);
		
		// Re-query to refresh the CursorAdapter.
		EstimateManager.get(getActivity())
			.retrieveDataForRoom(mCustomerId, mRoom, this);
		
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
							
							// TODO Validation for the input.
							if (!isInputValid())
							{
								Utils.showToast(getActivity(), "Invalid input");
								return;
							}
							
							// Prepare the user-inputted data.
							EstimateItem item = new EstimateItem(
								mCustomerId, // from parent fragment
								mName,       // validated
								mSize,       // validated
								mQuantity,   // validated
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
				.setTitle("Adding an item")
				.setView(v)
				.setPositiveButton("Add", listener)
				.setNegativeButton("Cancel", listener)
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
