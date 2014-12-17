package com.mnishiguchi.android.movingestimator;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


public class CustomerListFragment extends ListFragment
{
	private static final String TAG = "movingestimator.CustomerListFragment";
	
	private static final String DIALOG_DELETE = "deleteDialog";
	private static final String DIALOG_ABOUT = "aboutDialog";
	private static final String DIALOG_PASSWORD = "passwordDialog";
	
	// For shared preferences
	private final static String PREFS = "prefs";
	private static final String PREF_PASSWORD = "password";
	
	// Reference to the list of customers stored in FileCabinet.
	private ArrayList<Customer> mCustomers;
	
	// Remember the currently selected item.
	@SuppressWarnings("unused")
	private int mPositionSelected;
	
	// remember the reference to the hosting activity for callbacks.
	private ListCallbacks mCallbacks;
		
	/**
	 * Required interface for hosting activities.
	 */
	public interface ListCallbacks
	{
		void onListItemClicked(Customer customer);
		void onListItemsDeleted(Customer[] customers);
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
			throw new ClassCastException(activity.toString()
					+ " must implement CustomerListFragment.Callbacks");
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
		super.onCreate(savedInstanceState);
		
		// Notify the FragmentManager that this fragment needs to receive options menu callbacks.
		setHasOptionsMenu(true);
	
		// Get the list of customers via the FileCabinet singleton.
		FileCabinet.get(getActivity()).registerForLoadingCustomers(this);
		mCustomers = FileCabinet.get(getActivity()).getCustomers();
		
		// Set the list adapter.
		CustomerListAdapter adapter = new CustomerListAdapter(mCustomers);
		setListAdapter(adapter);
		
		// Retain this fragment.
		setRetainInstance(true);
	}
	
