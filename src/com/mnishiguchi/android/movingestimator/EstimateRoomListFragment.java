package com.mnishiguchi.android.movingestimator;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

public class EstimateRoomListFragment extends Fragment implements
	AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener
{
	private static final String TAG = "movingestimator.EstimateRoomListFragment";
	
	private static final String DIALOG_ADD_ROOM = "addRoomDialog";

	// Reference to the customer stored in FileCabinet.
	private Customer mCustomer;
	
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
		void onActionMode();
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
		
		// Get the customer with that id.
		mCustomer = Customer.getCurrentCustomer();
	
		// Notify the FragmentManager that this fragment needs to receive options menu callbacks.
		setHasOptionsMenu(true);
	
		// If the customer's roomlist is empty, fill it with the default.
		if (mCustomer.getRooms().isEmpty())
		{
			// Create a new list with the default rooms.
			ArrayList<String> rooms = new ArrayList<String>();
			String[] defaultRooms = getActivity().getResources().getStringArray(R.array.rooms_default);
			Log.d(TAG, "getStringArray(R.array.rooms_default).length: " + defaultRooms.length);
			
			for (String each : defaultRooms)
			{
				rooms.add(each);
			}
			
			// Save the updated entire customers data to disk.
			mCustomer.setRooms(rooms);
			FileCabinet.get(getActivity()).saveCustomers();
		}
		
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
		View v = inflater.inflate(R.layout.fragment_roomlist, parent, false);
	
		// Configure the listView.
		mListView = (ListView)v.findViewById(R.id.roomlist);
		mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		mAdapter = new ArrayAdapter<String> (getActivity(),
				android.R.layout.simple_list_item_single_choice,
				mCustomer.getRooms()); // the data source
		mListView.setAdapter(mAdapter);
		
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
		
		// If a parent activity is registered in the manifest file, enable the Up button.
		setupActionBarUpButton();
		
		// Set the customer name on the Actionbar.
		getActivity().setTitle(mCustomer.toString() + " | ESTIMATE | EDIT");
		
		// Reload the list.
		mAdapter.notifyDataSetChanged();
		
		clearListSelection();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		Log.d(TAG, "onPause()");
		
		if (mActionMode != null)
		{
			mActionMode.finish();
			mActionMode = null;
		}
		
	}
	
	private void setActionBarTitle(String room)
	{
		getActivity().setTitle(mCustomer.toString() + " | " + room);
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

		// Get the selected room.
		final String clickedRoom = (String)parent.getItemAtPosition(position);
		
		// Remember the selected position
		mClickedPosition = position;
		
		// Set the room on the Actionbar subtitle.
		//setRoomOnActionBar(clickedRoom);
		setActionBarTitle(clickedRoom);
		
		// Notify the hosting Activity.
		mCallbacks.onListItemClicked(clickedRoom);
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
			inflater.inflate(R.menu.context_roomlist, menu);
			return true;
		}
				
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu)
		{
			mode.setTitle("Deleting the checked room");
			return false;
		}
		
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item)
		{
			switch (item.getItemId())
			{
				case R.id.contextmenu_delete_room: // Delete menu item.
					
					// Retrieve the selected room's position.
					deleteRoom();

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
		
		// Show the Contexual Action Bar.
		getActivity().startActionMode(actionModeCallback);

		return true; // Long click was consumed.
	}
	
	@SuppressLint("NewApi")
	private void deleteRoom()
	{
		// The clicked room.
		String room = (String)mAdapter.getItem(mClickedPosition);

		// Remove the data from model layer.
		mCustomer.getRooms().remove(room);

		// Update the listView.
		mAdapter.notifyDataSetChanged();
		
		// Save the updated entire customers data to disk.
		FileCabinet.get(getActivity()).saveCustomers();
		
		// Delete this room's estimate data from database.
		EstimateDataManager.get(getActivity()).deleteRoom(mCustomer.getId(), room);
		
		clearListSelection();
		
		// Notyfy the hosting activity.
		mCallbacks.onListItemDeleted(room);
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
		inflater.inflate(R.menu.fragment_roomlist, menu);
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
			
			// --- NEW ROOM ---
			case R.id.optionsmenu_new_room:
				
				new AddRoomDialog().show(
						getActivity().getSupportFragmentManager(), DIALOG_ADD_ROOM);
				
				return true;  // No further processing is necessary.
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Add the specified room to this customer's attributes.
	 * Update the listView and save the whole customer data to disk.
	 * @param room
	 */
	private void addRoom(String room)
	{
		Log.d(TAG, "addRoom -" + room);
		
		// Access the customer's room list and add a new room.
		mCustomer.getRooms().add(room);
		
		// Update the listView.
		mAdapter.notifyDataSetChanged();
		
		// Save the updated entire customers data to disk.
		FileCabinet.get(getActivity()).saveCustomers();
		
		setLastItemSelected();
	}
	
	/**
	 * Set the last list item selected.
	 */
	void setLastItemSelected()
	{
		int lastIndex = mAdapter.getCount() - 1;
		mListView.setItemChecked(lastIndex, true);
		getActivity().getActionBar().setSubtitle(mAdapter.getItem(lastIndex));
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
	
	/**
	 * Add the selected room to the room list.
	 */
	class AddRoomDialog extends DialogFragment
	{
		private AutoCompleteTextView mTextView;
		
		// The room list for autoComplete.
		private final String[] mRooms = EstimateRoomListFragment.this.getActivity()
				.getResources().getStringArray(R.array.rooms);
		
		// For validation.
		private String mRoomText;
		
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
							
							// Do nothing if the room is invalid.
							mRoomText = mTextView.getText().toString();
							if (!isRoomValid())
							{
								Utils.showToast(getActivity(), "Invalid room");
								return;
							}
							
							// Add it to the list.
							EstimateRoomListFragment.this.addRoom(mRoomText);
							break; 
							
						case DialogInterface.BUTTON_NEGATIVE: 
							// do nothing 
							break; 
					} 
				}
			};
			
			// Inflate the dialog's root view.
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View v = inflater.inflate(R.layout.dialog_addroom, null);
			
			// Configure the AutoCompleteTextView.
			mTextView = (AutoCompleteTextView)v.findViewById(R.id.AutoCompleteTextViewRooms);
			mTextView.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_dropdown_item_1line,
					this.mRooms));
			
			// Create and return a dialog.
			return new AlertDialog.Builder(getActivity())
				.setTitle("Adding a room")
				.setView(v)
				.setPositiveButton("Add", listener)
				.setNegativeButton("Cancel", listener)
				.create();
		}
		
		/**
		 * Check if the room name is valid.
		 */
		private boolean isRoomValid()
		{
			// Check for duplicates.
			for (String room : mCustomer.getRooms())
			{
				if (room.equalsIgnoreCase(mRoomText))
				{
					return false;
				}
			}
			return true;
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
