package com.mnishiguchi.android.movingestimator;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * A custom adapter designed specifically for the EstimateItems.
 */
public class EstimateListAdapter extends ArrayAdapter<EstimateItem>
{
	private static final String TAG = "movingestimator.EstimateListAdapter";
	
	private ArrayList<EstimateItem> mList;
	private Context mContext;

	/**
	 * Constructor.
	 * @param context The current context.
	 * @param resource The resource ID for a layout file containing a TextView to use when instantiating views.
	 * @param objects The objects to represent in the ListView.
	 */
	public EstimateListAdapter(Context context, int resId, ArrayList<EstimateItem> list)
	{
		// super constructor
		super(context, resId, list);
			
		// Remember all the elements of the passed-in list. 
		this.mList = list;
		this.mContext = context;
	}

	/** 
	 * A ViewHolder stores the TextViews of a list item.
	 * It can be attached to a View as a tag.
	 */
	private class ViewHolder
	{
		TextView name;
		TextView size;
		TextView quantity;
		TextView mode;
		TextView comment;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder = null;
		Log.v(TAG, String.valueOf(position));
			
		// If the recycled view is not provided, create a new one.
		if (convertView == null)
		{
			LayoutInflater inflator = (LayoutInflater)mContext.
					getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflator.inflate(R.layout.listitem_estimate, null);
				
			// Get references to the TextViews and store them in a VewHolder object.
			holder = new ViewHolder();
			holder.name = (TextView)convertView.findViewById(R.id.textViewListItemEstimateItemName);
			holder.size = (TextView)convertView.findViewById(R.id.textViewListItemEstimateSize);
			holder.quantity = (TextView)convertView.findViewById(R.id.textViewListItemEstimateQuantity);
			holder.mode = (TextView)convertView.findViewById(R.id.textViewListItemEstimateMode);
			holder.comment = (TextView)convertView.findViewById(R.id.textViewListItemEstimateComment);
			
			// Attach the ViewHolder to the view as a tag.
			convertView.setTag(holder);
		}
		else
		{
			// Get the ViewHolder object attached to the recycled view.
			holder = (ViewHolder)convertView.getTag();
		}
		
		EstimateItem item = mList.get(position);
		
		// Set the data text on each TextView.
		holder.name.setText(item.name);
		holder.size.setText("" + item.size);
		holder.quantity.setText("" + item.quantity);
		holder.mode.setText(item.mode);
		holder.comment.setText(item.comment);
			
		return convertView;
	}
}
