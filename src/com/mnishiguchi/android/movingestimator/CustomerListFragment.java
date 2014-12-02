package com.mnishiguchi.android.movingestimator;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class CustomerListFragment extends ListFragment
{
	private static final String TAG = "movingestimatort.CustomerListFragment";
	
	private static final String DIALOG_DELETE = "delete";
	
	// Store reference to the current instance to this fragment.
	private static CustomerListFragment sCustomerListFragment;
	
	// Reference to the list of customers stored in FileCabinet.
	private ArrayList<Customer> mCustomers;
	
	// The state of the Action Bar's subtitle.
	private boolean mSubtitleVisible;
	
	// Remember the currently selected item.
	private int mPositionSelected;
	
	// remember the reference to the hosting activity for callbacks.
	private ListCallbacks mCallbacks;
		
	/**
	 * Required interface for hosting activities.
	 */
	public interface ListCallbacks
	{
		void onListItemClicked(Customer customer);
		void onListReset();
		void onListItemsDeleted(Customer[] selectedCustomers);
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
		Log.d(TAG, "onCreate");
		
		super.onCreate(savedInstanceState);
		
		// Store a reference to this instance.
		sCustomerListFragment = this;
	
		// Notify the FragmentManager that this fragment needs to receive options menu callbacks.
		setHasOptionsMenu(true);
		
		// Set the action-bar title.
		getActivity().setTitle(R.string.actionbar_title_list);
	
		// Get the list of customers via the FileCabinet singleton.
		mCustomers = FileCabinet.get(getActivity()).getCustomers();
		
		// Set the list adapter.
		CustomerListAdapter adapter = new CustomerListAdapter(mCustomers);
		setListAdapter(adapter);
		
		// Retain this fragment.
		setRetainInstance(true);
		
		// The action bar's subtitle initially hidden.
		mSubtitleVisible = false;
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
		
		// Note:
		// For this app, android.R.id.list is defined in fragment_customerlist.xml.
		// Get a ListView object by using android.R.id.list resource ID
		// instead of getListView() because the layout view is not created yet.
		
		ListView listView = (ListView)v.findViewById(android.R.id.list);
		
		// If mSubtitleVisible == true, then set the subtitle.
		//if (mSubtitleVisible)
		//{
			// temp subtitle.
			//getActivity().getActionBar().setSubtitle(R.string.actionbar_subtitle);
		//}
		
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
					
					// Call back.
					// TODO - mCallbacks.onActionMode();
					return true;
				}
					
				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu)
				{
					return false; // Return false if nothing is done
				}
				
				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item)
				{
					switch (item.getItemId())
					{
						case R.id.contextmenu_delete:
							
							Log.d(TAG, "onActionItemClicked - contextmenu_delete");
							// Show Delete Confirmation dialog.
							DeleteDialog.newInstance(getSelectedItems())
								.show(getActivity().getSupportFragmentManager(), DIALOG_DELETE);

							mode.finish(); // Action picked, so close the CAB
							return true;
						
						default:
							return false;
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
		
		return v;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		// Reload the list.
		((CustomerListAdapter)getListAdapter()).notifyDataSetChanged();
		
		if (!Utils.hasTwoPane(getActivity())) // Single=pane
		{
			clearListSelection();
		}
		
		// If no list item is selected, don't show the detail.
		if (getSelectedItems().length <= 0)
		{
			mCallbacks.onListReset();
		}
	}
	
	/**
	 * Creates the options menu and populates it with the items defined
	 * in res/menu/fragment_customerlist.xml.
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
	
	/* 
	 * Responds to menu selection.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId() )
		{
			// --- NEW ---
			
			case R.id.customerlist_menuitem_new:
				
				addNewCustomer();
				
				return true;  // No further processing is necessary.
			
			// --- Show/Hide subtitle ---
			
			//case R.id.customerlist_menuitem_subtitle:
				//return true;  // No further processing is necessary.
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	/* Respond to a short click on a list item. */
	@Override
	public void onListItemClick(ListView lv, View v, int position, long id)
	{
		// Get the selected item.
		Customer customer = ((CustomerListAdapter) getListAdapter()).getItem(position);
		
		// Remember the selected position
		mPositionSelected = position;
		
		// Update the action bar title.
		// setActionBarTitle(customer);
		
		// Notify the hosting Activity.
		mCallbacks.onListItemClicked(customer);
	}
	
	/**
	 * Set the customer name as a title and the company name as a subtitle.
	 */
	private void setActionBarTitle(Customer customer)
	{
		getActivity().setTitle(customer.toString());
		
		// If mSubtitleVisible == true, then set the subtitle.
		if (mSubtitleVisible)
		{
			getActivity().getActionBar().setSubtitle(customer.getOrganization());
		}
		
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
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			// If the convertView wasn't provided, inflate a new one. (Else recycle it)
			if (null == convertView)
			{
				convertView = getActivity().getLayoutInflater()
						.inflate(R.layout.listitem_customer, null);
			}
			
			// --- Configure the convertView for this particular Customer ---
			
			// Get the crime object in question.
			Customer customer = getItem(position);
			
			// TODO - Implement with a ViewHolder.
			
			TextView customerName = (TextView)
					convertView.findViewById(R.id.listitem_customer_customername);
			customerName.setText(customer.toString());
			TextView company = (TextView)
					convertView.findViewById(R.id.listitem_customer_company);
			company.setText(customer.getOrganization());
			
			return convertView;
		}
	}
	
	private void addNewCustomer()
	{
		// Create and add a new Customer object to the FileCabinet's list.
		Customer customer = new Customer();
		FileCabinet.get(getActivity() ).addCustomer(customer) ;
		
		// Update the listView.
		((CustomerListAdapter)getListAdapter()).notifyDataSetChanged();
		
		// For tablets only.
		if (Utils.hasTwoPane(getActivity()))
		{
			// Update the selection.
			setLastItemSelected();
			
			// Clear the action bar title.
			getActivity().setTitle("");
		}
		
		// callback
		mCallbacks.onListItemClicked(customer);
	}
	
	/**
	 * Update the listView's UI based on the updated list of the adapter.
	 */
	void updateListView()
	{		
		((CustomerListAdapter) getListAdapter()).notifyDataSetChanged();
	}
	
	/**
	 * @return an array of Customer objects that are selected.
	 */
	private Customer[] getSelectedItems()
	{
		CustomerListAdapter adapter = (CustomerListAdapter)getListAdapter();
		ArrayList<Customer> list = new ArrayList<Customer>(adapter.getCount());
		
		// Iterate over the list items.
		for (int index = adapter.getCount() - 1;
				index >= 0; index--)
		{
			// Check that  item is selected or not.
			if (getListView().isItemChecked(index))
			{
				// Add the selected items to list.
				list.add(adapter.getItem(index));
			} 
		}
		
		// Get the size of the result.
		int resultSize = list.size();
		
		// Convert the ArrayList to an array.
		Customer[] result = new Customer[resultSize];
		result = list.toArray(result);
		
		return result;
	}

	/**
	 * Delete selected list items and update the list view.
	 * @param selectedItems
	 * @return the number of items deleted.
	 */
	private int deleteSelectedItems(Customer[] selectedItems)
	{
		CustomerListAdapter adapter = (CustomerListAdapter)getListAdapter();
		FileCabinet fileCabinet = FileCabinet.get(getActivity());
		int count = 0;
		
		// Delete the selected items from the CrimeLab's list.
		for (Customer each : selectedItems)
		{
			fileCabinet.deleteCustomer(each);
			count += 1;
		}
		
		// Update the ListView.
		adapter.notifyDataSetChanged();
		
		// Call back.
		mCallbacks.onListItemsDeleted(selectedItems);
		
		// Notify the user about the result.
		Utils.showToast(getActivity(), count + " deleted");
		return count;
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
		static Customer[] sSelectedItems;
		static int sCount;
		
		/**
		 * Create a new instance that is capable of deleting the specified list items.
		 */
		static DeleteDialog newInstance(Customer[] selectedItems)
		{
			// Store the selected items so that we can refer to it later.
			sSelectedItems = selectedItems;
			sCount = selectedItems.length;
			
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
							
							sCustomerListFragment.deleteSelectedItems(sSelectedItems);
							break; 
							
						case DialogInterface.BUTTON_NEGATIVE: 
							// do nothing 
							break; 
					} 
				}
			};
			
			// Create and return a dialog.
			return new AlertDialog.Builder(getActivity())
				.setTitle("Deleting " + sCount + " item(s)")
				.setMessage("Are you sure?")
				.setPositiveButton("Yes", listener)
				.setNegativeButton("Cancel", listener)
				.create();
		}
	}
}
