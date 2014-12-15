package com.mnishiguchi.android.movingestimator;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class CustomerEditFragment extends Fragment

{
	private static final String TAG = "movingestimator.CustomerEditFragment";
	
	public static final String EXTRA_CUSTOMER_ID = "com.mnishiguchi.android.movingestimator.customer_id_edit";
	
	public static final int REQUEST_DATE = 1;
	
	public static final String DIALOG_DATETIME = "dialogDateTime";
	
	// Reference to the Customer object stored in the FileCabinet(model layer)
	private Customer mCustomer;
	
	// UI components
	EditText mEtRefNumber, mEtFirstName, mEtLastName;
	Spinner mSpinnerPrefix;
	EditText mEtOrganization;
	EditText mEtEmail, mEtPhoneHome, mEtPhoneWork, mEtPhoneCell;
	EditText mEtFrom, mEtTo;
	Button mBtnMovingDate;
	EditText mEtMovingSchedule;
	EditText mEtHomeDescription;
	EditText mEtSpecialOrder;
	EditText mEtGeneralComment;
	
	// Remember the original prefix.
	String mPrefix;
	
	/**
	 * Creates a new fragment instance and set the specified id as fragment's arguments.
	 * @param crimeId a UUID
	 * @return a new fragment instance with the specified UUID attached as its arguments.
	 */
	public static CustomerEditFragment newInstance(String customerId)
	{
		// Prepare arguments.
		Bundle args = new Bundle(); // Contains key-value pairs.
		args.putString(EXTRA_CUSTOMER_ID, customerId);
		
		// Creates a fragment instance and sets its arguments.
		CustomerEditFragment fragment = new CustomerEditFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// Retrieve the arguments.
		String customerId = getArguments().getString(EXTRA_CUSTOMER_ID);
		//Log.e(TAG, "customerId=>" + customerId);
		
		// Fetch the Customer based on the id.
		mCustomer = FileCabinet.get(getActivity()).getCustomer(customerId);
		//Log.e(TAG, "mCustomer=>" + mCustomer);
		
		// Enable the options menu callback.
		setHasOptionsMenu(true);
		
		// Remember the original prefix
		mPrefix = mCustomer.getPrefix();
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState)
	{
		// Get reference to the layout.
		View v = inflater.inflate(R.layout.fragment_customeredit, parent, false);
		
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
				setActionBarTitle();
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
		
		// Set the current prefix selected.
		for (int i = 0, size = adapter.getCount();
				i < size; i++)
		{
			if (adapter.getItem(i).equals(mPrefix))
			{
				mSpinnerPrefix.setSelection(i);
			}
		}

		mSpinnerPrefix.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id)
			{
				mCustomer.setPrefix(parent.getItemAtPosition(position).toString());
				setActionBarTitle();
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
				setActionBarTitle();
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
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{ } // Required, but not used in this implementation.
			
			@Override
			public void afterTextChanged(Editable s)
			{ } // Required, but not used in this implementation.
		});
		
		
		// --- mEtFrom ---
		
		mEtFrom = (EditText)v.findViewById(R.id.editTextFrom);
		mEtFrom.setText(mCustomer.getFrom());
		mEtFrom.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence input, int start, int before, int count)
			{
				mCustomer.setFrom(input.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{ } // Required, but not used in this implementation.
			
			@Override
			public void afterTextChanged(Editable s)
			{ } // Required, but not used in this implementation.
		});
		
		// --- mEtTo ---
		
		mEtTo = (EditText)v.findViewById(R.id.editTextTo);
		mEtTo.setText(mCustomer.getTo());
		mEtTo.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence input, int start, int before, int count)
			{
				mCustomer.setTo(input.toString());
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
		showUpdatedDate();

		mBtnMovingDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				FragmentManager fm = getActivity().getSupportFragmentManager();
				DialogFragment dialog;
				
				// If the moving date is not initialized, create one.
				Date date = (null == mCustomer.getMovingDate()) ?
						new Date() : mCustomer.getMovingDate();

				dialog = DateTimeEditDialog.newInstance(date);
			
				// Build a connection with the dialog to get the result returned later on.
				dialog.setTargetFragment(CustomerEditFragment.this, REQUEST_DATE);
				
				// Show the DatePickerFragment.
				dialog.show(fm, DIALOG_DATETIME);
			}
		});
		
		// --- mEtMovingDateComment ---
		
		mEtMovingSchedule = (EditText)v.findViewById(R.id.editTextMovingSchedule);
		mEtMovingSchedule.setText(mCustomer.getMovingSchedule());
		mEtMovingSchedule.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence input, int start, int before, int count)
			{
				mCustomer.setMovingSchedule(input.toString());
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
		mEtSpecialOrder.setText(mCustomer.getSpecialOrder());
		mEtSpecialOrder.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence input, int start, int before, int count)
			{
				mCustomer.setSpecialOrder(input.toString());
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
	
	private void showUpdatedDate()
	{
		String movingDateString = (null == mCustomer.getMovingDate()) ?
				"TBD" : mCustomer.getMovingDateString();
		mBtnMovingDate.setText(movingDateString);
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
	
	private void setActionBarTitle()
	{
		// Set the ActionBar's title.
		getActivity().setTitle(mCustomer.toString() + " | EDIT");
		getActivity().getActionBar().setSubtitle(mCustomer.getOrganization());
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		// Set the ActionBar's title.
		setActionBarTitle();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		
		FileCabinet.get(getActivity()).saveCustomers();
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
		//inflater.inflate(R.menu.fragment_customeredit, menu);
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

			default:
				return super.onOptionsItemSelected(item);
	 	}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent resultData)
	{
		if (resultCode != Activity.RESULT_OK) return;
		
		// --- Retrieve updated date ---
		
		if (requestCode == REQUEST_DATE)
		{
			// Retrieve data from the passed-in Intent.
			Date date = (Date) resultData.getSerializableExtra(DateTimeEditDialog.EXTRA_DATE);
			
			// Update the date in the model layer(FileCabinet)
			mCustomer.setMovingDate(date);
			
			// Set the updated date on the button.
			showUpdatedDate();
		}
	}
	
	
}
