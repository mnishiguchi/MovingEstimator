package com.mnishiguchi.android.movingestimator;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class CustomerDetailFragment extends Fragment

{
	private static final String TAG = "movingestimator.CustomerDetailFragment";
	
	//public static final String EXTRA_DATE = "com.mnishiguchi.android.movingestimator.date";
	public static final String EXTRA_CUSTOMER_ID = "com.mnishiguchi.android.movingestimator.customer_id";

	//private static final String DIALOG_DATETIME = "datetime";
	//private static final String DIALOG_IMAGE = "image";
	private static final String DIALOG_DELETE = "delete";
	
	//public static final int REQUEST_DATE = 0;
	//public static final int REQUEST_CAMERA = 1;
	//public static final int REQUEST_CONTACT = 2;
	
	// Store reference to an instance of this fragment that is currently working..
	private static CustomerDetailFragment sCustomerDetailFragment;
		
	// Reference to the Customer object stored in the FileCabinet(model layer)
	private Customer mCustomer;
	
	// UI components
	EditText mEtRefNumber, mEtFirstName, mEtLastName;
	Spinner mSpinnerPrefix;
	EditText mEtOrganization;
	EditText mEtAddress, mEtEmail, mEtPhoneHome, mEtPhoneWork, mEtPhoneCell;
	Button mBtnVolumeOcean, mBtnVolumeAir;
	EditText mEtVolumeComment;
	Button mBtnMovingDate;
	EditText mEtMovingDateComment;
	EditText mEtHomeDescription;
	EditText mEtSpecialOrder;
	EditText mEtGeneralComment;
	
	// Reference to CAB.
	private ActionMode mActionMode;
	
	// Default camera
	private Uri mPhotoFileUri;
	private String mPhotoFilepath;
	private String mPhotoFilename;
	
	// Remember reference to callback-registered activities.
	private DetailCallbacks mCallbacks;
	
	/**
	 * Required interface for hosting activities.
	 */
	public interface DetailCallbacks
	{
		void onCustomerAdded(Customer customer);
		void onCustomerUpdated(Customer customer);
		void onCustomerDeleted(Customer customer);
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
		// Ensure that the hosting activity has implemented the callbacks
		try
		{
			mCallbacks = (DetailCallbacks)activity;
		}
		catch (ClassCastException e)
		{
			throw new ClassCastException(activity.toString() + " must implement CrimeFragment.Callbacks");
		}
	}
	
	@Override
	public void onDetach()
	{
		super.onDetach();
		mCallbacks = null;
	}
	
	/**
	 * Creates a new fragment instance and set the specified id as fragment's arguments.
	 * @param crimeId a UUID
	 * @return a new fragment instance with the specified UUID attached as its arguments.
	 */
	public static CustomerDetailFragment newInstance(String customerId)
	{
		// Prepare arguments.
		Bundle args = new Bundle();  // Contains key-value pairs.
		args.putString(EXTRA_CUSTOMER_ID, customerId);
		
		// Creates a fragment instance and sets its arguments.
		CustomerDetailFragment fragment = new CustomerDetailFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Log.d(TAG, "onCreate()");
		
		// Store a reference to this instance.
		sCustomerDetailFragment = this;
		
		
		if (null == getArguments()){
			Log.d(TAG, "null == getArguments()");
			return;
		}
		
		// Retrieve the arguments.
		String customerId = getArguments().getString(EXTRA_CUSTOMER_ID);

	
		// Fetch the Customer based on the id.
		mCustomer = FileCabinet.get(getActivity()).getCustomer(customerId);
		
		// Enable the options menu callback.
		setHasOptionsMenu(true);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreateView()");
		
		// Get reference to the layout.
		View v = inflater.inflate(R.layout.fragment_customerdetail, parent, false);
		
		// If a parent activity is registered in the manifest file, enable the Up button.
		setupActionBarUpButton();

		// UI components
		EditText mEtRefNumber = (EditText)v.findViewById(R.id.editTextRefNumber);
		EditText mEtFirstName = (EditText)v.findViewById(R.id.editTextFirstName);
		EditText mEtLastName = (EditText)v.findViewById(R.id.editTextLastName);
		Spinner mSpinnerPrefix = (Spinner)v.findViewById(R.id.spinnerPrefix);
		EditText mEtOrganization = (EditText)v.findViewById(R.id.editTextOrganization);
		EditText mEtAddress = (EditText)v.findViewById(R.id.editTextAddress);
		EditText mEtEmail = (EditText)v.findViewById(R.id.editTextEmail);
		EditText mEtPhoneHome = (EditText)v.findViewById(R.id.editTextPhoneHome);
		EditText mEtPhoneWork = (EditText)v.findViewById(R.id.editTextPhoneWork);
		EditText mEtPhoneCell = (EditText)v.findViewById(R.id.editTextPhoneCell);
		Button mBtnVolumeOcean = (Button)v.findViewById(R.id.btnVolumeOcean);
		Button mBtnVolumeAir = (Button)v.findViewById(R.id.btnVolumeAir);
		EditText mEtVolumeComment = (EditText)v.findViewById(R.id.editTextVolumeComment);
		Button mBtnMovingDate = (Button)v.findViewById(R.id.btnMovingDate);
		EditText mEtMovingDateComment = (EditText)v.findViewById(R.id.editTextMovingDateComment);
		EditText mEtHomeDescription = (EditText)v.findViewById(R.id.editTextHomeDescription);
		EditText mEtSpecialOrder = (EditText)v.findViewById(R.id.editTextSpecialOrder);
		EditText mEtGeneralComment = (EditText)v.findViewById(R.id.editTextGeneralComment);

		// Return the layout.
		return v;
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
	public void onResume()
	{
		super.onResume();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		Log.d(TAG, "onPause()");
		
		// TODO - FileCabinet.get(getActivity()).saveCustomers();
	}
	
	/**
	 * Creates the options menu and populates it with the items defined
	 * in res/menu/fragment_customerdetail.xml.
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		
		// Inflate the menu, adding menu items to the action bar.
		inflater.inflate(R.menu.fragment_customerdetail, menu);
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

			case R.id.menuitem_delete:
				
				// Show the delete dialog.
				new DeleteDialog().show(getFragmentManager(), DIALOG_DELETE);

			default:
				return super.onOptionsItemSelected(item);
	 	}
	}
	
	/**
	 * Delete the currently shown Customer from the FileCabinet's list.
	 * Update the Pager. Finish this fragment. Show a toast message.
	 */
	private void deleteCustomer()
	{
		Log.d(TAG, "deleteCustomer() - mCustomer.getLastName(): " + mCustomer.getLastName());
		
		// Get the crime title.
		String customerString =
				(null == mCustomer.getLastName() || mCustomer.toString().equals("")) ?
				"(No last name)" : mCustomer.toString();
		
		// Delete the crime.
		FileCabinet.get(getActivity()).deleteCustomer(mCustomer);
		
		// Update the pager adapter.
		if (Utils.hasTwoPane(getActivity())) // Two-pane.
		{
			mCallbacks.onCustomerDeleted(mCustomer);
		}
		else // Single-pane.
		{
			// Update the pager data.
			((CustomerPagerActivity)getActivity()).getPagerAdapter().notifyDataSetChanged();
			
			// Notify the user about the result.
			Utils.showToast(getActivity(), customerString + " has been deleted.");
			
			// Finish this activity.
			getActivity().finish();
		}
	}
	
	/**
	 * Show a confirmation message before actually deleting selected items.
	 */
	static class DeleteDialog extends DialogFragment
	{
		// Store the selected list item that was passed in.
		//static Customer sCustomer;
		
		/**
		 * Create a new instance that is capable of deleting the specified list items.
		 */
		//static DeleteDialog newInstance(Customer customer)
		{
			// Store the selected items so that we can refer to it later.
			//sCustomer = customer;
			
			// Create a fragment.
			//DeleteDialog fragment = new DeleteDialog();
			//fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
			
			//return fragment;
		}
		
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
							
							sCustomerDetailFragment.deleteCustomer();
							break; 
							
						case DialogInterface.BUTTON_NEGATIVE: 
							// do nothing 
							break; 
					} 
				}
			};
			
			// Create and return a dialog.
			return new AlertDialog.Builder(getActivity())
				.setTitle("Deleting this customer")
				.setMessage("Are you sure?")
				.setPositiveButton("Yes", listener)
				.setNegativeButton("Cancel", listener)
				.create();
		}
	}
}
