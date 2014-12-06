package com.mnishiguchi.android.movingestimator;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

public class RoomListFragment extends ListFragment
{
	private static final String TAG = "movingestimator.EstimateRoomListFragment";
	
	private static final String DIALOG_ADD_ROOM = "addRoomDialog";
	
	public static final String EXTRA_CUSTOMER_ID_ROOM = "com.mnishiguchi.android.movingestimator.customer_id_room";
	
	// Store reference to the current instance to this fragment.
	//private static EstimateRoomListFragment sEstimateRoomListFragment;
	
	// Reference to the list of rooms stored in FileCabinet.
	private Customer mCustomer;
	private ArrayList<String> mRooms;
	
	// Remember the currently selected item.
	private int mPositionSelected;
	
	// remember the reference to the hosting activity for callbacks.
	private ListCallbacks mCallbacks;
		
	/**
	 * Required interface for hosting activities.
	 */
	public interface ListCallbacks
	{
		void onListItemClicked(String room);
		void onListItemDeleted(String room);
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
			throw new ClassCastException(activity.toString()
					+ " must implement EstimateRoomListFragment.Callbacks");
		}
	}
		
	@Override
	public void onDetach()
	{
		super.onDetach();
		mCallbacks = null;
	}
	
	/**
	 * Create a new instance associated with the passed-in id.
	 */
	public static RoomListFragment newInstance(String customerId)
	{
		// Set the customer's id on the arguments.
		Bundle args = new Bundle();
		args.putString(EXTRA_CUSTOMER_ID_ROOM, customerId);
		
		// Instantiate the fragment with the arguments.
		RoomListFragment fragment = new RoomListFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	/**
	 * Private constructor so that nobody accidentally would instantiate.
	 */
	private RoomListFragment()
	{}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreate");
		
		super.onCreate(savedInstanceState);
		
		// Retrieve the passed-in customerId.
		String customerId = getArguments().getString(EXTRA_CUSTOMER_ID_ROOM);
		
		// Get the customer with that id.
		mCustomer = FileCabinet.get(getActivity()).getCustomer(customerId);
	
		// Notify the FragmentManager that this fragment needs to receive options menu callbacks.
		setHasOptionsMenu(true);
	
		// Get the list of customer's rooms.
		if (mCustomer.getRooms().isEmpty())
		{
			// Create a new list and add a set of default rooms.
			mRooms = new ArrayList<String>();
			String[] rooms = getActivity().getResources().getStringArray(R.array.rooms_default);
			Log.d(TAG, "getStringArray(R.array.rooms_default).length: " + rooms.length);
			
			for (String each : rooms)
			{
				mRooms.add(each);
			}
			
			// Save the updated entire customers data to disk.
			mCustomer.setRooms(mRooms);
			FileCabinet.get(getActivity()).saveCustomers();
		}
		else
		{
			mRooms = mCustomer.getRooms();
		}
		
		// Set the list adapter. (Default)
		setListAdapter(new ArrayAdapter<String> (getActivity(),
				android.R.layout.simple_list_item_1, // the default list item layout.
				mRooms)); // the data source
		
		// Retain this fragment.
		setRetainInstance(true);
	}
	
	// Note:
	// ListFragments come with a default onCreateView() method.
	// The default implementation of a ListFragment inflates a layout that
	// defines a full screen ListView.

	@Override
	public void onResume()
	{
		super.onResume();
		
		// If a parent activity is registered in the manifest file, enable the Up button.
		setupActionBarUpButton();
		
		// Set the customer name on the Actionbar.
		getActivity().setTitle("Estimate for " + mCustomer.toString());
		
		// Reload the list.
		((ArrayAdapter<String>)getListAdapter()).notifyDataSetChanged();
		
		if (!Utils.hasTwoPane(getActivity())) // Single=pane
		{
			clearListSelection();
		}
	}
	
	/**
	 * @return The currently selected room, "" if none selected.
	 */
	private String getCurrentRoom()
	{
		int pos = getListView().getCheckedItemPosition();
		if (pos == ListView.INVALID_POSITION)
		{
			return "";
		}
		return (String)getListView().getAdapter().getItem(pos);
	}
	
	/**
	 * If a parent activity is registered in the manifest file,
	 * enable the Up button.
	 */
	private void setupActionBarUpButton()
	{
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	/*
	 * Respond to a short click on a list item.
	 */
	@Override
	public void onListItemClick(ListView lv, View v, int position, long id)
	{
		// Get the selected item.
		String room = (String)getListAdapter().getItem(position);
		
		// Remember the selected position
		mPositionSelected = position;
		
		// Set the room on the Actionbar subtitle.
		getActivity().getActionBar().setSubtitle("Currently at: " + room);
		
		// Notify the hosting Activity.
		mCallbacks.onListItemClicked(room);
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
		// Access the customer's room list and add a new room.
		mCustomer.getRooms().add(room);
		
		// Update the listView.
		((ArrayAdapter<String>)getListAdapter()).notifyDataSetChanged();
		
		// Save the updated entire customers data to disk.
		FileCabinet.get(getActivity()).saveCustomers();
	}
	
	/**
	 * Set the last list item selected.
	 */
	void setLastItemSelected()
	{
		ArrayAdapter<String> adapter = (ArrayAdapter<String>)getListAdapter();
		int lastIndex = adapter.getCount() - 1;
		getListView().setItemChecked(lastIndex, true);
	}
	
	/**
	 * Set the last list item selected.
	 */
	void clearListSelection()
	{
		ArrayAdapter<String> adapter = (ArrayAdapter<String>)getListAdapter();
		getListView().clearChoices();
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * Add the selected room to the room list.
	 */
	class AddRoomDialog extends DialogFragment
	{
		private AutoCompleteTextView mTextView;
		
		// The room list for autoComplete.
		private final String[] mRooms = RoomListFragment.this.getActivity()
				.getResources().getStringArray(R.array.rooms);
		
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
							
							// TODO
							addRoom(mTextView.getText().toString());
							
							break; 
							
						case DialogInterface.BUTTON_NEGATIVE: 
							// do nothing 
							break; 
					} 
				}
			};
			
			// Get the layout inflater
			LayoutInflater inflater = getActivity().getLayoutInflater();

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_dropdown_item_1line,
					this.mRooms);
			View v = inflater.inflate(R.layout.dialog_addroom, null);
			mTextView = (AutoCompleteTextView)v.findViewById(R.id.AutoCompleteTextViewRooms);
			mTextView.setAdapter(adapter);
			
			// Create and return a dialog.
			return new AlertDialog.Builder(getActivity())
				.setTitle("Adding a room")
				.setView(v)
				.setPositiveButton("Add", listener)
				.setNegativeButton("Cancel", listener)
				.create();
		}
		
		@Override
		public void onPause()
		{
			dismiss();
			super.onPause();
		}
	}
}
