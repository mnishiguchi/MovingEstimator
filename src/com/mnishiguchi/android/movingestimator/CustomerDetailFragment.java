package com.mnishiguchi.android.movingestimator;

import java.io.File;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomerDetailFragment extends Fragment
{
	private static final String TAG = "movingestimator.CustomerDetailFragment";
	
	public static final String EXTRA_CUSTOMER_ID = "com.mnishiguchi.android.movingestimator.customer_id_detail";
	
	private static final String DIALOG_DELETE = "deleteDialog";
	private static final String DIALOG_IMAGE = "imageDialog";

	public static final int REQUEST_DEFAULT_CAMERA = 1;
	
	// Reference to the Customer object stored in the FileCabinet(model layer)
	private Customer mCustomer;
	
	// UI components
	TextView mTvRefNumber, mTvCustomerName,mTvOrganization,
		mTvAddress, mTvEmail, mTvPhoneHome, mTvPhoneWork, mTvPhoneCell,
		mTvVolumeOcean, mTvVolumeAir, mTvVolumeComment,
		mTvMovingDate, mTvMovingDateComment,
		mTvHomeDescription, mTvSpecialOrder, mTvGeneralComment;
	ImageView mThumbnail;
	ImageButton mBtnPhoto;
	
	// Default camera
	private Uri mPhotoFileUri;
	private String mPhotoFilepath;
	private String mPhotoFilename;
	
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
		
		// Retrieve the arguments.
		String customerId = getArguments().getString(EXTRA_CUSTOMER_ID);

		// Fetch the Customer based on the id.
		mCustomer = FileCabinet.get(getActivity()).getCustomer(customerId);
		
		Log.d(TAG, "onCreate() - mCustomer=>" + mCustomer.getLastName());
		
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
		View v = inflater.inflate(R.layout.fragment_customerdetail, parent, false);
		
		// Reuse
		String temp;
		
		// --- RefNumber ---
		
		mTvRefNumber = (TextView)v.findViewById(R.id.textViewRefNumber);
		mTvRefNumber.setText(mCustomer.getRefNumber());
		
		// --- Customer name ---
		
		mTvCustomerName = (TextView)v.findViewById(R.id.textViewCustomerName);
		
		String prefix = (null == mCustomer.getPrefix()) ? "" : mCustomer.getPrefix();
		String lastName = (null == mCustomer.getLastName()) ? "" : mCustomer.getLastName();
		String firstName = (null == mCustomer.getFirstName()) ? "" : mCustomer.getFirstName();
		if (null == prefix && null == lastName && null == firstName)
		{
			temp = "";
		}
		else
		{
			temp = prefix + " " + lastName.toUpperCase(Locale.US) + ", " + firstName;
		}
		
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
				"TBD" : mCustomer.getMovingDateString();
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
		
		// --- mThumbnail ---
		
		mThumbnail = (ImageView)v.findViewById(R.id.imageViewThumbnail);
		mThumbnail.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				Photo photo = mCustomer.getPhoto();
				if (null == photo) return;
				
				FragmentManager fm = getActivity().getSupportFragmentManager();
				
				// Get the absolute path for this crime's photo.
				String path = photo.getAbsolutePath(getActivity());
				if (null == path)
				{
					Log.e(TAG, "Couldn't get this photo's filepath");
					return;
				}
				
				// Show an ImageFragment
				ImageFragment.newInstance(path).show(fm, DIALOG_IMAGE);
			}
		});
		
		// Long click => Contextual action for deleting photo.
		final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu)
			{
				// Remember reference to action mode.
				mActionMode = mode;
						
				// Inflate the menu using a special inflater defined in the ActionMode class.
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.context_thumbnail, menu);
				return true;
			}
					
			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu)
			{
				mode.setTitle("Photo Checked");
				return false;
			}
					
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item)
			{
				switch (item.getItemId())
				{
					case R.id.contextmenu_delete_photo: // Delete menu item.
						
						deletePhoto();

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
		
		// Listen for long clicks. Start the CAB.
		mThumbnail.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v)
			{
				// Ignore the long click if already in the ActionMode.
				if (mActionMode != null) return false;
				
				// Check if a photo is set on the ImageView.
				boolean hasDrawable = (mThumbnail.getDrawable() != null);
				if (hasDrawable)
				{
					// Show the Contexual Action Bar.
					getActivity().startActionMode(actionModeCallback);
				}
						
				return true; // Long click was consumed.
			}
		});
		
		// --- Photo Button ---
				
		mBtnPhoto = (ImageButton)v.findViewById(R.id.btnPhoto);
		mBtnPhoto.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				startDefaultCamera();
			}
		});
		
		// --- Checking For Camera Availability ---
		
		// if camera is not available, disable camera functionality.
		if (!Utils.hasCamera(getActivity()))
		{
			mBtnPhoto.setEnabled(false);
		}
		
		// Return the root-layout.
		return v;
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser)
	{
		super.setUserVisibleHint(isVisibleToUser);
		
		Log.d(TAG, "isVisibleToUser=>" + isVisibleToUser);
		if (isVisibleToUser)
		{
			// Remember the current customer here because the viewPager's
			// position changes when it prepares for next move.
			Customer.setCurrentCustomer(mCustomer);
			
			// Set the action bar title.
			getActivity().setTitle(mCustomer.toString());
		}
		else
		{
			// Remove the CAB when the pager is swiped.
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
		
		showThumbnail();
	}
	
	/*
	 * Unload the photo as soon as this Fragment's view becomes invisible to the user.
	 */
	@Override
	public void onStop()
	{
		super.onStop();
		
		ImageUtils.cleanImageView(mThumbnail);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent resultData)
	{
		if (resultCode != Activity.RESULT_OK) return;

		// --- Built-in Camera ---
		
		if (requestCode == REQUEST_DEFAULT_CAMERA)
		{
			Log.d(TAG, "After camera activity, mPhotoPath: " + mPhotoFilepath);
			
			// Delete the old photo, if any.
			if (mCustomer.getPhoto() != null)
			{
				deletePhoto();
			}

			Uri photoUri = null;

			if (null == resultData)
			{
				// A known bug here! The image should have saved in fileUri
				Utils.showToast(getActivity(), "Image saved successfully");
				photoUri  = mPhotoFileUri;
			}
			else
			{
				photoUri  = resultData.getData();
				Utils.showToast(getActivity(), "Image saved successfully in: " + resultData.getData());
			}

			if (photoUri != null)
			{
				Photo photo = new Photo(mPhotoFilename);
				mCustomer.setPhoto(photo);
				Log.d(TAG, "photo.getFilename(): " + photo.getFilename());
				
				// Notify it.
				mCallbacks.onCustomerUpdated(mCustomer);
				
				// Set the photo on the imageView.
				showThumbnail();
				
				// Forget the filepath.
				mPhotoFilepath = null;
			}
			else
			{
				Utils.showToast(getActivity(), "Error saving photo: " + mPhotoFilepath);
			}
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

	private void startDefaultCamera()
	{
		// Camera exists? Then proceed...
		Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	
		// Ensure that there's a camera activity to handle the intent
		if (Utils.isIntentSafe(getActivity(), i))
		{
			// Create the File where the photo should go.
			// If you don't do this, you may get a crash in some devices.
			File photoFile = null;
			
			// Create a file, where we save a photo.
			mPhotoFilename = ImageUtils.generateImageFileName(getActivity());
			photoFile = ImageUtils.createImageFile(getActivity(), mPhotoFilename);
			
			// Remember the filepath.
			mPhotoFilepath = photoFile.getAbsolutePath();
			Log.e(TAG, "After createImageFile(): " + mPhotoFilepath);
			
			// Continue only if the File was successfully created
			if (photoFile != null)
			{
				mPhotoFileUri = Uri.fromFile(photoFile);
				i.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoFileUri);
				startActivityForResult(i, REQUEST_DEFAULT_CAMERA);
			}
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

	/**
	 * Get the current crime's photo image from file storage and show it on the ImageView.
	 * Loading images in onStart() and them unloading in onStop is a good practice.
	 */
	private void showThumbnail()
	{
		// Ensure that this Crime has a photo.
		if (null == mCustomer.getPhoto())
		{
			mCustomer.setPhoto(null);
			return ; // Fail.
		}
		
		// Get a scaled bitmap.
		Photo photo = mCustomer.getPhoto();
		BitmapDrawable bitmap = photo.loadBitmapDrawable(getActivity());
	
		// Set the image on the ImageView.
		mThumbnail.setImageDrawable(bitmap);
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
		
		// Delete photo too, if there's one.
		deletePhoto();
		
		// Notify the user..
		Utils.showToast(getActivity(), customerString + " has been deleted.");
	
		if (Utils.hasTwoPane(getActivity())) // Two-pane.
		{
			mCallbacks.onCustomerDeleted(mCustomer);
		}
	}

	/**
	 * Delete from disk and from CrimeLab  the photo of the currently shown Crime.
	 * Show a toast message.
	 */
	private void deletePhoto()
	{
		if (null == mCustomer.getPhoto())
		{
			Utils.showToast(getActivity(), "Couldn't find any photo to delete");
			return ; // Fail.
		}
		
		// Clean up the ImageView.
		ImageUtils.cleanImageView(mThumbnail);
		
		// Delete the image data file on disk.
		boolean success = mCustomer.getPhoto().deletePhoto(getActivity());
		if (success)
		{
			Utils.showToast(getActivity(), "old photo deleted");
			
			// Set the reference to null.
			mCustomer.setPhoto(null);
		}
		else
		{
			Utils.showToast(getActivity(), "Couldn't delete the old photo");
		}
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
		Intent i;
		
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
				return true; // Indicate that no further processing is necessary.
			
			case R.id.optionsmenu_edit:
				
				i = new Intent(getActivity(), CustomerEditActivity.class);
				i.putExtra(CustomerEditFragment.EXTRA_CUSTOMER_ID, mCustomer.getId());
				startActivity(i);
				return true; // Indicate that no further processing is necessary.
				
			case R.id.optionsmenu_estimate:
				
				i = new Intent(getActivity(), EstimateOverviewActivity.class);
				startActivity(i);
				return true; // Indicate that no further processing is necessary.
				
			case R.id.optionsmenu_email:

				// Create a csv file for estimate.
				EstimateDataManager.get(getActivity())
					.createCSVReport(Customer.getCurrentCustomer().getId());
				
				// TODO
				// Attach csv file.
				
				// send.
				reportEstimate();
				
				return true; 
				
			default:
				return super.onOptionsItemSelected(item);
	 	}
	}
	
	private void reportEstimate()
	{
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("text/plain");
		i.putExtra(Intent.EXTRA_TEXT, getEstimateReport());
		i.putExtra(Intent.EXTRA_SUBJECT, R.string.estimate_report_subject);
		
		// Set the chooser so that the user can choose every time they push this button.
		i = Intent.createChooser(i, getString(R.string.send_report));
		startActivity(i);
	}

	/**
	 * Create a string for the reporting purpose.
	 */
	private String getEstimateReport()
	{
		Customer customer = Customer.getCurrentCustomer();
		
		String report = "";
		report += getString(R.string.customer_info,
				customer.toString() + ", " + customer.getFirstName(),
				customer.getOrganization(),
				customer.getEmail(),
				customer.getPhoneHome(),
				customer.getPhoneWork(),
				customer.getPhoneCell()
				);
		report += getString(R.string.moving_info,
				"",//customer.from,
				"",//customer.to,
				formatDateForReport(customer.getMovingDate()),
				customer.getMovingDateComment(),
				customer.getHomeDescription(),
				customer.getSpecialOrder(),
				customer.getGeneralComment()
				);
		
		return report;
	}

	private String formatDateForReport(Date date)
	{
		if (null == date) return "";
		
		String dateFormat = "EEE, MMM dd";
		return (String) DateFormat.format(dateFormat, date).toString();
	}
	
	/**
	 * Show a confirmation message before actually deleting selected items.
	 */
	class DeleteDialog extends DialogFragment
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
							
							CustomerDetailFragment.this.deleteCustomer();
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
