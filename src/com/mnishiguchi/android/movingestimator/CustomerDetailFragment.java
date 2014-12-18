package com.mnishiguchi.android.movingestimator;

import java.io.File;
import java.util.ArrayList;
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
		mTvEmail, mTvPhoneHome, mTvPhoneWork, mTvPhoneCell,
		mTvFrom, mTvTo, mTvMovingDate, mTvMovingSchedule,
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
			if (Locale.getDefault().getDisplayLanguage().equals("“ú–{Œê"))
			{
				temp = lastName + " " + firstName + " " + prefix;
			}
			else
			{
				temp = prefix + " " + lastName.toUpperCase(Locale.US) + ", " + firstName;
			}
		}
		
		mTvCustomerName.setText(temp);
		
		// --- Organization ---
		
		mTvOrganization = (TextView)v.findViewById(R.id.textViewOrganization);
		mTvOrganization.setText(mCustomer.getOrganization());
		
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
		
		// --- From ---
		
		mTvFrom = (TextView)v.findViewById(R.id.textViewFrom);
		mTvFrom.setText(String.valueOf(mCustomer.getFrom()));
		
		// --- To ---
		
		mTvTo = (TextView)v.findViewById(R.id.textViewTo);
		mTvTo.setText(String.valueOf(mCustomer.getTo()));
		
		// --- MovingDate ---
		
		mTvMovingDate = (TextView)v.findViewById(R.id.textViewMovingDate);
		
		temp = (null == mCustomer.getMovingDate()) ?
				getActivity().getString(R.string.tbd) : mCustomer.getMovingDateString();
		mTvMovingDate.setText(temp);
		
		// --- mEtMovingDateComment ---
		
		mTvMovingSchedule = (TextView)v.findViewById(R.id.textViewMovingSchedule);
		temp = (null == mCustomer.getMovingSchedule()) ?
				"" : mCustomer.getMovingSchedule();
		mTvMovingSchedule.setText(temp);

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
		
		//Log.d(TAG, "isVisibleToUser=>" + isVisibleToUser);
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
			//Log.d(TAG, "After camera taken, mPhotoPath=>" + mPhotoFilepath);
			
			// Delete the old photo, if any.
			if (mCustomer.getPhoto() != null)
			{
				deletePhoto();
			}

			// The image should have saved in mPhotoFileUri
			Uri photoUri = null;
			photoUri  = mPhotoFileUri;

			if (photoUri != null)
			{
				Photo photo = new Photo(mPhotoFilename);
				mCustomer.setPhoto(photo);
				
				// Set the photo on the imageView.
				showThumbnail();
				
				// Forget the filepath.
				mPhotoFilepath = null;
				
				// Save updated customers data.
				FileCabinet.get(getActivity()).saveCustomers();
				
				// Notify the hosting activity.
				mCallbacks.onCustomerUpdated(mCustomer);
				
				// Notify the user.
				Utils.showToast(getActivity(), "A photo saved successfully");
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
			Log.e(TAG, "Couldn't enable the Up button");
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
			//Utils.showToast(getActivity(), "Couldn't find any photo to delete");
			return ; // Fail.
		}
		
		// Clean up the ImageView.
		ImageUtils.cleanImageView(mThumbnail);
		
		// Delete the image data file on disk.
		boolean success = mCustomer.getPhoto().deletePhoto(getActivity());
		if (success)
		{
			Utils.showToast(getActivity(), "A photo deleted");
			
			// Set the reference to null.
			mCustomer.setPhoto(null);
			
			// Save updated customers data.
			FileCabinet.get(getActivity()).saveCustomers();
		}
		else
		{
			Utils.showToast(getActivity(), "Couldn't delete the photo");
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
				return true; // no further processing is necessary.
				
			case R.id.optionsmenu_estimate:
				
				i = new Intent(getActivity(), EstimateOverviewActivity.class);
				startActivity(i);
				return true; // no further processing is necessary.
				
			case R.id.optionsmenu_report:

				// Create a csv file for estimate.
				EstimateDataManager.get(getActivity())
					.createCSVReport(Customer.getCurrentCustomer().getId(), this);

				return true; 
				
			default:
				return super.onOptionsItemSelected(item);
	 	}
	}
	
	/**
	 * Invoked after a csv file is created.
	 * Send a report with that csv file and photo file attached.
	 * @param csvFile
	 */
	public void sendReport(File csvFile)
	{
		Intent i = new Intent(Intent.ACTION_SEND_MULTIPLE);
		i.setType("text/plain");
		
		// Subject.
		i.putExtra(Intent.EXTRA_SUBJECT, getString(
				R.string.estimate_report_subject, Customer.getCurrentCustomer()));
		
		// Body.
		i.putExtra(Intent.EXTRA_TEXT, getEstimateReport());

		// Attachments.
		ArrayList<Uri> uris = new ArrayList<Uri>();
		
		if (csvFile != null)
		{
			uris.add(Uri.fromFile(csvFile));
		}
		
		if (mCustomer.getPhoto() != null)
		{
			File dir = ImageUtils.getPictureStorageDir(getActivity());
			File photoFile = new File(dir, mCustomer.getPhoto().getFilename());
			uris.add(Uri.fromFile(photoFile));
		}

		i.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
		
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
		
		String nameString = "";
		if (Locale.getDefault().getDisplayLanguage().equals("“ú–{Œê")) // Japanese
		{
			nameString = customer.getLastName() + " " + customer.getFirstName() + " " + customer.getPrefix();
		}
		else // The other languages
		{
			nameString = customer.toString() + ", " + customer.getFirstName();
		}
		
		String report = "";
		report += getString(R.string.customer_info,
				nameString,
				customer.getOrganization(),
				customer.getEmail(),
				customer.getPhoneHome(),
				customer.getPhoneWork(),
				customer.getPhoneCell()
				);
		report += getString(R.string.moving_info,
				customer.getFrom(),
				customer.getTo(),
				customer.getMovingDateString(),
				customer.getMovingSchedule(),
				customer.getHomeDescription(),
				customer.getSpecialOrder(),
				customer.getGeneralComment()
				);
		
		return report;
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
				.setTitle(R.string.deleting_this_customer)
				.setMessage(R.string.are_you_sure)
				.setPositiveButton(android.R.string.ok, listener)
				.setNegativeButton(android.R.string.cancel, listener)
				.create();
		}
	}

}
