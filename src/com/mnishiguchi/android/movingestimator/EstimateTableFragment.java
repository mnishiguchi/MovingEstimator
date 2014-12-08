package com.mnishiguchi.android.movingestimator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class EstimateTableFragment extends Fragment
{
	private static final String TAG = "movingestimator.EstimateFragment";
	
	private static final String DIALOG_ADD_ITEM = "addItemDialog";
	//private static final String DIALOG_IMAGE = "imageDialog";
	
	public static final String EXTRA_CUSTOMER_ID = "com.mnishiguchi.android.movingestimator.id";
	public static final String EXTRA_ROOM = "com.mnishiguchi.android.movingestimator.room";
	
	// Reference to the Customer object stored in the FileCabinet(model layer)
	//private Customer mCustomer;
	
	private Cursor mEstimateCursor;
	
	// UI components
	// TODO
	
	// Reference to CAB.
	private ActionMode mActionMode;
	
	/**
	 * Creates a new fragment instance and set the specified id as fragment's arguments.
	 * @param crimeId a UUID
	 * @return a new fragment instance with the specified UUID attached as its arguments.
	 */
	public static EstimateTableFragment newInstance(String customerId, String room)
	{
		// Prepare arguments.
		Bundle args = new Bundle();  // Contains key-value pairs.
		args.putString(EXTRA_CUSTOMER_ID, customerId);
		args.putString(EXTRA_ROOM, room);
		
		// Creates a fragment instance and sets its arguments.
		EstimateTableFragment fragment = new EstimateTableFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	/**
	 * Private constructor.
	 */
	private EstimateTableFragment()
	{ }
		
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// Retrieve the arguments.
		String customerId = getArguments().getString(EXTRA_CUSTOMER_ID);
		String room = getArguments().getString(EXTRA_ROOM);

		// Fetch the Customer's estimate data from database.
		// TODO
		
		Log.d(TAG, "onCreate()");
		
		// Enable the options menu callback.
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreateView()");
		
		// Get reference to the layout.
		View v = inflater.inflate(R.layout.fragment_estimate, parent, false);
		
		// If a parent activity is registered in the manifest file, enable the Up button.
		setupActionBarUpButton();

		// UI components.
		// TODO
		
		
		// Return the root-layout.
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
		inflater.inflate(R.menu.fragment_estimate, menu);
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
	
	private void addMovingItem(String item)
	{
		// TODO
	}
	
	// TODO
	/**
	 * Add the selected item to the estimate.
	 */
	class AddItemDialog extends DialogFragment
	{
		private AutoCompleteTextView mTextView;
		
		// The room list for autoComplete.
		private final String[] mMovingItems = EstimateTableFragment.this.getActivity()
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
							
							EstimateTableFragment.this.addMovingItem(mTextView.getText().toString());
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
			mTextView = (AutoCompleteTextView)v.findViewById(R.id.AutoCompleteTextViewMovingItems);
			mTextView.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_dropdown_item_1line,
					this.mMovingItems));
			
			// Create and return a dialog.
			return new AlertDialog.Builder(getActivity())
				.setTitle("Adding an item")
				.setView(v)
				.setPositiveButton("Add", listener)
				.setNegativeButton("Cancel", listener)
				.create();
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