	// Note:
	// ListFragments come with a default onCreateView() method.
	// The default implementation of a ListFragment inflates a layout that
	// defines a full screen ListView.
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState)
	{
		// Inflate a custom layout with list & empty.
		View v = inflater.inflate(R.layout.fragment_customerlist, parent, false);
		
		// For this app, android.R.id.list is defined in fragment_customerlist.xml.
		// Get a ListView object by using android.R.id.list resource ID
		// instead of getListView() because the layout view is not created yet.
		ListView listView = (ListView)v.findViewById(android.R.id.list);
		
		// --- Contexual Action Bar ---
		
		if (Utils.hasTwoPane(getActivity())) // Two-pane.
		{
			listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}
		else // Single-pane.
		{
			// Define responce to multi-choice.
			listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
			listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
				
				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu)
				{
					// Inflate the menu using a special inflater defined in the ActionMode class.
					MenuInflater inflater = mode.getMenuInflater();
					inflater.inflate(R.menu.context_customerlist_listitem, menu);
					
					return true;
				}
					
				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu)
				{
					// Remove unnecessay menu items.
					menu.findItem(R.id.contextmenu_edit).setVisible(false);
					menu.findItem(R.id.contextmenu_estimate).setVisible(false);
					return true;
				}
				
				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item)
				{
					switch (item.getItemId())
					{
						case R.id.contextmenu_delete:
							
							Log.d(TAG, "onActionItemClicked - contextmenu_delete");
							
							// Show Delete Confirmation dialog.
							DeleteDialog.newInstance(CustomerListFragment.this, getMultiSelectedListItems())
								.show(getActivity().getSupportFragmentManager(), DIALOG_DELETE);

							mode.finish(); // Action picked, so close the CAB
							return true;
						
						default:
							return false; // Return false if nothing is done
					}
				}
				
				@Override
				public void onDestroyActionMode(ActionMode mode)
				{
					// Required, but not used in this implementation.
				}

				@Override
				public void onItemCheckedStateChanged(ActionMode mode,
						int position, long id, boolean checked)
				{
					// Show the number of selected items on the CAB.
					mode.setTitle(getListView().getCheckedItemCount() + " selected");
				}
			});
		}
		
		// Activate the floating context menu, if in the two-pane mode.
		if (Utils.hasTwoPane(getActivity())) // Two-pane
		{
			registerForContextMenu (listView);
		}
		// Return the root view.
		return v;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		
		// Clear the current customer.
		Customer.setCurrentCustomer(null);
		
		// Reload the listView.
		((CustomerListAdapter)getListAdapter()).notifyDataSetChanged();
		
		// Clear the list.
		clearListSelection();
		mCallbacks.onListReset(); // Request removing the detailFragment.
	}
	
	/*
	 * Respond to a short click on a list item.
	 */
	@Override
	public void onListItemClick(ListView lv, View v, int position, long id)
	{
		// Get the selected item.
		Customer customer = ((CustomerListAdapter)getListAdapter()).getItem(position);
		
		// Remember the selected position
		mPositionSelected = position;
		
		// Remember the current customer.
		Customer.setCurrentCustomer(customer);
		
		// Set the action bar title.
		getActivity().setTitle(customer.toString());

		// Notify the hosting Activity.
		mCallbacks.onListItemClicked(customer);
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
		inflater.inflate(R.menu.fragment_customerlist, menu);
		
		// Get a reference to the subtitle menu item.
		//MenuItem menuSubtitle = menu.findItem(R.id.customerlist_menuitem_subtitle);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId() )
		{
			// --- NEW ---
			
			case R.id.optionsmenu_new:
				
				addNewCustomer();
				return true;  // No further processing is necessary.
			
			// --- ABOUT ---
				
			case R.id.optionsmenu_about:
					
				AboutDialog.newInstance().show(getFragmentManager(), DIALOG_ABOUT);
				return true;  // No further processing is necessary.
				
			// --- SETTINGS ---
					
			case R.id.optionsmenu_settings:

				new ChangePasswordDialog().show(getFragmentManager(), DIALOG_PASSWORD);
				return true; 
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	/*
	 * Floating Context Menu on list items.
	 */
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo )
	{
		getActivity().getMenuInflater().inflate(R.menu.context_customerlist_listitem, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		// Get the selected list position.
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		int position = info.position;
	
		// Get the selected list item.
		CustomerListAdapter adapter = (CustomerListAdapter)getListAdapter();
		SparseArray<Customer> selectedCustomers = new SparseArray<Customer>(1);
		selectedCustomers.put(position, adapter.getItem(position));
		
		// Remember the selected customer.
		Customer.setCurrentCustomer(selectedCustomers.get(position));
	
		// Get the selected menu item and respond to it.
		Intent i;
		switch (item.getItemId())
		{
			case R.id.contextmenu_edit:
				
				i = new Intent(getActivity(), CustomerEditActivity.class);
				i.putExtra(CustomerEditFragment.EXTRA_CUSTOMER_ID,
						selectedCustomers.get(position).getId());
				startActivity(i);
				return true; // No further processing is necessary.
				
			case R.id.contextmenu_estimate:
				
				if (Customer.getCurrentCustomer() != null)
				{
					i = new Intent(getActivity(), EstimateOverviewActivity.class);
					startActivity(i);
				}
				return true;
				
			case R.id.contextmenu_delete:
				
				// Show Delete Confirmation dialog.
				DeleteDialog.newInstance(CustomerListFragment.this, selectedCustomers)
					.show(getActivity().getSupportFragmentManager(), DIALOG_DELETE);
				
				return true;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * A custom ArrayAdapter designed to display Customer-specific list items.
	 */
	private class CustomerListAdapter extends ArrayAdapter<Customer>
	{
		/**
		 * Constructor. Create a listAdapter for the passed-in customers.
		 * @param crimes - An ArrayList of Customer objects to be displayed in the ListView.
		 */
		public CustomerListAdapter(ArrayList<Customer> customers)
		{
			// Super constructor. (args[1] => 0 when a pre-defined layout is not used.)
			super(getActivity(), 0, customers);
		}
		
		/** 
		 * A ViewHolder stores the TextViews of a list item.
		 * It can be attached to a View as a tag.
		 */
		private class ViewHolder
		{
			TextView name;
			TextView company;
		}
		
		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolder holder = null;
			//Log.v(TAG, "getView(), position=>" + String.valueOf(position));
			
			// If the convertView wasn't provided, inflate a new one. (Else recycle it)
			if (null == convertView)
			{
				convertView = getActivity().getLayoutInflater()
						.inflate(R.layout.listitem_customer, null);
				
				// Get references to the TextViews and store them in a VewHolder object.
				holder = new ViewHolder();
				holder.name = (TextView)convertView.findViewById(R.id.listitem_customer_customername);
				holder.company = (TextView)convertView.findViewById(R.id.listitem_customer_company);
				
				// Attach the ViewHolder to the view as a tag.
				convertView.setTag(holder);
			}
			else
			{
				// Get the ViewHolder object attached to the recycled view.
				holder = (ViewHolder)convertView.getTag();
			}
			
			// --- Configure the convertView for this particular Customer ---
			
			// Get the crime object in question.
			Customer customer = getItem(position);
			
			// Set the data text on each TextView.
			holder.name.setText(customer.toString());
			holder.company.setText(customer.getOrganization());
			
			return convertView;
		}
	}
	
	private void addNewCustomer()
	{
		// Create and add a new Customer object to the FileCabinet's list.
		Customer customer = new Customer();
		FileCabinet.get(getActivity()).addCustomer(customer) ;
		
		// Update the listView.
		((CustomerListAdapter)getListAdapter()).notifyDataSetChanged();
		
		Intent i = new Intent(getActivity(), CustomerEditActivity.class);
		
		if (customer.getId() != null)
		{
			i.putExtra(CustomerEditFragment.EXTRA_CUSTOMER_ID, customer.getId());
			//Log.e(TAG, "mCustomer.getId()=>" + customer.getId());
			startActivity(i);
		}
	}
	
	/**
	 * Update the listView's UI based on the updated list of the adapter.
	 */
	void updateListView()
	{
		//Log.d(TAG, "updateListView() - mCustomers.size(): " + mCustomers.size());
		((CustomerListAdapter) getListAdapter()).notifyDataSetChanged();
	}

	/**
	 * @return a map of Customer objects that are selected.
	 */
	private SparseArray<Customer> getMultiSelectedListItems()
	{
		int size = getListAdapter().getCount();
		SparseArray<Customer> map = new SparseArray<Customer>(size);
		
		// Iterate over the list items.
		for (int index = size - 1;
				index >= 0; index--)
		{
			// Check that  item is selected or not.
			if (getListView().isItemChecked(index))
			{
				// Add the selected items to the map.
				map.put(index, (Customer)getListAdapter().getItem(index));
			} 
		}
		return map;
	}
	
	/**
	 * Delete selected list items and update the list view.
	 * @param selectedItems
	 */
	private void deleteSelectedItems(SparseArray<Customer> selectedItems)
	{
		int size = selectedItems.size();
		
		// Create a list for callback.
		Customer[] customers = new Customer[size];
		
		// Do nothing if none is selected.
		if (selectedItems.size() <= 0) return;
		
		// Delete the selected items from the CrimeLab's list.
		for (int i = 0; i < size; i++)
		{
			deleteListItem(selectedItems.keyAt(i));
			customers[i] = selectedItems.valueAt(i);
		}
		
		// Notify the hosting activity.
		mCallbacks.onListItemsDeleted(customers);
	}
	
	@SuppressLint("NewApi")
	private void deleteListItem(int position)
	{
		final Customer clickedIitem = (Customer)getListAdapter().getItem(position);
		
		// Delete animation.
		final View view = getListAdapter().getView(position, null, getListView());
		view.animate()
			.setDuration(1000)
			.alpha (0)
			.withEndAction(new Runnable() {
				
				@Override
				public void run()
				{
					// Remove the data from model layer.
					FileCabinet.get(getActivity()).deleteCustomer(clickedIitem);
					
					// Update the listView.
					((BaseAdapter)getListAdapter()).notifyDataSetChanged();
					
					// Make the list item disappear.
					view.setAlpha(1);
					
					// Save the updated entire customers data to disk.
					FileCabinet.get(getActivity()).saveCustomers();
				}
			});
	}
	
	/**
	 * Set the last list item selected.
	 */
	void setLastItemSelected()
	{
		CustomerListAdapter adapter = (CustomerListAdapter)getListAdapter();
		int lastIndex = adapter.getCount() - 1;
		getListView().setItemChecked(lastIndex, true);
	}
	
	/**
	 * Set the last list item selected.
	 */
	void clearListSelection()
	{
		CustomerListAdapter adapter = (CustomerListAdapter)getListAdapter();
		getListView().clearChoices();
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * Show a confirmation message before actually deleting selected items.
	 */
	static class DeleteDialog extends DialogFragment
	{
		// Store the selected list item that was passed in.
		static Fragment sParentFragment;
		static SparseArray<Customer> sSelectedItems;
		
		/**
		 * Create a new instance that is capable of deleting the specified list items.
		 */
		static DeleteDialog newInstance(Fragment parentFragment, SparseArray<Customer> selectedItems)
		{
			// Store the selected items so that we can refer to it later.
			sParentFragment = parentFragment;
			sSelectedItems = selectedItems;
			
			// Create a fragment.
			DeleteDialog fragment = new DeleteDialog();
			fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
			
			return fragment;
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
							
							((CustomerListFragment)sParentFragment).deleteSelectedItems(sSelectedItems);
							break; 
							
						case DialogInterface.BUTTON_NEGATIVE: 
							// do nothing 
							break; 
					} 
				}
			};
			
			// Create and return a dialog.
			return new AlertDialog.Builder(getActivity())
				.setTitle(getActivity().getString(R.string.deleting) + " "
						+ sSelectedItems.size() + " "
						+ getActivity().getString(R.string.items))
				.setMessage(R.string.are_you_sure)
				.setPositiveButton(android.R.string.ok, listener)
				.setNegativeButton(android.R.string.cancel, listener)
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
	
	static class AboutDialog extends DialogFragment
	{
		TextView mTextView;
		
		static AboutDialog newInstance()
		{
			// Instantiate the fragment with the arguments.
			AboutDialog fragment = new AboutDialog();
			fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
			
			return fragment;
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
		{
			// Dynamically create an mTextView from scratch.
			mTextView = new TextView(getActivity());
			mTextView.setGravity(Gravity.CENTER_HORIZONTAL);

			String text = getActivity().getString(R.string.aboutdialog_text);
			
			// Set text on the TextView.
			mTextView.setText(text);
			mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
			
			return mTextView;
		}
		
		@Override
		public void onPause()
		{
			// Close the dialog as soon as the device orientation changes.
			dismiss();
			super.onPause();
		}
	}
	
	static class ChangePasswordDialog extends DialogFragment
	{
		private EditText mEtPassword;
		
		// For validation.
		private String mPassword;
		SharedPreferences prefs;
		
		/*
		 * Configure the dialog.
		 */
		@SuppressLint("InflateParams")
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			prefs = getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
			
			// Define the response to buttons.
			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
			{ 
				public void onClick(DialogInterface dialog, int which) 
				{ 
					switch (which) 
					{ 
						case DialogInterface.BUTTON_POSITIVE:
							
							mPassword = mEtPassword.getText().toString();
							
							// Save the password.
							prefs.edit()
								.putString(PREF_PASSWORD, mPassword)
								.commit();
							
							// Notify the user.
							Utils.showToast(getActivity(), 
									getActivity().getString(R.string.new_password)
									+ " " + prefs.getString(PREF_PASSWORD, ""));
							break; 
							
						case DialogInterface.BUTTON_NEGATIVE: 
							// do nothing 
							break; 
					} 
				}
			};
			
			// Inflate the dialog's root view.
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View v = inflater.inflate(R.layout.dialog_change_password, null);
			
			// Get a reference to the user input.
			mEtPassword = (EditText)v.findViewById(R.id.EditTextPassword);
			
			String currentPassword = getActivity()
					.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
					.getString(PREF_PASSWORD, "");
			
			// Create and return a dialog.
			return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.change_password)
				.setView(v)
				.setMessage(getActivity().getString(R.string.currently) + " " + currentPassword)
				.setPositiveButton(R.string.change, listener)
				.setNegativeButton(android.R.string.cancel, listener)
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
