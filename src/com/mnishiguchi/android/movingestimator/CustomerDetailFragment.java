package com.mnishiguchi.android.movingestimator;

import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class CustomerDetailFragment extends Fragment

{
	private static final String TAG = "movingestimator.CustomerDetailFragment";
	public static final String EXTRA_CUSTOMER_ID_DETAIL = "com.mnishiguchi.android.movingestimator.customer_id_detail";
	private static final String DIALOG_DELETE = "delete";

	// Store reference to an instance of this fragment that is currently working..
	private static CustomerDetailFragment sCustomerDetailFragment;
		
	// Reference to the Customer object stored in the FileCabinet(model layer)
	private Customer mCustomer;
	
	// UI components
	TextView mTvRefNumber, mTvCustomerName,mTvOrganization,
		mTvAddress, mTvEmail, mTvPhoneHome, mTvPhoneWork, mTvPhoneCell,
		mTvVolumeOcean, mTvVolumeAir, mTvVolumeComment,
		mTvMovingDate, mTvMovingDateComment,
		mTvHomeDescription, mTvSpecialOrder, mTvGeneralComment;
	
	// Reference to CAB.
	private ActionMode mActionMode;
	
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
		args.putString(EXTRA_CUSTOMER_ID_DETAIL, customerId);
		
		// Creates a fragment instance and sets its arguments.
		CustomerDetailFragment fragment = new CustomerDetailFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// Store a reference to this instance.
		sCustomerDetailFragment = this;
		
		
		if (null == getArguments()){
			Log.e(TAG, "null == getArguments()");
			return;
		}
		
		// Retrieve the arguments.
		String customerId = getArguments().getString(EXTRA_CUSTOMER_ID_DETAIL);

		// Fetch the Customer based on the id.
		mCustomer = FileCabinet.get(getActivity()).getCustomer(customerId);
		
		Log.d(TAG, "onCreate() - mCustomer: " + mCustomer.getLastName());
		
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

		// Reuse
		String temp;
		
		// --- RefNumber ---
		
		mTvRefNumber = (TextView)v.findViewById(R.id.textViewRefNumber);
		mTvRefNumber.setText(mCustomer.getRefNumber());
		
		// --- Customer name ---
		
		mTvCustomerName = (TextView)v.findViewById(R.id.textViewCustomerName);
		temp = mCustomer.getPrefix() + " "
				+ mCustomer.getLastName().toUpperCase(Locale.US) + ", "
				+ mCustomer.getFirstName();
		mTvCustomerName.setText(temp);
		
		// --- Organization ---
		
		mTvOrganization = (TextView)v.findViewById(R.id.textViewOrganization);
		mTvOrganization.setText(mCustomer.getOrganization());
		
		// --- Address ---
		
		mTvAddress = (TextView)v.findViewById(R.id.textViewAddress);
		mTvAddress.setText(mCustomer.getAddress());
		
		// --- Email ---
		
		mTvEmail = (TextView)v.findViewById(R.id.textViewEmail);
		mTvEmail.setText(mCustomer.getEmail());
		
		// --- PhoneHome ---
		
		mTvPhoneHome = (TextView)v.findViewById(R.id.textViewPhoneHome);
		mTvPhoneHome.setText(mCustomer.getPhoneHome());
		
		// --- PhoneWork ---
		
		mTvPhoneWork = (TextView)v.findViewById(R.id.textViewPhoneWork);
		mTvPhoneWork.setText(mCustomer.getPhoneWork());
		
		// --- mEtPhoneCell ---
		
		mTvPhoneCell = (TextView)v.findViewById(R.id.textViewPhoneCell);
		mTvPhoneCell.setText(mCustomer.getPhoneCell());
		
		// --- VolumeOcean ---
		
		mTvVolumeOcean = (TextView)v.findViewById(R.id.textViewVolumeOcean);
		mTvVolumeOcean.setText(String.valueOf(mCustomer.getVolumeOcean()));
		
		// --- VolumeAir ---
		
		mTvVolumeAir = (TextView)v.findViewById(R.id.textViewVolumeAir);
		mTvVolumeAir.setText(String.valueOf(mCustomer.getVolumeAir()));
		
		// --- VolumeComment ---
		mTvVolumeComment = (TextView)v.findViewById(R.id.textViewVolumeComment);
		String volumeComment = (null == mCustomer.getVolumeComment()) ?
				"" : mCustomer.getVolumeComment();
		mTvVolumeComment.setText(volumeComment);
		
		// --- MovingDate ---
		
		mTvMovingDate = (TextView)v.findViewById(R.id.textViewMovingDate);
		temp = (null == mCustomer.getMovingDate()) ?
				"TBD" : mCustomer.getMovingDate().toString();
		mTvMovingDate.setText(temp);
		
		// --- mEtMovingDateComment ---
		
		mTvMovingDateComment = (TextView)v.findViewById(R.id.textViewMovingDateComment);
		temp = (null == mCustomer.getVolumeComment()) ?
				"" : mCustomer.getVolumeComment();
		mTvMovingDateComment.setText(temp);

		// --- HomeDescription ---
		
		mTvHomeDescription = (TextView)v.findViewById(R.id.textViewHomeDescription);
		temp = (null == mCustomer.getHomeDescription()) ?
				"" : mCustomer.getHomeDescription();
		mTvHomeDescription.setText(temp);
		
		// --- SpecialOrder ---
		mTvSpecialOrder = (TextView)v.findViewById(R.id.textViewSpecialOrder);
		temp = (null == mCustomer.getSpecialOrder()) ?
				"" : mCustomer.getSpecialOrder();
		mTvSpecialOrder.setText(temp);
		
		// --- GeneralComment ---
		
		mTvGeneralComment = (TextView)v.findViewById(R.id.textViewGeneralComment);
		temp = (null == mCustomer.getGeneralComment()) ?
				"" : mCustomer.getGeneralComment();
		mTvGeneralComment.setText(temp);
		
		// Return the root-layout.
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
	
	@Override
	public void onPrepareOptionsMenu(Menu menu)
	{
		super.onPrepareOptionsMenu(menu);
		
		// Show delete item only in the two-pane mode.
		menu.findItem(R.id.optionsmenu_delete).setVisible(Utils.hasTwoPane(getActivity()));
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

			case R.id.optionsmenu_delete:
				
				// Show the delete dialog.
				new DeleteDialog().show(getFragmentManager(), DIALOG_DELETE);
				
			case R.id.optionsmenu_edit:
				
				Intent i = new Intent(getActivity(), CustomerEditActivity.class);
				
				if (mCustomer.getId() != null)
				{
					i.putExtra(EXTRA_CUSTOMER_ID_DETAIL, mCustomer.getId());
					Log.e(TAG, "mCustomer.getId()" + mCustomer.getId());
					startActivity(i);
				}
				
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
		
		// Delete the customer.
		FileCabinet.get(getActivity()).deleteCustomer(mCustomer);
		
		// Update the pager adapter.
		if (Utils.hasTwoPane(getActivity())) // Two-pane.
		{
			mCallbacks.onCustomerDeleted(mCustomer);
		}
		else // Single-pane.
		{
			((CustomerPagerActivity)getActivity()).getPagerAdapter()
				.notifyDataSetChanged();
			
			// Toast a message and finish this activity.
			Utils.showToast(getActivity(), customerString + " has been deleted.");

			getActivity().finish();
		}
	}
	
	/**
	 * Show a confirmation message before actually deleting selected items.
	 */
	static class DeleteDialog extends DialogFragment
	{
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
