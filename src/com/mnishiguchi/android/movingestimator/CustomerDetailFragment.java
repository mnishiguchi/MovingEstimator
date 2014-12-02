package com.mnishiguchi.android.movingestimator;

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
	//private Uri mPhotoFileUri;
	//private String mPhotoFilepath;
	//private String mPhotoFilename;
	
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
			Log.e(TAG, "null == getArguments()");
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

		// --- mEtRefNumber ---
		
		mEtRefNumber = (EditText)v.findViewById(R.id.editTextRefNumber);
		mEtRefNumber.setText(mCustomer.getRefNumber());
		mEtRefNumber.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence input, int start, int before, int count)
			{
				mCustomer.setRefNumber(input.toString());
				
				// Notify it.
				mCallbacks.onCustomerUpdated(mCustomer);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{ } // Required, but not used in this implementation.
			
			@Override
			public void afterTextChanged(Editable s)
			{ } // Required, but not used in this implementation.
		});

		// --- mEtFirstName ---
		
		mEtFirstName = (EditText)v.findViewById(R.id.editTextFirstName);
		mEtFirstName.setText(mCustomer.getFirstName());
		mEtFirstName.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence input, int start, int before, int count)
			{
				mCustomer.setFirstName(input.toString());
				
				// Notify it.
				mCallbacks.onCustomerUpdated(mCustomer);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{ } // Required, but not used in this implementation.
			
			@Override
			public void afterTextChanged(Editable s)
			{ } // Required, but not used in this implementation.
		});
		
		// --- mEtLastName ---
		
		mEtLastName = (EditText)v.findViewById(R.id.editTextLastName);
		mEtLastName.setText(mCustomer.getLastName());
		mEtLastName.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence input, int start, int before, int count)
			{
				mCustomer.setLastName(input.toString());
				
				// Notify it.
				mCallbacks.onCustomerUpdated(mCustomer);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{ } // Required, but not used in this implementation.
			
			@Override
			public void afterTextChanged(Editable s)
			{ } // Required, but not used in this implementation.
		});
		
		// --- mSpinnerPrefix ---
		
		mSpinnerPrefix = (Spinner)v.findViewById(R.id.spinnerPrefix);
		
		// Create an ArrayAdapter using the string array.
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getActivity(),
				R.array.prefixes, // a string-array defined in res/values/strings.xml
				android.R.layout.simple_spinner_item); // the default layout
		
		// Specify the dropdown layout to use.
		// The standard layout defined by the platform.
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		
		// Apply the adapter to the spinner
		mSpinnerPrefix.setAdapter(adapter);
		
		mSpinnerPrefix.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id)
			{
				mCustomer.setPrefix(parent.getItemAtPosition(position).toString());
				
				// Notify it.
				mCallbacks.onCustomerUpdated(mCustomer);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{ } // Required, but not used in this implementation.
		});
		
		// --- mEtOrganization ---
		
		mEtOrganization = (EditText)v.findViewById(R.id.editTextOrganization);
		mEtOrganization.setText(mCustomer.getOrganization());
		mEtOrganization.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence input, int start, int before, int count)
			{
				mCustomer.setOrganization(input.toString());
				
				// Notify it.
				mCallbacks.onCustomerUpdated(mCustomer);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{ } // Required, but not used in this implementation.
			
			@Override
			public void afterTextChanged(Editable s)
			{ } // Required, but not used in this implementation.
		});
		
		// --- mEtAddress ---
		
		mEtAddress = (EditText)v.findViewById(R.id.editTextAddress);
		mEtAddress.setText(mCustomer.getAddress());
		mEtAddress.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence input, int start, int before, int count)
			{
				mCustomer.setAddress(input.toString());
				
				// Notify it.
				mCallbacks.onCustomerUpdated(mCustomer);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{ } // Required, but not used in this implementation.
			
			@Override
			public void afterTextChanged(Editable s)
			{ } // Required, but not used in this implementation.
		});
		
		// --- mEtEmail ---
		
		mEtEmail = (EditText)v.findViewById(R.id.editTextEmail);
		mEtEmail.setText(mCustomer.getEmail());
		mEtEmail.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence input, int start, int before, int count)
			{
				mCustomer.setEmail(input.toString());
				
				// Notify it.
				mCallbacks.onCustomerUpdated(mCustomer);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{ } // Required, but not used in this implementation.
			
			@Override
			public void afterTextChanged(Editable s)
			{ } // Required, but not used in this implementation.
		});
		
		// --- mEtPhoneHome ---
		
		mEtPhoneHome = (EditText)v.findViewById(R.id.editTextPhoneHome);
		mEtPhoneHome.setText(mCustomer.getPhoneHome());
		mEtPhoneHome.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence input, int start, int before, int count)
			{
				mCustomer.setPhoneHome(input.toString());
				
				// Notify it.
				mCallbacks.onCustomerUpdated(mCustomer);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{ } // Required, but not used in this implementation.
			
			@Override
			public void afterTextChanged(Editable s)
			{ } // Required, but not used in this implementation.
		});
		
		// --- mEtPhoneWork ---
		
		mEtPhoneWork = (EditText)v.findViewById(R.id.editTextPhoneWork);
		mEtPhoneWork.setText(mCustomer.getPhoneWork());
		mEtPhoneWork.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence input, int start, int before, int count)
			{
				mCustomer.setPhoneWork(input.toString());
				
				// Notify it.
				mCallbacks.onCustomerUpdated(mCustomer);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{ } // Required, but not used in this implementation.
			
			@Override
			public void afterTextChanged(Editable s)
			{ } // Required, but not used in this implementation.
		});
		
		// --- mEtPhoneCell ---
		
		mEtPhoneCell = (EditText)v.findViewById(R.id.editTextPhoneCell);
		mEtPhoneCell.setText(mCustomer.getPhoneCell());
		mEtPhoneCell.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence input, int start, int before, int count)
			{
				mCustomer.setPhoneCell(input.toString());
				
				// Notify it.
				mCallbacks.onCustomerUpdated(mCustomer);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{ } // Required, but not used in this implementation.
			
			@Override
			public void afterTextChanged(Editable s)
			{ } // Required, but not used in this implementation.
		});
		
		// --- mBtnVolumeOcean ---
		
		mBtnVolumeOcean = (Button)v.findViewById(R.id.btnVolumeOcean);
		mBtnVolumeOcean.setText(String.valueOf(mCustomer.getVolumeOcean()));
		
		// --- mBtnVolumeAir ---
		
		mBtnVolumeAir = (Button)v.findViewById(R.id.btnVolumeAir);
		mBtnVolumeAir.setText(String.valueOf(mCustomer.getVolumeAir()));
		
		// --- mEtVolumeComment ---
		mEtVolumeComment = (EditText)v.findViewById(R.id.editTextVolumeComment);
		mEtVolumeComment.setText(mCustomer.getVolumeComment());
		mEtVolumeComment.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence input, int start, int before, int count)
			{
				mCustomer.setVolumeComment(input.toString());
				
				// Notify it.
				mCallbacks.onCustomerUpdated(mCustomer);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{ } // Required, but not used in this implementation.
			
			@Override
			public void afterTextChanged(Editable s)
			{ } // Required, but not used in this implementation.
		});
		
		// --- mBtnMovingDate ---
		
		mBtnMovingDate = (Button)v.findViewById(R.id.btnMovingDate);
		
		String movingDateString = (null == mCustomer.getMovingDate()) ?
				"Moving date" : mCustomer.getMovingDate().toString();
		mBtnMovingDate.setText(movingDateString);
		
		// --- mEtMovingDateComment ---
		
		mEtMovingDateComment = (EditText)v.findViewById(R.id.editTextMovingDateComment);
		mEtMovingDateComment.setText(mCustomer.getMovingDateComment());
		mEtMovingDateComment.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence input, int start, int before, int count)
			{
				mCustomer.setMovingDateComment(input.toString());
				
				// Notify it.
				mCallbacks.onCustomerUpdated(mCustomer);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{ } // Required, but not used in this implementation.
			
			@Override
			public void afterTextChanged(Editable s)
			{ } // Required, but not used in this implementation.
		});
		
		// --- mEtHomeDescription ---
		
		mEtHomeDescription = (EditText)v.findViewById(R.id.editTextHomeDescription);
		mEtHomeDescription.setText(mCustomer.getHomeDescription());
		mEtHomeDescription.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence input, int start, int before, int count)
			{
				mCustomer.setHomeDescription(input.toString());
				
				// Notify it.
				mCallbacks.onCustomerUpdated(mCustomer);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{ } // Required, but not used in this implementation.
			
			@Override
			public void afterTextChanged(Editable s)
			{ } // Required, but not used in this implementation.
		});
		
		// --- mEtSpecialOrder ---
		
		mEtSpecialOrder = (EditText)v.findViewById(R.id.editTextSpecialOrder);
		mEtSpecialOrder.setText(mCustomer.getSpecialOrders());
		mEtSpecialOrder.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence input, int start, int before, int count)
			{
				mCustomer.setHomeDescription(input.toString());
				
				// Notify it.
				mCallbacks.onCustomerUpdated(mCustomer);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{ } // Required, but not used in this implementation.
			
			@Override
			public void afterTextChanged(Editable s)
			{ } // Required, but not used in this implementation.
		});
		
		// --- mEtGeneralComment ---
		
		mEtGeneralComment = (EditText)v.findViewById(R.id.editTextGeneralComment);
		mEtGeneralComment.setText(mCustomer.getGeneralComment());
		mEtGeneralComment.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence input, int start, int before, int count)
			{
				mCustomer.setGeneralComment(input.toString());
				
				// Notify it.
				mCallbacks.onCustomerUpdated(mCustomer);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{ } // Required, but not used in this implementation.
			
			@Override
			public void afterTextChanged(Editable s)
			{ } // Required, but not used in this implementation.
		});
		
		
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

			case R.id.optionsitem_delete:
				
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
